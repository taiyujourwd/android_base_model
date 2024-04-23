package com.benwu.androidbase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.benwu.androidbase.data.Demo
import com.benwu.androidbase.databinding.ListItemDemoBinding
import com.benwu.baselib.adapter.BaseAdapter
import com.benwu.baselib.utils.BaseComparator

class DemoRvAdapter : BaseAdapter<Demo, ListItemDemoBinding>(BaseComparator()) {

    override fun bindViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ListItemDemoBinding.inflate(inflater, parent, false)

    override fun bindView(
        binding: ListItemDemoBinding,
        position: Int,
        data: Demo?,
        payloads: MutableList<Any>?
    ) {
        binding.tvTitle.text = data?.title
        binding.tvSubtitle.text = data?.subtitle
    }
}