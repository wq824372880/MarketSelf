package com.zeekrlife.market.utils.xml;

import com.zeekrlife.market.BuildConfig;

/**
 * @author Lei.Chen29
 */
public final class StringPool {

    private final String[] pool = new String[512];

    public StringPool() {
    }

    private static boolean contentEquals(String s, char[] chars, int start, int length) {
        if (s.length() != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (chars[start + i] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string equal to {@code new String(array, start, length)}.
     */
    public String get(char[] array, int start, int length) {
        // Compute an arbitrary hash of the content
        int hashCode = 0;
        for (int i = start; i < start + length; i++) {
            hashCode = (hashCode * 31) + array[i];
        }

        // Pick a bucket using Doug Lea's supplemental secondaryHash function (from HashMap)
        hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
        hashCode ^= (hashCode >>> 7) ^ (hashCode >>> 4);
        int index = hashCode & (pool.length - 1);

        String pooled = pool[index];
        if (pooled != null && contentEquals(pooled, array, start, length)) {
            return pooled;
        }

        String result = new String(array, start, length);
        pool[index] = result;
        return result;
    }

    /**
     * 获取车型信息；<>br</>
     * BX1E\CS1E
     */
    public static String getModelType() {
        return BuildConfig.MODEL_TYPE;
    }
}