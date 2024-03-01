package com.benwu.baselib.recyclerview

import android.view.View

interface OnItemClickListener<T> {
    fun onItemClick(view: View, position: Int, data: T?)
}