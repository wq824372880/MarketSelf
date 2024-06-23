package com.zeekrlife.net.interception.logging.util

import android.text.TextUtils
import android.util.Log

class LogUtils private constructor() {
    companion object {
        private const val DEFAULT_TAG = "Market"
        private var isLog = true
        fun isLog(): Boolean {
            return isLog
        }
        fun setLog(isLog: Boolean) {
            Companion.isLog = isLog
            XLog.init(isLog)
        }

        fun debugInfo(tag: String?, msg: String?) {
            if (!isLog || TextUtils.isEmpty(msg)) {
                return
            }
            Log.d(tag, msg?:"")
        }
        @JvmStatic
        fun debugInfo(msg: String?) {
            debugInfo(DEFAULT_TAG, msg)
        }

        fun i(tag: String?, msg: String?){
            XLog.logType(XLog.I, tag, XLog.STACK_TRACE_INDEX_7, msg)
        }

        fun w(tag: String?, msg: String?){
            XLog.logType(XLog.W, tag, XLog.STACK_TRACE_INDEX_7, msg)
        }
        @JvmStatic
        fun e(tag: String?, msg: String?){
            XLog.logType(XLog.E, tag, XLog.STACK_TRACE_INDEX_7, msg)
        }

        fun warnInfo(tag: String?, msg: String?) {
            if (!isLog || TextUtils.isEmpty(msg)) {
                return
            }
            Log.w(tag, msg?:"")
        }

        fun warnInfo(msg: String?) {
            warnInfo(DEFAULT_TAG, msg)
        }

        /**
         * 这里使用自己分节的方式来输出足够长度的 message
         *
         * @param tag 标签
         * @param msg 日志内容
         */
        private fun debugLongInfo(tag: String?, msg: String) {
            var mMsg = msg
            if (!isLog || TextUtils.isEmpty(mMsg)) {
                return
            }
            mMsg = mMsg.trim { it <= ' ' }
            var index = 0
            val maxLength = 3500
            var sub: String
            while (index < mMsg.length) {
                sub = if (mMsg.length <= index + maxLength) {
                    mMsg.substring(index)
                } else {
                    mMsg.substring(index, index + maxLength)
                }
                index += maxLength
                Log.d(tag, sub.trim { it <= ' ' })
            }
        }

        fun debugLongInfo(msg: String) {
            debugLongInfo(DEFAULT_TAG, msg)
        }
    }

    init {
        throw IllegalStateException("you can't instantiate me!")
    }
}