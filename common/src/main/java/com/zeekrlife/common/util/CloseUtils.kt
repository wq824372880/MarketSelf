package com.zeekrlife.common.util

import com.zeekrlife.common.ext.logStackTrace
import java.io.Closeable
import java.io.IOException
import java.lang.UnsupportedOperationException

class CloseUtils private constructor() {
    companion object {
        /**
         * Close the io stream.
         *
         * @param closeables closeables
         */
        @JvmStatic
        fun closeIO(vararg closeables: Closeable?) {
            for (closeable in closeables) {
                if (closeable != null) {
                    try {
                        closeable.close()
                    } catch (e: IOException) {
                        e.logStackTrace()
                    }
                }
            }
        }

        /**
         * Close the io stream quietly.
         *
         * @param closeables closeables
         */
        fun closeIOQuietly(vararg closeables: Closeable?) {
            for (closeable in closeables) {
                if (closeable != null) {
                    try {
                        closeable.close()
                    } catch (ignored: IOException) {
                    }
                }
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}