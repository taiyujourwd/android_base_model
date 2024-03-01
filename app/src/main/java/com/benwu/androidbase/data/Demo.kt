package com.benwu.androidbase.data

import com.benwu.baselib.data.BaseDiffItemData

data class Demo(
    var title: String,
    var subtitle: String
) : BaseDiffItemData {
    override fun getKey() = title
}
