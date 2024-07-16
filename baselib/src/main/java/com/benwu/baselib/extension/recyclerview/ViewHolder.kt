package com.benwu.baselib.extension.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class ViewHolder<out V : ViewBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)