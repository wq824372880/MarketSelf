package com.zeekrlife.market.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 分类列表
 */
@Parcelize
data class HomeItemCategoryBean(
    var createTime: String? = null,
    var createTimeDisplay: String? = null,
    var creatorId: Int = 0,
    var icon: String? = null,
    var nightIcon: String? = null,
    var delFlag: Int = 0,
    var displayStatus: Int = 0,
    var editorId: Int = 0,
    var id: Int = 0,
    var categoryName: String? = null,
    var updateTime: String? = null,
    var updateTimeDisplay: String? = null,
) : Parcelable