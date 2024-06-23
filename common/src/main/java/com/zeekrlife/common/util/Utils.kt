package com.zeekrlife.common.util

import android.graphics.Color
import android.os.Environment
import android.os.StatFs
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import com.zeekr.basic.appContext
import com.zeekr.car.util.StorageManagerUtils
import com.zeekrlife.common.ext.logStackTrace
import org.jetbrains.annotations.TestOnly
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils {
    private const val MIN_NEED_SPACE = 800 * 1024 * 1024 //800M

    /**
     * 将云端返回的大小转换为byte单位
     *
     * @param size
     * @return
     */
//    fun castSizeToBytes(size: String): Long {
//        try {
//            if (TextUtils.isEmpty(size)) {
//                return 0
//            }
//            if (size.contains("K") || size.contains("KB")) {
//                return (size.replace("K|KB".toRegex(), "").toFloat() * 1024).toLong()
//            } else if (size.contains("M") || size.contains("MB")) {
//                return (size.replace("M|MB".toRegex(), "").toFloat() * 1024 * 1024).toLong()
//            } else if (size.contains("G") || size.contains("GB")) {
//                return (size.replace("G|GB".toRegex(), "").toFloat() * 1024 * 1024 * 1024).toLong()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return 0
//    }

    fun calculateMBSize(size: String): Long {
        try {
            if (TextUtils.isEmpty(size)) {
                return 0
            }
            return (size.toFloat() * 1024 * 1024).toLong()
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return 0
    }

    /**
     * 磁盘空间是否足够
     * 剩余空间 - 下载需要的大小 >= 800M ：足够，否则不足够
     *
     * @param size 下载需要的大小
     * @return
     */
    fun isSpaceEnough(size: String): Boolean {
        val needSize: Long = calculateMBSize(size)
        return isSpaceEnough(needSize)
    }

    fun isSpaceEnough(size: Long): Boolean {
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?.let { externalFilesDir ->
                    val pathStr = externalFilesDir.absolutePath
                    val stat = StatFs(pathStr)
                    val blockSize = stat.blockSizeLong
                    val totalBlocks = stat.availableBlocksLong
                    val availableSize = blockSize * totalBlocks
                    return availableSize - size >= StorageManagerUtils.getStorageLowBytes(externalFilesDir)
                }
        }
        return false
    }

    /**
     * （不区分大小写、关键字多次出现多次变色)：
     */
    fun stringInterceptionChangeColor(text: String?, keyword: String?): SpannableString {
        val ss = SpannableString(text ?:"")
        try {
            if (text.isNullOrEmpty()) return ss
            val string = text.lowercase(Locale.ROOT)
            val key = keyword?.lowercase(Locale.ROOT) ?: ""
            val pattern = Pattern.compile(key)
            val matcher = pattern.matcher(string)
            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                ss.setSpan(
                    ForegroundColorSpan(Color.parseColor("#f88650")), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }catch (e: Exception) {
            e.logStackTrace()
        }
        return ss
    }

    /**
     * 判断字符串是否为URL
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    fun isHttpUrl(urls: String): Boolean {
        val regex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+$"
        try {
            val pat = Pattern.compile(regex.trim())
            val mat: Matcher = pat.matcher(urls.trim())
            return mat.matches()
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return false
    }

}