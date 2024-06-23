package com.zeekrlife.market.property

import android.util.Log
import com.google.gson.reflect.TypeToken
import com.zeekr.basic.appContext
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.market.provider.AppPropertiesProvider
import org.json.JSONException
import java.io.IOException
import java.io.InputStream

/**
 * @author Lei.Chen29
 * @date 2023/3/29 9:49
 * description：行车限制属性
 */
abstract class NonPGearLimitProperty : AppProperty() {

    abstract fun getDefaultForbidJsonData(): String

    private var defaultForbidList: List<String>? = null

    override fun getPropertyValue(packageName: String, versionCode: Long): Int {
        val value = super.getPropertyValue(packageName, versionCode)
        var stream: InputStream? = null
        try {
            //-1表示未定义
            if (value == -1) {
                if (defaultForbidList == null) {
                    val nonPGearLimitJson = getDefaultForbidJsonData()
                    Log.e(propertyName, "nonPGearLimitJson : $nonPGearLimitJson")
                    defaultForbidList = if (nonPGearLimitJson.isNotEmpty()) {
                        GsonUtils.fromJson<List<String>>(nonPGearLimitJson, object : TypeToken<List<String>>() {}.type)
                    } else {
                        arrayListOf()
                    }
                }

                if (defaultForbidList?.contains(packageName) == true) {
                    return 0
                }
            }
        } catch (e: JSONException) {
            Log.e("getPropertyValue", "getDefaultForbidJsonData exception : JSON exception")
            e.logStackTrace()
        } catch (e: IOException) {
            Log.e("getPropertyValue", "getDefaultForbidJsonData exception : IO exception")
            e.logStackTrace()
        } finally {
            try {
                stream?.close()
            } catch (e: Exception) {
                e.logStackTrace()
            }
        }
        return value
    }

    override fun isContentResolverNotify() = true

    override fun onPropertyClear() {
        notifyPropertyChange(AppPropertiesProvider.getUriBuilder("clear", propertyName, "").build())
    }
}