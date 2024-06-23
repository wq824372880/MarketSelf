package com.zeekrlife.market.utils.xml;

import androidx.annotation.Nullable;

import java.nio.charset.Charset;

/**
 * @author Lei.Chen29
 */
public class HexDump {
    private final static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private final static char[] HEX_LOWER_CASE_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static String dumpHexString(@Nullable byte[] array) {
        if (array == null) {
            return "(null)";
        }
        // 调用重载方法，转换整个字节数组为十六进制字符串
        return dumpHexString(array, 0, array.length);
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static String dumpHexString(@Nullable byte[] array, int offset, int length) {
        if (array == null) {
            return "(null)";
        }
        StringBuilder result = new StringBuilder();

        byte[] line = new byte[16];
        int lineIndex = 0;

        result.append("\n0x");
        result.append(toHexString(offset));

        for (int i = offset; i < offset + length; i++) {
            if (lineIndex == 16) {
                result.append(" ");

                for (int j = 0; j < 16; j++) {
                    if (line[j] > ' ' && line[j] < '~') {
                        result.append(new String(line, j, 1, Charset.defaultCharset()));
                    } else {
                        result.append(".");
                    }
                }

                result.append("\n0x");
                result.append(toHexString(i));
                lineIndex = 0;
            }

            byte b = array[i];
            result.append(" ");
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);

            line[lineIndex++] = b;
        }

        if (lineIndex != 16) {
            int count = (16 - lineIndex) * 3;
            count++;
            for (int i = 0; i < count; i++) {
                result.append(" ");
            }

            for (int i = 0; i < lineIndex; i++) {
                if (line[i] > ' ' && line[i] < '~') {
                    result.append(new String(line, i, 1));
                } else {
                    result.append(".");
                }
            }
        }

        return result.toString();
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static String toHexString(byte b) {
        return toHexString(toByteArray(b));
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static String toHexString(byte[] array) {
        return toHexString(array, 0, array.length, true);
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static String toHexString(byte[] array, boolean upperCase) {
        return toHexString(array, 0, array.length, upperCase);
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static String toHexString(byte[] array, int offset, int length) {
        return toHexString(array, offset, length, true);
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static String toHexString(byte[] array, int offset, int length, boolean upperCase) {
        char[] digits = upperCase ? HEX_DIGITS : HEX_LOWER_CASE_DIGITS;
        char[] buf = new char[length * 2];

        int bufIndex = 0;
        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            buf[bufIndex++] = digits[(b >>> 4) & 0x0F];
            buf[bufIndex++] = digits[b & 0x0F];
        }

        return new String(buf);
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static String toHexString(int i) {
        return toHexString(toByteArray(i));
    }

    public static byte[] toByteArray(byte b) {
        byte[] array = new byte[1];
        array[0] = b;
        return array;
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static byte[] toByteArray(int i) {
        byte[] array = new byte[4];

        array[3] = (byte) (i & 0xFF);
        array[2] = (byte) ((i >> 8) & 0xFF);
        array[1] = (byte) ((i >> 16) & 0xFF);
        array[0] = (byte) ((i >> 24) & 0xFF);

        return array;
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    private static int toByte(char c) {
        if (c >= '0' && c <= '9') {
            return (c - '0');
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 'A' + 10);
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a' + 10);
        }

        throw new RuntimeException("Invalid hex char '" + c + "'");
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] buffer = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString.charAt(i + 1)));
        }

        return buffer;
    }
    /**
     * 将字节数组转换为十六进制字符串的静态方法。
     * 如果输入的字节数组为null，则返回字符串"(null)"。
     * 否则，会调用dumpHexString方法的另一个重载版本，以0为起始索引，数组长度为结束索引，
     * 对字节数组的这部分内容进行十六进制字符串的转换。
     * 欲转换为十六进制字符串的字节数组。可以为null。
     * @return 如果输入的字节数组为null，返回"(null)"字符串；
     *         否则，返回表示字节数组对应部分的十六进制字符串。
     */
    public static StringBuilder appendByteAsHex(StringBuilder sb, byte b, boolean upperCase) {
        char[] digits = upperCase ? HEX_DIGITS : HEX_LOWER_CASE_DIGITS;
        sb.append(digits[(b >> 4) & 0xf]);
        sb.append(digits[b & 0xf]);
        return sb;
    }
}