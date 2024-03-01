package com.benwu.baselib.utils

import androidx.recyclerview.widget.DiffUtil
import com.benwu.baselib.data.BaseDiffItemData

class BaseComparator<T : BaseDiffItemData> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.getKey() == newItem.getKey()

    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}