package com.benwu.baselib.data

interface BaseDiffItemData {

    override fun equals(other: Any?): Boolean

    fun getKey(): Any
}
