package com.benwu.androidbase.data

import android.os.Parcelable
import com.benwu.baselib.data.BaseDiffItemData
import com.benwu.baselib.extension.getOrDefault
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Repo(
    @SerializedName("items")
    val items: List<Item>? = null
) {
    @Parcelize
    data class Item(
        @SerializedName("id")
        val id: Int? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("description")
        val description: String? = null,
        @SerializedName("stargazers_count")
        val starCount: Int? = null
    ) : BaseDiffItemData, Parcelable {
        override val key get() = id.getOrDefault(0)
    }
}
