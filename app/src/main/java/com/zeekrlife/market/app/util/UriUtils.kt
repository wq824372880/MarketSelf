package com.zeekrlife.market.app.util

import android.annotation.SuppressLint
import android.text.TextUtils
import kotlin.Throws
import android.provider.MediaStore
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.zeekrlife.common.ext.logStackTrace
import java.io.*
import java.lang.Exception

/**
 * 描述　:
 */
object UriUtils {
    fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
        val rootDataDir = context.getExternalFilesDir(null)
        val fileName = getFileName(contentUri)
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(rootDataDir.toString() + File.separator + fileName)
            copyFile(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    @SuppressLint("LogNotTimber")
    fun copyFile(context: Context, srcUri: Uri?, dstFile: File?) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
            outputStream = FileOutputStream(dstFile)
            copyStream(inputStream, outputStream)
        } catch (e: FileNotFoundException) {
            // 处理文件未找到异常
            Log.e("UriUtils", "FileNotFoundException: ${e.message}")
        } catch (e: IOException) {
            // 处理 IO 异常
            Log.e("UriUtils", "IOException: ${e.message}")
        } catch (e: SecurityException) {
            // 处理安全异常
            Log.e("UriUtils", "SecurityException: ${e.message}")
        } catch (e: Exception) {
            // 处理其他异常
            e.logStackTrace()
            Log.e("UriUtils", "Exception: ${Log.getStackTraceString(e)}")
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    @SuppressLint("LogNotTimber")
    @Throws(Exception::class, IOException::class)
    fun copyStream(input: InputStream?, output: OutputStream?): Int {
        val BUFFER_SIZE = 1024 * 2
        val buffer = ByteArray(BUFFER_SIZE)
        val `in` = BufferedInputStream(input, BUFFER_SIZE)
        val out = BufferedOutputStream(output, BUFFER_SIZE)
        var count = 0
        var n = 0
        try {
            while (`in`.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                // 处理输出流关闭异常
                Log.e("UriUtils", "IOException when closing output stream: ${e.message}")
            }
            try {
                `in`.close()
            } catch (e: IOException) {
                // 处理输入流关闭异常
                Log.e("UriUtils", "IOException when closing input stream: ${e.message}")
            }
        }
        return count
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}