package com.zeekrlife.market.utils

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Html
import android.text.Spanned

/**
 * @author Lei.Chen29
 * @date 2022/6/22 11:34
 * description：
 */
object HtmlUtils {

    /**
     * html转Spanned
     */
    fun fromHtml(html: String?): Spanned? = if (VERSION.SDK_INT >= VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(html)
    }
}