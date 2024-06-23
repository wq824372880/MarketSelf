package com.zeekrlife.common.util


import android.annotation.SuppressLint
import android.os.Build
import android.os.StatFs
import android.text.TextUtils
import com.zeekrlife.common.ext.logStackTrace
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object FileUtils {

    private val LINE_SEP = System.getProperty("line.separator") ?: ""

    /**
     * `
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(filePath: String): Boolean {
        return isFileExists(getFileByPath(filePath))
    }

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(file: File?): Boolean {
        return file != null && file.exists()
    }

    /**
     * Rename the file.
     *
     * @param filePath The path of file.
     * @param newName  The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(filePath: String, newName: String): Boolean {
        return rename(getFileByPath(filePath), newName)
    }

    /**
     * Rename the file.
     *
     * @param file    The file.
     * @param newName The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(file: File?, newName: String): Boolean {
        // file is null then return false
        if (file == null) return false
        // file doesn't exist then return false
        if (!file.exists()) return false
        // the new name is space then return false
        if (isSpace(newName)) return false
        // the new name equals old name then return true
        if (newName == file.name) return true
        val newFile = File(file.parent + File.separator + newName)
        // the new name of file exists then return false
        return !newFile.exists() && file.renameTo(newFile)
    }

    /**
     * Return whether it is a directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(dirPath: String): Boolean {
        return isDir(getFileByPath(dirPath))
    }

    /**
     * Return whether it is a directory.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
    }

    /**
     * Return whether it is a file.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(filePath: String): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * Return whether it is a file.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param dirPath The path of directory.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(dirPath: String): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param filePath The path of file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsFile(filePath: String): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.logStackTrace()
            return false
        }

    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(filePath: String): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.logStackTrace()
            return false
        }

    }

    /**
     * Copy the directory.
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @param listener    The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyDir(
        srcDirPath: String,
        destDirPath: String,
        listener: OnReplaceListener
    ): Boolean {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
    }

    /**
     * Copy the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, false)
    }

    /**
     * Copy the directory or file.
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFile(srcPath: String, destPath: String): Boolean {
        return copyFile(getFileByPath(srcPath), getFileByPath(destPath),
            object : OnReplaceListener {
                override fun onReplace() = false
            })
    }

    /**
     * Copy the file.
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @param listener     The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFile(
        srcFilePath: String,
        destFilePath: String,
        listener: OnReplaceListener
    ): Boolean {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
    }

    /**
     * Copy the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, false)
    }

    /**
     * Move the directory.
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @param listener    The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(
        srcDirPath: String,
        destDirPath: String,
        listener: OnReplaceListener
    ): Boolean {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
    }

    /**
     * Move the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, true)
    }

    /**
     * Move the file.
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @param listener     The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(
        srcFilePath: String,
        destFilePath: String,
        listener: OnReplaceListener
    ): Boolean {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
    }

    /**
     * Move the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, true)
    }

    private fun copyOrMoveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener,
        isMove: Boolean
    ): Boolean {
        if (srcDir == null || destDir == null) return false
        // destDir's path locate in srcDir's path then return false
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) return false
        if (!srcDir.exists() || !srcDir.isDirectory) return false
        if (destDir.exists()) {
            if (listener.onReplace()) {// require delete the old directory
                if (!deleteAllInDir(destDir)) {// unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        if (!createOrExistsDir(destDir)) return false
        val files = srcDir.listFiles()
        for (file in files) {
            val oneDestFile = File(destPath + file.name)
            if (file.isFile) {
                if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false
            } else if (file.isDirectory) {
                if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    private fun copyOrMoveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener,
        isMove: Boolean
    ): Boolean {
        if (srcFile == null || destFile == null) return false
        // srcFile equals destFile then return false
        if (srcFile == destFile) return false
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile) return false
        if (destFile.exists()) {
            if (listener.onReplace()) {// require delete the old file
                if (!destFile.delete()) {// unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        if (!createOrExistsDir(destFile.parentFile)) return false
        return try {
            FileIOUtils.writeFileFromIS(
                destFile,
                FileInputStream(srcFile),
                false
            ) && !(isMove && !deleteFile(srcFile))
        } catch (e: FileNotFoundException) {
            e.logStackTrace()
            false
        }

    }

    /**
     * Delete the directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteDir(dirPath: String): Boolean {
        return deleteDir(getFileByPath(dirPath))
    }

    /**
     * Delete the directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * Delete the file.
     *
     * @param srcFilePath The path of source file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFile(srcFilePath: String): Boolean {
        return deleteFile(getFileByPath(srcFilePath))
    }

    /**
     * Delete the file.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    /**
     * Delete the all in directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteAllInDir(dirPath: String): Boolean {
        return deleteAllInDir(getFileByPath(dirPath))
    }

    /**
     * Delete the all in directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteAllInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir, FileFilter { true })
    }

    /**
     * Delete all files in directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dirPath: String): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * Delete all files in directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir, FileFilter { pathname -> pathname.isFile })
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(
        dirPath: String,
        filter: FileFilter
    ): Boolean {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
        }
        return true
    }

    /**
     * Return the files in directory.
     *
     * @param dirPath     The path of directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    @JvmOverloads
    fun listFilesInDir(dirPath: String, isRecursive: Boolean = false): List<File>? {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }

    /**
     * Return the files in directory.
     *
     * @param dir         The directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    @JvmOverloads
    fun listFilesInDir(dir: File?, isRecursive: Boolean = false): List<File>? {
        return listFilesInDirWithFilter(dir, FileFilter { true }, isRecursive)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dirPath: String,
        filter: FileFilter
    ): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dirPath     The path of directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dirPath: String,
        filter: FileFilter,
        isRecursive: Boolean
    ): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    @JvmOverloads
    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FileFilter,
        isRecursive: Boolean = false
    ): List<File>? {
        if (!isDir(dir)) return null
        val list = ArrayList<File>()
        val files = dir!!.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file)) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory) {
                    listFilesInDirWithFilter(file, filter, true)?.let {
                        list.addAll(it)
                    }
                }
            }
        }
        return list
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param filePath The path of file.
     * @return the time that the file was last modified
     */

    fun getFileLastModified(filePath: String): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param file The file.
     * @return the time that the file was last modified
     */
    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: -1
    }

    /**
     * Return the charset of file simply.
     *
     * @param filePath The path of file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(filePath: String): String {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * Return the charset of file simply.
     *
     * @param file The file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(file: File?): String {
        var p = 0
        var inputStream: InputStream? = null
        try {
            inputStream = BufferedInputStream(FileInputStream(file!!))
            p = (inputStream.read() shl 8) + inputStream.read()
        } catch (e: IOException) {
            e.logStackTrace()
        } finally {
            inputStream?.let {
                CloseUtils.closeIO(it)
            }
        }
        return when (p) {
            0xefbb -> "UTF-8"
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * Return the number of lines of file.
     *
     * @param filePath The path of file.
     * @return the number of lines of file
     */
    fun getFileLines(filePath: String): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * Return the number of lines of file.
     *
     * @param file The file.
     * @return the number of lines of file
     */
    fun getFileLines(file: File?): Int {
        var count = 1
        var inputStream: InputStream? = null
        try {
            inputStream = BufferedInputStream(FileInputStream(file!!))
            val buffer = ByteArray(1024)
            var readChars: Int
            if (LINE_SEP.endsWith("\n")) {
                do {
                    readChars = inputStream.read(buffer, 0, 1024)
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\n'.code.toByte()) ++count
                    }
                } while (readChars != -1)
            } else {
                do {
                    readChars = inputStream.read(buffer, 0, 1024)
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\r'.code.toByte()) ++count
                    }
                } while (readChars != -1)
            }
        } catch (e: IOException) {
            e.logStackTrace()
        } finally {
            inputStream?.let {
                CloseUtils.closeIO(it)
            }
        }
        return count
    }

    /**
     * Return the size of directory.
     *
     * @param dirPath The path of directory.
     * @return the size of directory
     */
    fun getDirSize(dirPath: String): String {
        return getDirSize(getFileByPath(dirPath))
    }

    /**
     * Return the size of directory.
     *
     * @param dir The directory.
     * @return the size of directory
     */
    fun getDirSize(dir: File?): String {
        val len = getDirLength(dir)
        return if (len == -1L) "" else byte2FitMemorySize(len)
    }

    /**
     * Return the length of file.
     *
     * @param filePath The path of file.
     * @return the length of file
     */
    fun getFileSize(filePath: String): String {
        val len = getFileLength(filePath)
        return if (len == -1L) "" else byte2FitMemorySize(len)
    }

    /**
     * Return the length of file.
     *
     * @param file The file.
     * @return the length of file
     */
    fun getFileSize(file: File): String {
        val len = getFileLength(file)
        return if (len == -1L) "" else byte2FitMemorySize(len)
    }

    /**
     * Return the length of directory.
     *
     * @param dirPath The path of directory.
     * @return the length of directory
     */
    fun getDirLength(dirPath: String): Long {
        return getDirLength(getFileByPath(dirPath))
    }

    /**
     * Return the length of directory.
     *
     * @param dir The directory.
     * @return the length of directory
     */
    fun getDirLength(dir: File?): Long {
        if (!isDir(dir)) return -1
        var len: Long = 0
        val files = dir!!.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                len += if (file.isDirectory) {
                    getDirLength(file)
                } else {
                    file.length()
                }
            }
        }
        return len
    }

    /**
     * Return the length of file.
     *
     * @param filePath The path of file.
     * @return the length of file
     */
    fun getFileLength(filePath: String): Long {
        val isURL = filePath.matches("[a-zA-z]+://[^\\s]*".toRegex())
        if (isURL) {
            try {
                val conn = URL(filePath).openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                conn.connect()
                return if (conn.responseCode == 200) {
                    conn.contentLength.toLong()
                } else -1
            } catch (e: IOException) {
                e.logStackTrace()
            }

        }
        return getFileLength(getFileByPath(filePath))
    }

    /**
     * Return the length of file.
     *
     * @param file The file.
     * @return the length of file
     */
    fun getFileLength(file: File?): Long {
        return if (!isFile(file)) -1L else file!!.length()
    }

    /**
     * Return the file's path of directory.
     *
     * @param file The file.
     * @return the file's path of directory
     */
    fun getDirName(file: File?): String? {
        return if (file == null) null else getDirName(file.absolutePath)
    }

    /**
     * Return the file's path of directory.
     *
     * @param filePath The path of file.
     * @return the file's path of directory
     */
    fun getDirName(filePath: String): String? {
        if (isSpace(filePath)) return filePath
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    /**
     * Return the name of file.
     *
     * @param file The file.
     * @return the name of file
     */
    fun getFileName(file: File?): String? {
        return if (file == null) null else getFileName(file.absolutePath)
    }

    /**
     * Return the name of file.
     *
     * @param filePath The path of file.
     * @return the name of file
     */
    fun getFileName(filePath: String): String? {
        if (isSpace(filePath)) return filePath
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * Return the name of file without extension.
     *
     * @param file The file.
     * @return the name of file without extension
     */
    fun getFileNameNoExtension(file: File?): String? {
        return if (file == null) null else getFileNameNoExtension(file.path)
    }

    /**
     * Return the name of file without extension.
     *
     * @param filePath The path of file.
     * @return the name of file without extension
     */
    fun getFileNameNoExtension(filePath: String): String? {
        if (isSpace(filePath)) return filePath
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * Return the extension of file.
     *
     * @param file The file.
     * @return the extension of file
     */
    fun getFileExtension(file: File?): String? {
        return if (file == null) null else getFileExtension(file.path)
    }

    /**
     * Return the extension of file.
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    fun getFileExtension(filePath: String): String? {
        if (isSpace(filePath)) return filePath
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    ///////////////////////////////////////////////////////////////////////////
    // copy from ConvertUtils
    ///////////////////////////////////////////////////////////////////////////

    private val HEX_DIGITS =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    private fun bytes2HexString(bytes: ByteArray?): String? {
        if (bytes == null) return null
        val len = bytes.size
        if (len <= 0) return null
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = HEX_DIGITS[bytes[i].toInt().ushr(4) and 0xf]
            ret[j++] = HEX_DIGITS[(bytes[i] and 0xf).toInt()]
            i++
        }
        return String(ret)
    }

    @SuppressLint("DefaultLocale")
    private fun byte2FitMemorySize(byteNum: Long): String {
        return if (byteNum < 0) {
            "shouldn't be less than zero!"
        } else if (byteNum < 1024) {
            String.format("%.3fB", byteNum.toDouble())
        } else if (byteNum < 1048576) {
            String.format("%.3fKB", byteNum.toDouble() / 1024)
        } else if (byteNum < 1073741824) {
            String.format("%.3fMB", byteNum.toDouble() / 1048576)
        } else {
            String.format("%.3fGB", byteNum.toDouble() / 1073741824)
        }
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * 读取文件内容,并以字符串形式返回
     *
     * @param path 文件路径
     * @return 文件内容
     */
    fun load(path: String?): String {
        if (path == null) {
            throw NullPointerException("path should not be null.")
        }

        var string: String? = null
        try {
            string = StringUtils.stringFromInputStream(FileInputStream(path))
        } catch (e: Exception) {
            e.logStackTrace()
        }

        return string ?: ""
    }

    /**
     * 将字符串数据保存到文件.
     * <br></br>注意：如果没有目录则会创建目录
     *
     * @param content 字符串内容
     * @param path    文件路径
     * @return 成功返回true, 否则返回false
     */
    @Synchronized
    fun store(content: String?, path: String?): Boolean {
        if (path == null) {
            throw NullPointerException("path should not be null.")
        }
        var bufferedWriter: BufferedWriter? = null
        try {
            //可能无存储卡或者其他原因导致
            val file = createFile(path) ?: return false
            bufferedWriter = BufferedWriter(FileWriter(file))
            bufferedWriter.write(content ?: "")
            bufferedWriter.flush()
            return true
        } catch (e: IOException) {
            e.logStackTrace()
        } finally {
            bufferedWriter?.close()
        }
        return false
    }

    /**
     * 创建文件， 如果不存在则创建，否则返回原文件的File对象
     *
     * @param path 文件路径
     * @return 创建好的文件对象, 返回为空表示失败
     */
    @Synchronized
    fun createFile(path: String): File? {
        if (StringUtils.isEmpty(path)) {
            return null
        }

        val file = File(path)
        if (file.isFile) {
            return file
        }

        val parentFile = file.parentFile
        if (parentFile != null && (parentFile.isDirectory || parentFile.mkdirs())) {
            try {
                if (file.createNewFile()) {
                    return file
                }
            } catch (e: Exception) {
                e.logStackTrace()
            }
        }
        return null
    }

    /**
     * 无条件删除指定目录中的文件
     *
     * @param path 目录路径
     * @return 删除文件个数
     */
    @Synchronized
    fun clearFolder(path: File): Int {
        var deletedItems = 0
        val fileList = path.listFiles()
        if (fileList != null) {
            for (file in fileList) {
                if (file.isDirectory) {
                    deletedItems += clearFolder(file)
                }
                if (file.delete()) {
                    deletedItems++
                }
            }
        }
        return deletedItems
    }

    /**
     * 删除文件或目录
     *
     * @param path 文件或目录路径。
     * @return true 表示删除成功，否则为失败
     */
    @Synchronized
    fun delete(path: String): Boolean {
        return path.isNotEmpty() && delete(File(path))
    }

    /**
     * 删除文件或目录
     *
     * @param path 文件或目录。
     * @return true 表示删除成功，否则为失败
     */
    @Synchronized
    fun delete(path: File?): Boolean {
        if (null == path) {
            return true
        }

        if (path.isDirectory) {
            val files = path.listFiles()
            if (null != files) {
                for (file in files) {
                    if (!delete(file)) {
                        return false
                    }
                }
            }
        }
        return !path.exists() || path.delete()
    }

    /**
     * Return the total size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the total size of file system
     */
    fun getFsTotalSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val totalSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            totalSize = statFs.blockCountLong
        } else {
            blockSize = statFs.blockSize.toLong()
            totalSize = statFs.blockCount.toLong()
        }
        return blockSize * totalSize
    }

    /**
     * Return the available size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the available size of file system
     */
    fun getFsAvailableSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val availableSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            availableSize = statFs.availableBlocksLong
        } else {
            blockSize = statFs.blockSize.toLong()
            availableSize = statFs.availableBlocks.toLong()
        }
        return blockSize * availableSize
    }

    interface OnReplaceListener {
        fun onReplace(): Boolean
    }
}
