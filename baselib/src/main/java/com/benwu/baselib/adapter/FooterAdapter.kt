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
import com.benwu.baselib.recyclerview.ViewHolder

class FooterAdapter(val retry: () -> Unit) : LoadStateAdapter<ViewHolder<ListItemFooterBinding>>(),
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

    override fun onBindViewHolder(holder: ViewHolder<ListItemFooterBinding>, loadState: LoadState) {
        holder.binding.lavProgress.isVisible = loadState is LoadState.Loading
        holder.binding.groupError.isVisible = loadState is LoadState.Error
        holder.binding.btnRetry.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        retry()
    }
}