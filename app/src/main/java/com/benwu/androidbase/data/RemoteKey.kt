package com.benwu.androidbase.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class RemoteKey(
    @PrimaryKey
    val table: String,
    val nextKey: Int,
)
