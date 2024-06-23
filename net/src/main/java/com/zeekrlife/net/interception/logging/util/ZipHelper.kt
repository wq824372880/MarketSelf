package com.zeekrlife.net.interception.logging.util

import android.util.Log
import com.zeekr.car.util.CarLogUtils
import java.io.*
import java.nio.charset.Charset
import java.util.*
import java.util.zip.*

class ZipHelper private constructor() {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun decompressToStringForZlib(bytesToDecompress: ByteArray,charsetName: String = "UTF-8"): String? {
            val bytesDecompressed = decompressForZlib(bytesToDecompress)
            var returnValue: String? = null
            try {
                returnValue = String(bytesDecompressed,0, bytesDecompressed.size, Charset.forName(charsetName))
            } catch (uee: UnsupportedEncodingException) {
                Log.e("info", "UnsupportedEncodingException: " + Log.getStackTraceString(uee))
            } catch (e: Exception) {
                // 处理其他未知异常
                Log.e("info", "Exception: " + e.message)
            }
            return returnValue
        }

        /**
         * zlib decompress 2 byte
         *
         * @param bytesToDecompress
         * @return
         */
        fun decompressForZlib(bytesToDecompress: ByteArray): ByteArray {
            var returnValues: ByteArray = byteArrayOf()
            val inflater = Inflater()
            val numberOfBytesToDecompress = bytesToDecompress.size
            inflater.setInput(
                bytesToDecompress,
                0,
                numberOfBytesToDecompress
            )
            var numberOfBytesDecompressedSoFar = 0
            val bytesDecompressedSoFar: MutableList<Byte> =
                ArrayList()
            try {
                while (!inflater.needsInput()) {
                    val bytesDecompressedBuffer =
                        ByteArray(numberOfBytesToDecompress)
                    val numberOfBytesDecompressedThisTime = inflater.inflate(
                        bytesDecompressedBuffer
                    )
                    numberOfBytesDecompressedSoFar += numberOfBytesDecompressedThisTime
                    for (b in 0 until numberOfBytesDecompressedThisTime) {
                        bytesDecompressedSoFar.add(bytesDecompressedBuffer[b])
                    }
                }
                returnValues = ByteArray(bytesDecompressedSoFar.size)
                for (b in returnValues.indices) {
                    returnValues[b] = bytesDecompressedSoFar[b]
                }
            } catch (dfe: DataFormatException) {
                // 处理 DataFormatException
                Log.e("info", "DataFormatException: " + Log.getStackTraceString(dfe))
            } catch (e: Exception) {
                // 处理其他未知异常
                Log.e("info", "Exception: " + e.message)
            }
            inflater.end()
            return returnValues
        }

        /**
         * zlib compress 2 byte
         *
         * @param bytesToCompress
         * @return
         */
        fun compressForZlib(bytesToCompress: ByteArray?): ByteArray {
            val deflater = Deflater()
            deflater.setInput(bytesToCompress)
            deflater.finish()
            val bytesCompressed =
                ByteArray(Short.MAX_VALUE.toInt())
            val numberOfBytesAfterCompression = deflater.deflate(bytesCompressed)
            val returnValues = ByteArray(numberOfBytesAfterCompression)
            System.arraycopy(
                bytesCompressed,
                0,
                returnValues,
                0,
                numberOfBytesAfterCompression
            )
            return returnValues
        }

        /**
         * zlib compress 2 byte
         *
         * @param stringToCompress
         * @return
         */
        fun compressForZlib(stringToCompress: String): ByteArray? {
            var returnValues: ByteArray? = null
            try {
                returnValues = compressForZlib(
                    stringToCompress.toByteArray(charset("UTF-8"))
                )
            } catch (uee: UnsupportedEncodingException) {
                CarLogUtils.logStackTrace(uee)
            } catch (e: Exception) {
                // 处理其他未知异常
                Log.e("info", "Exception: " + e.message)
            }
            return returnValues
        }

        /**
         * gzip compress 2 byte
         *
         * @param string
         * @return
         * @throws IOException
         */
        fun compressForGzip(string: String): ByteArray? {
            var os: ByteArrayOutputStream? = null
            var gos: GZIPOutputStream? = null
            try {
                os = ByteArrayOutputStream(string.length)
                gos = GZIPOutputStream(os)
                gos.write(string.toByteArray(charset("UTF-8")))
                return os.toByteArray()
            } catch (e: IOException) {
                CarLogUtils.logStackTrace(e)
            } catch (e: Exception) {
                // 处理其他未知异常
                Log.e("info", "Exception: " + e.message)
            }finally {
                closeQuietly(gos)
                closeQuietly(os)
            }
            return null
        }
        /**
         * gzip decompress 2 string
         *
         * @param compressed
         * @param charsetName
         * @return
         */
        /**
         * gzip decompress 2 string
         *
         * @param compressed
         * @return
         * @throws IOException
         */
        @JvmStatic
        @JvmOverloads
        fun decompressForGzip(
            compressed: ByteArray,
            charsetName: String? = "UTF-8"
        ): String? {
            val BUFFER_SIZE = compressed.size
            var gis: GZIPInputStream? = null
            var `is`: ByteArrayInputStream? = null
            try {
                `is` = ByteArrayInputStream(compressed)
                gis = GZIPInputStream(`is`, BUFFER_SIZE)
                val string = StringBuilder()
                val data = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                while (gis.read(data).also { bytesRead = it } != -1) {
                    string.append(String(data, 0, bytesRead, Charset.forName(charsetName)))
                }
                return string.toString()
            } catch (e: IOException) {
                Log.e("info", "exception " + Log.getStackTraceString(e))
            } catch (e: Exception) {
                // 处理其他未知异常
                Log.e("info", "Exception: " + e.message)
            }finally {
                closeQuietly(gis)
                closeQuietly(`is`)
            }
            return null
        }

        private fun closeQuietly(closeable: Closeable?) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    // 处理 IOException
                    Log.e("info", "IOException while closing closeable: " + e.message)
                } catch (rethrown: RuntimeException) {
                    throw rethrown
                } catch (ignored: Exception) {
                    Log.e("info", "Exception: " + ignored.message)
                }
            }
        }
    }

    init {
        throw IllegalStateException("you can't instantiate me!")
    }
}