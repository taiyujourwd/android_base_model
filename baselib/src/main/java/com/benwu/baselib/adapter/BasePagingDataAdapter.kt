package com.benwu.baselib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.extension.isNullOrEmpty
import com.benwu.baselib.extension.recyclerview.ViewHolder
import com.benwu.baselib.utils.IAdapterInit

abstract class BasePagingDataAdapter<T : Any, V : ViewBinding>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, ViewHolder<V>>(diffCallback),
    IAdapterInit<T, V> {

    private lateinit var _mRecyclerView: RecyclerView
    private lateinit var _mContext: Context
    private var _onItemClickListener: ((View?, Int, T?) -> Unit)? = null

    override val mRecyclerView get() = _mRecyclerView

    override val mContext get() = _mContext

    override val onItemClickListener get() = _onItemClickListener

    //region 生命週期
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        _mRecyclerView = recyclerView
        _mContext = recyclerView.context

        mRecyclerView.layoutManager?.also {
            if (it is GridLayoutManager) {
                it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int) =
                        if (getItemViewType(position) == VIEW_TYPE_FOOTER) it.spanCount else 1
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(bindViewBinding(LayoutInflater.from(mContext), parent, viewType))

    override fun onBindViewHolder(
        holder: ViewHolder<V>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (isNullOrEmpty(payloads)) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            bindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<V>, position: Int) {
        bindViewHolder(holder, position, null)
    }

    override fun bindViewHolder(holder: ViewHolder<V>, position: Int, payloads: MutableList<Any>?) {
        bindView(holder.binding, position, getItem(position), payloads)
        bindOnClickListener(holder)
    }

    override fun getItemViewType(position: Int) = if (position == itemCount) VIEW_TYPE_FOOTER else 0
    //endregion

    override fun getData(holder: ViewHolder<V>): T? = getItem(getPosition(holder))

    override fun setOnItemClickListener(listener: (View?, Int, T?) -> Unit) {
        _onItemClickListener = listener
    }

    override fun onClick(view: View?, position: Int, data: T?) {
        super.onClick(view, position, data)
        onItemClickListener?.invoke(view, position, data)
    }

    companion object {
        private const val VIEW_TYPE_FOOTER = 100
    }
}