package com.zeekrlife.market.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.common.util.ApkUtils
import com.zeekrlife.market.manager.AppPropertyManager

/**
 * @author Lei.Chen29
 * @date 2022/5/20 14:27
 * description：
 * 三方应用配置查询提供者，目前可供查询的属性如下：
 */
class AppPropertiesProvider : ContentProvider() {

    companion object {

        private const val SCHEME = "content"

        private lateinit var authority: String

        const val TYPE_QUERY = 1

        const val TYPE_INSERT = 2

        const val TYPE_UPDATE = 3

        const val TYPE_DELETE = 4

        /**
         * @param type
         * @param property
         * @param pathParams
         */
        fun buildUri(type: Int, property: String, vararg pathParams: String): Uri = getUriBuilder(
            when (type) {
                TYPE_QUERY -> "query"
                TYPE_INSERT -> "insert"
                TYPE_UPDATE -> "update"
                TYPE_DELETE -> "delete"
                else -> ""
            }, property, *pathParams
        ).build()

        /**
         * @param curdType
         * @param property
         * @param pathParams
         */
        fun getUriBuilder(
            curdType: String, property: String, vararg pathParams: String
        ) = Uri.Builder().apply {
            scheme(SCHEME)
            encodedAuthority(authority)
            appendPath(curdType)
            appendPath(property)
            pathParams.forEach { appendPath(it) }
        }
    }

    private val matcher = UriMatcher(UriMatcher.NO_MATCH)

    /**
     * 跟进包名+version查询是否支持双音源
     */
    private val CODE_QUERY_PROPERTY_ALL = 1

    private val CODE_QUERY_PROPERTY = 2

    private val COLUMN_PACKAGE_NAME_STRING = "packageName"

    private val COLUMN_VERSION_CODE_INTEGER = "versionCode"

    override fun onCreate(): Boolean {
        authority = "${context?.packageName}.AppPropertiesProvider"
        //查询所有属性
        matcher.addURI(authority, "query/all", CODE_QUERY_PROPERTY_ALL)
        //查询指定属性：content://${authority}/query/${property}/${packageName}/${versionCode}
        matcher.addURI(authority, "query/*/*/*", CODE_QUERY_PROPERTY)

        return true
    }

    /**
     * 查询属性
     * @param uri
     * @param selection
     * @param sortOrder
     * @return
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> return queryAllProperties()
            CODE_QUERY_PROPERTY -> return queryProperty(uri.pathSegments)
        }
        return null
    }

    /**
     * 查询所有属性
     */
    private fun queryAllProperties(): Cursor {
        val columns = mutableListOf(COLUMN_PACKAGE_NAME_STRING, COLUMN_VERSION_CODE_INTEGER)
        //添加属性列
        columns.addAll(AppPropertyManager.properties.keys)

        val cursor = MatrixCursor(columns.toTypedArray())
        try {
            context?.let {
                ApkUtils.getAllAppsInfo(it).forEach { apk ->
                    val packageName = apk.packageName
                    val versionCode = apk.versionCode

                    val propertyValues = mutableListOf<String>()
                    AppPropertyManager.properties.values.forEach { p ->
                        val value = p.getPropertyValue(packageName ?: "", versionCode.toLong())
                        propertyValues.add(value.toString())
                    }

                    cursor.addRow(mutableListOf(packageName, versionCode).apply {
                        addAll(propertyValues)
                    }.toTypedArray())
                }
            }
        } catch (e: Exception) {
            e.logStackTrace()
        } finally {
            cursor.close()
        }
        return cursor
    }

    /**
     * 查询某个属性
     */
    private fun queryProperty(segments: List<String>?): Cursor? {

        val columns = mutableListOf(COLUMN_PACKAGE_NAME_STRING, COLUMN_VERSION_CODE_INTEGER)

        var cursor: MatrixCursor? = null

        try {
            var packageName = ""
            var versionCode = 0
            val rows = mutableListOf<Any>()
            if (!segments.isNullOrEmpty() && segments.size >= 4) {
                val propertyName = segments[1]
                columns.add(propertyName)
                packageName = segments[2]
                rows.add(packageName)
                versionCode = segments[3].toInt()
                rows.add(versionCode)

                cursor = MatrixCursor(columns.toTypedArray())
                AppPropertyManager.properties[propertyName]?.getPropertyValue(
                    packageName,
                    versionCode.toLong()
                ).apply {
                    rows.add(this ?: "")
                }
            }
            cursor?.addRow(rows.toTypedArray())
        } catch (e: Exception) {
            e.logStackTrace()
        }finally {
            cursor?.close()
        }
        return cursor
    }

    /**
     * 查询属性类型
     * @param uri
     * @return
     */
    override fun getType(uri: Uri): String? {
        return null
    }

    /**
     * 插入属性
     * @param uri
     * @param values
     * @return
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    /**
     * 删除属性
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    /**
     * 更新属性
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    /**
     * 调用方法
     * @param method
     * @param arg
     * @param extras
     * @return
     */
    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        try {
            when (method) {
                "clear" -> {
                    AppPropertyManager.properties[arg]?.propertyClear()
                }
                "sync" -> {
                    AppPropertyManager.cloudSyncProperties()
                }
                "queryOnline" -> {
                    arg?.apply {
                        val args = split("|")
                        if (args.size >= 3) {
                            val packageName = args[0]
                            val versionCode = args[1]
                            val propertyName = args[2]
                            AppPropertyManager.cloudQueryPropertyValue(
                                packageName,
                                versionCode.toLong(),
                                propertyName
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return super.call(method, arg, extras)
    }
}