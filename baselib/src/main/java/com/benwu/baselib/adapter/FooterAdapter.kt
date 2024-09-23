package com.benwu.baselib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.benwu.baselib.databinding.ListItemFooterBinding
import com.benwu.baselib.extension.hideKeyboard
import com.benwu.baselib.extension.recyclerview.ViewHolder

class FooterAdapter(val retry: () -> Unit) : LoadStateAdapter<ViewHolder>(),
    View.OnClickListener {

    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mContext: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView
        mContext = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        ViewHolder(ListItemFooterBinding.inflate(LayoutInflater.from(mContext), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        val binding = holder.binding as ListItemFooterBinding
        binding.progress.isVisible = loadState is LoadState.Loading
        binding.error.isVisible = loadState is LoadState.Error
        binding.retry.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        hideKeyboard(mContext, v)
        retry()
    }
}