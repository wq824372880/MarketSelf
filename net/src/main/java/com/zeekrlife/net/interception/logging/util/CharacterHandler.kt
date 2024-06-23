package com.zeekrlife.net.interception.logging.util

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import java.util.regex.Pattern
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class CharacterHandler private constructor() {
    companion object {
        //emoji过滤器
        val EMOJI_FILTER: InputFilter = object : InputFilter {
            var emoji = Pattern.compile(
                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE
            )

            override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
                val emojiMatcher = emoji.matcher(source)
                return if (emojiMatcher.find()) {
                    ""
                } else null
            }
        }

        //特殊字符过滤
        val setEditTextInhibitInputSpeChats: InputFilter = object : InputFilter {
            val speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_-]"
            var pattern = Pattern.compile(speChat)

            override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
                val matcher = pattern.matcher(source.toString())
                return if (matcher.find())
                    ""
                else null
            }
        }

        //只能输入文字字母数字
        val typeFilter: InputFilter = object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
                val p = Pattern.compile("[0-9a-zA-Z|\u4e00-\u9fa5]+")
                val m = p.matcher(source.toString())
                if (!m.matches()) return ""
                return null
            }
        }

        /**
         * 禁止输入空格
         * @param etText
         */
        fun setEditTextInhibitInputSpeChat(etText: EditText) {
            val filter = InputFilter { source, start, end, dest, dstart, dend ->
                if (source.let { it?.equals(" ") == true })
                    ""
                else null
            }
        }

        /**
         * json 格式化
         *
         * @param json
         * @return
         */
        @JvmStatic
        fun jsonFormat(json: String): String {
            var mJson = json
            if (TextUtils.isEmpty(mJson)) {
                return "Empty/Null json content"
            }
            var message: String
            try {
                mJson = mJson.trim { it <= ' ' }
                message = when {
                    mJson.startsWith("{") -> {
                        val jsonObject = JSONObject(mJson)
                        jsonObject.toString(4)
                    }
                    mJson.startsWith("[") -> {
                        val jsonArray = JSONArray(mJson)
                        jsonArray.toString(4)
                    }
                    else -> {
                        mJson
                    }
                }
            } catch (e: JSONException) {
                message = mJson
            } catch (error: OutOfMemoryError) {
                message = "Output omitted because of Object size"
            }
            return message
        }

        /**
         * xml 格式化
         *
         * @param xml
         * @return
         */
        @JvmStatic
        fun xmlFormat(xml: String?): String? {
            if (TextUtils.isEmpty(xml)) {
                return "Empty/Null xml content"
            }
            val message: String?
            message = try {
                val xmlInput: Source =
                    StreamSource(StringReader(xml))
                val xmlOutput =
                    StreamResult(StringWriter())
                val transformer =
                    TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                transformer.transform(xmlInput, xmlOutput)
                xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
            } catch (e: TransformerException) {
                xml
            }
            return message
        }
    }

    init {
        throw IllegalStateException("you can't instantiate me!")
    }
}