package com.zeekrlife.market.property

import android.net.Uri
import android.util.Log
import com.tencent.mmkv.MMKV
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.market.provider.AppPropertiesProvider
import com.zeekrlife.net.interception.logging.util.logE
import org.json.JSONException

/**
 * 应用属性
 * 是否支持属性：0-不支持，1-支持
 */
abstract class AppProperty {

    /**
     * 属性名称
     */
    abstract val propertyName: String

    private var propertiesCache: MMKV? = null

    /**
     * 作为当前属性存储方案
     */
    protected fun getPropertiesCache(): MMKV {
        if (MMKV.getRootDir() == null) {
            MMKV.initialize(appContext)
        }
        if(propertiesCache == null) {
            propertiesCache = MMKV.mmkvWithID("app_property_${propertyName}")
        }
        return propertiesCache!!
    }

    /**
     * 属性key：默认规则实现
     * @param packageName
     * @param versionCode
     */
    open fun propertyKey(packageName: String, versionCode: Long) = "${packageName}-${versionCode}"

    /**
     * 属性发生变化
     * @param packageName
     * @param versionCode
     * @param propertyValue
     */
    fun propertyChange(packageName: String, versionCode: Long, propertyValue: Int) {
        try {
            val propertyKey = propertyKey(packageName, versionCode)
            val value = getPropertiesCache().getInt(propertyKey, -1)

            //是否变更
            var isChange = false
            var curdType = -1

            if (value == -1 && propertyValue != -1) {
                //新增
                curdType = AppPropertiesProvider.TYPE_INSERT
                isChange = true
                getPropertiesCache().putInt(propertyKey, propertyValue)
                onPropertyAdd(propertyValue)
            } else if (value != -1 && value != propertyValue) {
                //更新
                curdType = AppPropertiesProvider.TYPE_UPDATE
                isChange = true
                getPropertiesCache().putInt(propertyKey, propertyValue)
                onPropertyUpdate(propertyValue)
            } else if (value != -1 && propertyValue == -1) {
                //删除
                curdType = AppPropertiesProvider.TYPE_DELETE
                isChange = true
                getPropertiesCache().remove(propertyKey)
                onPropertyDelete(propertyValue)
            }

            if (isChange) {
                //通过ContentResolver Notify
                if (isContentResolverNotify()) {
                    AppPropertiesProvider.buildUri(
                        curdType, propertyName, packageName, versionCode.toString(), propertyValue.toString()
                    ).apply {
                        notifyPropertyChange(this)
                    }
                }
                onPropertyChange(packageName, versionCode, value, propertyValue)
            }
        } catch (e: IllegalStateException) {
            Log.e("AppProperty", "processPropertyChange exception : Illegal state exception")
            e.logStackTrace()
        } catch (e: JSONException) {
            Log.e("AppProperty", "processPropertyChange exception : JSON exception")
            e.logStackTrace()
        } catch (e: NullPointerException) {
            Log.e("AppProperty", "processPropertyChange exception : Null pointer exception")
            e.logStackTrace()
        }
    }

    /**
     * 属性添加
     */
    open fun onPropertyAdd(propertyValue: Int) {}

    /**
     * 属性值更新
     */
    open fun onPropertyUpdate(propertyValue: Int) {}

    /**
     * 属性删除
     */
    open fun onPropertyDelete(propertyValue: Int) {}

    /**
     * 属性值发生变化
     * @param oldValue 旧值
     * @param newValue 新值
     */
    open fun onPropertyChange(packageName: String, versionCode: Long, oldValue: Int, newValue: Int) {}

    /**
     * 通过属性key，获取属性值
     * @param packageName
     * @param versionCode
     */
    open fun getPropertyValue(packageName: String, versionCode: Long): Int {
        try {
            return getPropertiesCache().getInt(propertyKey(packageName, versionCode), -1)
        } catch (e: IllegalStateException) {
            Log.e("AppProperty", "getPropertyValue exception : Illegal state exception")
            e.logStackTrace()
        } catch (e: JSONException) {
            Log.e("AppProperty", "getPropertyValue exception : JSON exception")
            e.logStackTrace()
        } catch (e: NullPointerException) {
            Log.e("AppProperty", "getPropertyValue exception : Null pointer exception")
            e.logStackTrace()
        }
        return -1
    }

    /**
     * 属性清空
     */
    fun propertyClear() {
        try {
            propertiesCache?.clear()
            onPropertyClear()
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 当属性值清除
     */
    open fun onPropertyClear() {}

    /**
     *是否通过contentResolver通知属性改变
     *新增:content://${authority}/add/${packageName}/${versionCode}/(0/1)
     *更新:content://${authority}/update/${packageName}/${versionCode}/(0/1)
     *删除content://${authority}/delete/${packageName}/${versionCode}/(0/1)
     */
    open fun isContentResolverNotify() = false

    /**
     * 通过Resolver通知属性改变
     */
    protected fun notifyPropertyChange(uri: Uri) {
        try {
            appContext.contentResolver?.notifyChange(uri.buildUpon().clearQuery().build(), null)
            "notifyPropertyChange uri:$uri".logE("AppProperty")
        } catch (e: SecurityException) {
            Log.e("AppProperty", "notifyChange exception : Security exception")
            e.logStackTrace()
        } catch (e: IllegalArgumentException) {
            Log.e("AppProperty", "notifyChange exception : Illegal argument exception")
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            Log.e("AppProperty", "notifyChange exception : Illegal state exception")
            e.logStackTrace()
        }
    }
}