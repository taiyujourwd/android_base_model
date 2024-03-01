package com.benwu.androidbase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.benwu.androidbase.data.Repo
import com.benwu.androidbase.databinding.IncludeItemRepoBinding
import com.benwu.baselib.adapter.BaseAdapter
import com.benwu.baselib.utils.BaseComparator

class RepoRvAdapter : BaseAdapter<Repo.Item, IncludeItemRepoBinding>(BaseComparator()) {

    override fun bindViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = IncludeItemRepoBinding.inflate(inflater, parent, false)

    override fun bindView(
        binding: IncludeItemRepoBinding,
        position: Int,
        data: Repo.Item?,
        payloads: MutableList<Any>?
    ) {
        binding.tvTitle.text = data?.name
        binding.tvStarCount.text = data?.starCount.toString()
        binding.tvSubtitle.text = data?.description
    }
}

