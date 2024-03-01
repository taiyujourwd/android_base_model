package com.benwu.androidbase.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.benwu.baselib.data.BaseDiffItemData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Repo(
    @SerializedName("items")
    val items: List<Item>? = null
) {
    @Entity(tableName = "repo")
    @Parcelize
    data class Item(
        @PrimaryKey
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("description")
        val description: String?,
        @SerializedName("stargazers_count")
        val starCount: Int
    ) : BaseDiffItemData, Parcelable {
        override fun getKey() = id
    }
}
