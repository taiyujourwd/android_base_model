package com.benwu.androidbase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.benwu.androidbase.data.Repo
import com.benwu.androidbase.databinding.ListItemRepoBinding
import com.benwu.baselib.adapter.BaseAdapter
import com.benwu.baselib.utils.BaseComparator

class RepoRvAdapter : BaseAdapter<Repo.Item, ListItemRepoBinding>(BaseComparator()) {

    override fun bindViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ListItemRepoBinding.inflate(inflater, parent, false)

    override fun bindView(
        binding: ListItemRepoBinding,
        position: Int,
        data: Repo.Item?,
        payloads: MutableList<Any>?
    ) {
        binding.tvTitle.text = data?.name
        binding.tvStarCount.text = data?.starCount.toString()
        binding.tvSubtitle.text = data?.description
    }
}

