package com.zeekrlife.common.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Paint
import android.text.InputType
import android.widget.EditText
import com.zeekrlife.common.ext.logStackTrace
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.regex.Pattern


object StringUtils {

    private const val CACHE_SIZE = 4096

    /**
     * 判断字符串是否为 null 或长度为 0
     *
     * @param s 待校验字符串
     * @return `true`: 空<br></br> `false`: 不为空
     */
    fun isEmpty(s: CharSequence?): Boolean {
        return s == null || s.isEmpty()
    }

    /**
     * 判断字符串是否为 null 或全为空格
     *
     * @param s 待校验字符串
     * @return `true`: null 或全空格<br></br> `false`: 不为 null 且不全空格
     */
    fun isTrimEmpty(s: String?): Boolean {
        return s == null || s.trim { it <= ' ' }.isEmpty()
    }

    /**
     * 判断字符串是否为 null 或全为空白字符
     *
     * @param s 待校验字符串
     * @return `true`: null 或全空白字符<br></br> `false`: 不为 null 且不全空白字符
     */
    fun isSpace(s: String?): Boolean {
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
     * 判断两字符串忽略大小写是否相等
     *
     * @param a 待校验字符串 a
     * @param b 待校验字符串 b
     * @return `true`: 相等<br></br>`false`: 不相等
     */
    fun equalsIgnoreCase(a: String, b: String): Boolean {
        return a.equals(b, ignoreCase = true)
    }

    /**
     * null 转为长度为 0 的字符串
     *
     * @param s 待转字符串
     * @return s 为 null 转为长度为 0 字符串，否则不改变
     */
    fun null2Length0(s: String?): String {
        return s ?: ""
    }

    /**
     * 返回字符串长度
     *
     * @param s 字符串
     * @return null 返回 0，其他返回自身长度
     */
    fun length(s: CharSequence?): Int {
        return s?.length ?: 0
    }

    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    fun upperFirstLetter(s: String): String? {
        return if (isEmpty(s) || !Character.isLowerCase(s[0])) s else (s[0].code - 32).toChar().toString() + s.substring(1)
    }

    /**
     * 首字母小写
     *
     * @param s 待转字符串
     * @return 首字母小写字符串
     */
    fun lowerFirstLetter(s: String): String? {
        return if (isEmpty(s) || !Character.isUpperCase(s[0])) s else (s[0].code + 32).toChar().toString() + s.substring(1)
    }

    /**
     * 反转字符串
     *
     * @param s 待反转字符串
     * @return 反转字符串
     */
    fun reverse(s: String): String {
        val len = length(s)
        if (len <= 1) return s
        val mid = len shr 1
        val chars = s.toCharArray()
        var c: Char
        for (i in 0 until mid) {
            c = chars[i]
            chars[i] = chars[len - i - 1]
            chars[len - i - 1] = c
        }
        return String(chars)
    }

    /**
     * 转化为半角字符
     *
     * @param s 待转字符串
     * @return 半角字符串
     */
    fun toDBC(s: String): String? {
        if (isEmpty(s)) return s
        val chars = s.toCharArray()
        var i = 0
        val len = chars.size
        while (i < len) {
            when (chars[i].code) {
                12288 -> chars[i] = ' '
                in 65281..65374 -> chars[i] = (chars[i].code - 65248).toChar()
                else -> chars[i] = chars[i]
            }
            i++
        }
        return String(chars)
    }

    /**
     * 转化为全角字符
     *
     * @param s 待转字符串
     * @return 全角字符串
     */
    fun toSBC(s: String): String? {
        if (isEmpty(s)) return s
        val chars = s.toCharArray()
        var i = 0
        val len = chars.size
        while (i < len) {
            when {
                chars[i] == ' ' -> chars[i] = 12288.toChar()
                chars[i].code in 33..126 -> chars[i] = (chars[i].code + 65248).toChar()
                else -> chars[i] = chars[i]
            }
            i++
        }
        return String(chars)
    }

    /**
     * 得到小数点几位的String
     * @param source 要转换的float值
     * @param decimal 保留小数点后位数
     * @return
     */
    fun getDecimalString(source: Float, decimal: Int): String {
        val decimalInt = Math.pow(10.0, decimal.toDouble()).toInt()
        val result = Math.round(source * decimalInt).toFloat() / decimalInt
        return result.toString()
    }

    /**
     * InputSteam 转换到 String，会把输入流关闭
     *
     * @param inputStream 输入流
     * @return String 如果有异常则返回null
     */
    fun stringFromInputStream(inputStream: InputStream): String? {
        try {
            val readBuffer = ByteArray(CACHE_SIZE)
            val byteArrayOutputStream = ByteArrayOutputStream()
            while (true) {
                val readLen = inputStream.read(readBuffer, 0, CACHE_SIZE)
                if (readLen <= 0) {
                    break
                }
                byteArrayOutputStream.write(readBuffer, 0, readLen)
            }
            return byteArrayOutputStream.toString("UTF-8")
        } catch (e: Exception) {
            e.logStackTrace()
        } catch (e: OutOfMemoryError) {
            e.logStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: Exception) {
                e.logStackTrace()
            }
        }
        return null
    }

    /**
     * 验证Email地址格式是否合法
     * @param emailAddress
     * @return
     */
    fun isEmail(emailAddress: String): Boolean {
        val p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
        val m = p.matcher(emailAddress)
        return m.find()
    }

    /**
     * 验证是否是合法的手机号码格式
     * @param mobile
     * @return
     */
    fun isMobile(mobile: String): Boolean {
        val l = mobile.length
        var rs = false
        when (l) {
            11 -> if (matchingText("^(13[0-9]|14[5|7]|17[0-9]|15[0-9]|18[0-9])\\d{4,8}$", mobile)) {
                rs = true
            }
            else -> rs = false
        }
        return rs
    }

    private fun matchingText(expression: String, text: String): Boolean {
        val p = Pattern.compile(expression) // 正则表达式
        val m = p.matcher(text) // 操作的字符串
        return m.matches()
    }

    /**
     * 验证输入的字符串是否全部是0-9的数字
     * @param str
     * @return
     */
    fun isNumeric(str: String): Boolean {
        val pattern = Pattern.compile("[0-9]*")
        val isNum = pattern.matcher(str)
        return isNum.matches()
    }

    /**
     * 手机号替换为****
     * @param s 手机号
     * return 手机号码中间数字替换为 ****
     */
    fun getSecureMobileText(s: String?): String? {
        if (s == null || s.length < 10) {
            return null
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append(s.substring(0, 3))
        stringBuilder.append("****")
        stringBuilder.append(s.substring(s.length - 4, s.length))
        return stringBuilder.toString()
    }

    /**
     * 判断是否全部是大写字符
     * @param word 字符串
     * @return 是否为大写字符
     */
    fun hasUpperCase(word: String): Boolean {
        for (i in 0 until word.length) {
            val c = word[i]
            if (Character.isUpperCase(c)) {
                return true
            }
        }
        return false
    }

    /**
     * 判断一个字符串是否含有数字
     * @param content 带判断字符串
     * @return true or false
     */
    fun hasDigit(content: String): Boolean {
        var flag = false
        val p = Pattern.compile(".*\\d+.*")
        val m = p.matcher(content)
        if (m.matches())
            flag = true
        return flag
    }

    /**
     * 密码显示或隐藏
     * @param editText
     */
    fun showOrHidePassword(editText: EditText) {
        if (editText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        } else {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
    }

    /**
     * 判断是否为中文
     */
    fun isChinese(str: String): Boolean {
        val regEx = "[\u4e00-\u9fa5]"
        val pat = Pattern.compile(regEx)
        val matcher = pat.matcher(str)
        var flg = false
        if (matcher.find())
            flg = true
        return flg
    }

    /**
     * 添加内容到剪切板
     */
    fun clipText(context: Context, s: String) {
        val cmb = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cmb.setPrimaryClip(ClipData.newPlainText(null, s))
    }

    /**
     * 获取剪切板内容
     */
    fun getClipboardContent(context: Context): String {
        val cm = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val data = cm.primaryClip
        if (data != null) {
            val item = data.getItemAt(0)
            return item.text.toString()
        }
        return ""
    }

    /**
     * @param text 字符串
     * @param size 文字大小
     * @return 字符串长度
     */
    fun getCharacterWidth(text: String?, size: Float): Int {
        if (null == text || "" == text) {
            return 0
        }
        val paint = Paint()
        paint.textSize = size
        return paint.measureText(text).toInt()
    }

}
