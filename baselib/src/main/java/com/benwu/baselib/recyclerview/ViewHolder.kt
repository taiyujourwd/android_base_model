package com.benwu.baselib.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class ViewHolder<out V : ViewBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)
