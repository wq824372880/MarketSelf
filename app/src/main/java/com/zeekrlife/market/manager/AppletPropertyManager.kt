package com.zeekrlife.market.manager

import android.database.ContentObserver
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.zeekr.basic.appContext
import com.zeekrlife.ampe.core.AppletServiceImpl
import com.zeekrlife.ampe.core.database.AromeShortcutBean
import com.zeekrlife.ampe.core.database.AromeShortcutDB
import com.zeekrlife.ampe.core.database.AromeShortcutDao
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.msg
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.imagetransform.ImageUtils
import com.zeekrlife.common.util.imagetransform.ImageUtils.ImgLoadListener
import com.zeekrlife.market.data.cache.CacheExt
import com.zeekrlife.market.data.repository.UserRepository
import com.zeekrlife.market.data.response.AppletSignatureInfo
import com.zeekrlife.market.provider.AppletPropertiesProvider
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashSet

/**
 * @author mac
 * @date 2022/12/1 18:20
 * description：TODO
 */
object AppletPropertyManager {

    private val aromeShortcutDao: AromeShortcutDao by lazy {
        AromeShortcutDB.getInstance(appContext).getAromeShortcutDao()
    }

    /**
     * 获取设备签名信息。
     * 此函数首先尝试从缓存中获取应用签名信息，如果缓存中存在且不为空，则直接通过回调函数返回。
     * 如果缓存中不存在或为空，则异步从服务器获取签名信息，成功后存储到缓存并返回。
     *
     * @param callbackSuccess 获取签名信息成功时的回调函数，接收一个 [AppletSignatureInfo] 类型的参数。
     * @param callbackFail 获取签名信息失败时的回调函数，接收一个 [String] 类型的错误信息参数。
     */
    fun getDeviceSignature(callbackSuccess: ((str: AppletSignatureInfo) -> Unit) = {}, callbackFail: ((str: String) -> Unit) = {}) {
        // 尝试从缓存获取应用的设备签名信息
        CacheExt.getAppletDeviceSignature().takeIf {
            !(it?.sign.isNullOrEmpty())
        }?.run {
            // 缓存中存在有效的签名信息，调用成功回调
            callbackSuccess.invoke(this)
        } ?: kotlin.run {
            // 缓存中不存在或签名信息为空，从服务器异步获取
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    // 尝试从服务器获取签名信息
                    kotlin.runCatching {
                        UserRepository.getAppletDeviceSignature().await()
                    }.onSuccess {
                        // 获取成功，存储到缓存并调用成功回调
                        CacheExt.setAppletDeviceSignature(it)
                        callbackSuccess.invoke(it)
                    }.onFailure {
                        // 获取失败，记录错误信息并调用失败回调
                        "getAppletDeviceSignature：${it}".logE("zzzAppletPropertyManager")
                        callbackFail.invoke(it.msg)
                    }
                }
            }
        }
    }

    /**
     * 获取合法的应用小程序ID集合。
     * 此函数首先尝试使用已有的合法ID集合，如果不存在或为空，则通过异步方式从用户仓库获取ID集合，
     * 并将结果回调给调用者。如果获取失败，则尝试从缓存中获取之前的合法ID集合并回调。
     *
     * @param callback 一个函数，当获取到合法的应用小程序ID集合后会被调用。传入参数为一个包含所有合法ID的HashSet。
     */
    fun getLegalAppletIdSet(callback: ((appletSet: HashSet<String>) -> Unit) = {}) {
        // 尝试使用已有合法ID集合，若非空则直接回调
        AppletServiceImpl.legalAppletIdSet.takeIf {
            it.size > 0
        }?.run {
            callback.invoke(this)
        } ?: kotlin.run {
            // 使用协程在后台异步获取ID集合
            MainScope().launch {
                // 尝试从用户仓库获取ID集合
                kotlin.runCatching {
                    UserRepository.getAppletIdSet().await()
                }.onSuccess {
                    // 如果获取到的ID集合非空，更新本地合法ID集合并缓存，然后回调
                    if (it.size > 0) {
                        AppletServiceImpl.legalAppletIdSet = it.toHashSet()
                        CacheExt.setAppletLegalSet(AppletServiceImpl.legalAppletIdSet)
                        callback.invoke(AppletServiceImpl.legalAppletIdSet)
                    } else {
                        // 如果获取的ID集合为空，尝试从缓存中获取并回调
                        CacheExt.getAppletLegalSet()?.let { appletLegalSet ->
                            callback.invoke(appletLegalSet.toHashSet())
                        }
                    }
                }.onFailure {
                    // 如果从用户仓库获取ID集合失败，尝试从缓存中获取并回调
                    CacheExt.getAppletLegalSet()?.let { appletLegalSet ->
                        callback.invoke(appletLegalSet.toHashSet())
                    }
                }
            }
        }
    }

    /**
     * 添加小程序到桌面
     */
    fun addShortcut(id: Long, name: String?, slogan: String?, appletUrl: String?, resourceId: Int) {
        if (id <= 0 || appletUrl.isNullOrEmpty()) {
            ToastUtils.show("该小程序不支持添加至桌面!")
            return
        }
        MainScope().launch {
            ImageUtils.url2ByteArray(appContext, appletUrl, resourceId, object : ImgLoadListener {
                override fun onImgLoadSuccess(byteArray: ByteArray) {
                    val shortcutBean = AromeShortcutBean().apply {
                        this.id = id
                        this.name = name
                        this.slogan = slogan
                        this.appletUrl = appletUrl
                        this.appletByteArray = byteArray
                    }
                    aromeShortcutDao.insertAromeShortcut(shortcutBean)
                    AppletPropertiesProvider.appletShortcutNotifyChange(appContext)
                }

            })
        }

    }

    /**
     * 从桌面移除此小程序
     */
    fun removeShortcut(id: Long) {
        if (id <= 0) return
        aromeShortcutDao.deleteById(id)
        AppletPropertiesProvider.appletShortcutNotifyChange(appContext)

    }

    /**
     * 查询小程序是否已添加到桌面
     */
    fun shortcutExist(id: Long): Boolean {
        var isAdd = false
        try {
            val shortcutCursor = aromeShortcutDao.queryAromeShortcut(id)
            while (shortcutCursor?.moveToNext() == true) {
                isAdd = true
            }
            shortcutCursor?.close()
        } catch (e: SQLiteException) {
            // 处理数据库操作异常的逻辑
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理 IllegalStateException 的逻辑
            e.logStackTrace()
        } catch (e: Exception) {
            // 捕获其他未特别处理的异常
            e.logStackTrace()
        }
        return isAdd
    }

    /**
     *  launcher引入示例
     *  查询所有桌面小程序
     */
     fun getAllApplet() {
        val appletList : ArrayList<AromeShortcutBean> = arrayListOf()
        var cursor: Cursor? = null
        try {
            val uri = Uri.parse("content://com.zeekrlife.market.AppletPropertiesProvider/query/all")
            appContext.contentResolver?.registerContentObserver(uri,true,object : ContentObserver(
                Handler(Looper.getMainLooper())
            ) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    super.onChange(selfChange, uri)
                }
            })
            cursor = appContext.contentResolver?.query(uri, null, null, null, null)
            while (cursor?.moveToNext() == true) {
                val aromeShortcutBean = AromeShortcutBean()
                cursor.columnNames.forEach {
                    val columnIndex = cursor.getColumnIndex(it)
                    when (it) {
                        "id" -> aromeShortcutBean.id = cursor.getLong(columnIndex)
                        "name" -> aromeShortcutBean.name = cursor.getString(columnIndex)
                        "slogan" ->  aromeShortcutBean.slogan = cursor.getString(columnIndex)
                        "appletUrl" -> aromeShortcutBean.appletUrl = cursor.getString(columnIndex)
                        "appletByteArray"->aromeShortcutBean.appletByteArray = cursor.getBlob(columnIndex)
                    }
                }
                appletList.add(aromeShortcutBean)
            }
            Log.e("AppletPropertiesProvider::", "getAllApplet: ${appletList.map { it.name }}")
        } catch (e: Exception) {
            e.logStackTrace()
        } finally {
            cursor?.close()
        }
    }

}