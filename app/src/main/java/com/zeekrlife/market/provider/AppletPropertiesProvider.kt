package com.zeekrlife.market.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.zeekr.basic.appContext
import com.zeekrlife.ampe.aidl.AppletInfo
import com.zeekrlife.ampe.core.database.AromeShortcutBean
import com.zeekrlife.ampe.core.database.AromeShortcutDB
import com.zeekrlife.ampe.core.database.AromeShortcutDao
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.market.ui.activity.AromeActivity
import com.zeekrlife.market.utils.applet.AppletUtils
import com.zeekrlife.market.utils.applet.AppletResultCallBack
import com.zeekrlife.net.interception.logging.util.logE


class AppletPropertiesProvider : ContentProvider() {

    companion object {
        const val TAG = "zzzAppletPropertiesProvider"
        const val AUTHORITY = "com.zeekrlife.market.AppletPropertiesProvider"
        const val URI_APPLET_TABLE_NAME = "arome_shortcut"
        const val URI_APPLET_SPEECH_RESULT = "speech"


        fun appletShortcutNotifyChange(context: Context) {
            val  authority = "${context.packageName}.AppletPropertiesProvider"
            val uri = Uri.parse("content://${authority}/query/all")
            context.contentResolver.notifyChange(uri, null)
        }
        //语音使用 Uri.parse("content://com.zeekrlife.market.AppletPropertiesProvider/speech/$appletId/$result")
        fun appletStartResultNotifyChange(context: Context,appletId:String,result:Int) {
            val  authority = "${context.packageName}.AppletPropertiesProvider"
            val uri = Uri.parse("content://${authority}/$URI_APPLET_SPEECH_RESULT/$appletId/$result")
            context.contentResolver.notifyChange(uri, null)
        }
    }

    private val matcher = UriMatcher(UriMatcher.NO_MATCH)

    private val CODE_QUERY_PROPERTY_ALL = 1
    private val CODE_QUERY_PROPERTY_ID = 2


    override fun onCreate(): Boolean {
        val  authority = "${context?.packageName}.AppletPropertiesProvider"
        //查询所有小程序
        matcher.addURI(authority, "query/all", CODE_QUERY_PROPERTY_ALL)
        //查询指定小程序：content://${authority}/query/${appletId}
        matcher.addURI(authority, "query/*", CODE_QUERY_PROPERTY_ID)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code: Int = matcher.match(uri)
        if (code == CODE_QUERY_PROPERTY_ALL || code == CODE_QUERY_PROPERTY_ID) {
            val context = context ?: return null
            val modelDao: AromeShortcutDao = AromeShortcutDB.getInstance(context).getAromeShortcutDao()
            var cursor: Cursor? = null
            cursor = if (code == CODE_QUERY_PROPERTY_ALL) {
                modelDao.queryAromeShortcutList()
            } else {
                modelDao.queryAromeShortcut(ContentUris.parseId(uri))
            }
            return cursor
        } else {
            return null
        }
    }

    /**
     * 获取URI对应的MIME类型
     */
    override fun getType(uri: Uri): String {
        val  authority = "${context?.packageName}.AppletPropertiesProvider"
        return when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> "vnd.android.cursor.dir/$authority.$URI_APPLET_TABLE_NAME"
            CODE_QUERY_PROPERTY_ID -> "vnd.android.cursor.item/$authority.$URI_APPLET_TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    /**
     * 插入数据
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> {
                val context = context ?: return null
                val id: Long = AromeShortcutDB.getInstance(context)
                    .getAromeShortcutDao()
                    .insertAromeShortcut(AromeShortcutDB.fromContentValues(values))
                if (id <= 0) {
                    return null
                }
                appletShortcutNotifyChange(context)
                ContentUris.withAppendedId(uri, id)
            }
            CODE_QUERY_PROPERTY_ID -> throw java.lang.IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    /**
     * 删除数据
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> {
                0
            }
            CODE_QUERY_PROPERTY_ID -> {
                val context = context ?: return 0
                val count: Int = AromeShortcutDB.getInstance(context).getAromeShortcutDao()
                    .deleteById(ContentUris.parseId(uri))
                appletShortcutNotifyChange(context)
                count
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    /**
     * 更新数据
     */
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> {
                throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            }
            CODE_QUERY_PROPERTY_ID -> {
                val context = context ?: return 0
                val aromeShortcutBean: AromeShortcutBean = AromeShortcutDB.fromContentValues(values)
                aromeShortcutBean.id = ContentUris.parseId(uri)
                val count: Int = AromeShortcutDB.getInstance(context).getAromeShortcutDao()
                    .updateAromeShortcut(aromeShortcutBean)
                appletShortcutNotifyChange(context)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    /**
     * 响应调用, 用于处理跨进程调用
     */
    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        var result = Bundle()
        try {
            when (method) {
                "startApplet" -> {
                    startAppletProcess(arg?:"")
                }
                "startAppletBySpeech" -> {
                    startAppletProcessBySpeech(arg?:"", callBack = {
                        result =  setCallResult(arg?:"",it)
                    })
                }

                "startAppletByCustomScene" -> {
                    startCustomScene(arg?:"", callBack = {
//                        result =  setCallResult(arg?:"",it)
                    })
                }

                "exitApplet" -> {
                    exitAppletProcess()
                }
            }
            return result
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return super.call(method, arg, extras)
    }

    // 设置回调结果的方法
    private fun setCallResult(appletId:String,info:AppletInfo): Bundle{
        val result = Bundle().apply {
            putBoolean("success", info.success)
            putInt("code", info.code)
            putString("message", info.message)
        }
        appletStartResultNotifyChange(appContext,appletId,if(info.success) 1 else 0)
        return result
    }



    /**
     *  launcher引入示例
     *  查询所有桌面小程序
     */
    private fun getAllApplet() {
        var appletList : ArrayList<AromeShortcutBean> = arrayListOf()
        var cursor: Cursor? = null
        try {
            val uri = Uri.parse("content://com.zeekrlife.market.AppletPropertiesProvider/query/all")
            context?.contentResolver?.registerContentObserver(uri,true,object : ContentObserver(
                Handler(Looper.getMainLooper())
            ) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    super.onChange(selfChange, uri)
                }
            })
            cursor = context?.contentResolver?.query(uri, null, null, null, null)
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
            Log.e("AppletPropertiesProvider", "getAllApplet: ${appletList.map { it.name }}")
        } catch (e: Exception) {
            e.logStackTrace()
        } finally {
            cursor?.close()
        }
    }

    /**
     *  桌面添加小程序
     */
    private fun handleInsertApplet(contentValues: ContentValues?) {
        val uri = Uri.parse("content://com.zeekrlife.market.AppletPropertiesProvider/query/all")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.contentResolver?.insert(uri, contentValues,null)
        }

    }

    /**
     *  桌面移除小程序
     */
    private fun handleDeleteApplet(appletId:String) {
        val uri = Uri.parse("content://com.zeekrlife.market.AppletPropertiesProvider/query/$appletId")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.contentResolver?.delete(uri,null)
        }
    }

    /**
     * 外部关闭小程序
     */
    private fun handleExitApplet() {
        val uri = Uri.parse("content://com.zeekrlife.market.AppletPropertiesProvider/query/0")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
           context?.contentResolver?.call(uri, "exitApplet", null, null)
        }
    }

    /**
     *  应用中心启动小程序
     */
    private fun startAppletProcess(appletId: String, callBack: ((info: AppletInfo) -> Unit)? = null) {
//        AppletResultCallBack.setAppletResultListener(object : AppletResultCallBack.AppletResultListener{
//            override fun onResult(result: AppletInfo) {
//                callBack?.invoke(result)
//                "appletResultEvent:${result.success}".logE("zzzappletResultEvent")
//            }
//
//        })

        val bundle = Bundle().apply {
            putString("appletId",appletId)
            putBoolean("startApplet",true)
        }
        toStartActivity(AromeActivity::class.java,bundle)
    }

    /**
     * 语音启动小程序
     */
    private fun startAppletProcessBySpeech(appletId: String, callBack: ((info: AppletInfo) -> Unit)? = null) {
        AppletResultCallBack.setAppletResultListener(object : AppletResultCallBack.AppletResultListener{
            override fun onResult(result: AppletInfo) {
                callBack?.invoke(result)
                "appletResultEvent:${result.success}".logE("zzzappletResultEvent")
            }

        })

        val bundle = Bundle().apply {
            putString("appletId",appletId)
            putBoolean("startApplet",true)
        }
        toStartActivity(AromeActivity::class.java,bundle)
    }

    /**
     *  卡片智能场景启动小程序
     */
    private fun startCustomScene(serviceCode: String, callBack: ((info: AppletInfo) -> Unit)? = null) {
//        AppletResultCallBack.setAppletResultListener(object : AppletResultCallBack.AppletResultListener{
//            override fun onResult(result: AppletInfo) {
//                callBack?.invoke(result)
//                "appletResultEvent:${result.success}".logE("zzzappletResultEvent")
//            }
//
//        })
        "AppletPropertiesProvider startCustomScene serviceCode:$serviceCode".logE(TAG)
        val bundle = Bundle().apply {
            putString("serviceCode",serviceCode)
            putBoolean("customScene",true)
        }
        toStartActivity(AromeActivity::class.java,bundle)
    }

    /**
     * 关闭小程序
     */
    private fun exitAppletProcess() {
        AppletUtils.exitApplet()
    }



    /**
     *  语音启动小程序
     *
     * content//:com.zeekrlife.market.AppletPropertiesProvider/speech/1		//成功
     *
     * content//:com.zeekrlife.market.AppletPropertiesProvider/speech/0           //失败
     */
    private fun handleStartApplet(appletId:String) {
        val uri = Uri.parse("content://com.zeekrlife.market.AppletPropertiesProvider")

        context?.contentResolver?.registerContentObserver(uri,true,object : ContentObserver(
            Handler(Looper.getMainLooper())
        ) { override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
            if(uri?.toString()?.contains("speech") == true){
                    //语音逻辑
                }

            }})
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val callBack = context?.contentResolver?.call(uri, "startAppletBySpeech", appletId, null)

            }
        }

    /**
     * 场景卡片打开小程序场景
     */
    private fun handleStartAppletCustomScene(serviceCode:String) {
        val uri = Uri.parse("content://com.zeekrlife.market.AppletPropertiesProvider")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.contentResolver?.call(uri, "startAppletByCustomScene",serviceCode, null)
        }
    }

}