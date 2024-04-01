@file:Suppress("UNCHECKED_CAST")

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
import com.benwu.baselib.recyclerview.OnItemClickListener
import com.benwu.baselib.recyclerview.ViewHolder
import com.benwu.baselib.utils.IAdapterInit

abstract class BasePagingDataAdapter<T : Any, V : ViewBinding>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, ViewHolder<V>>(diffCallback),
    IAdapterInit<T, V>, View.OnClickListener {

    private lateinit var _mRecyclerView: RecyclerView
    private lateinit var _mContext: Context

    protected var onItemClickListener: OnItemClickListener<T>? = null

    override val mRecyclerView get() = _mRecyclerView

    override val mContext get() = _mContext

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
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            bindView(holder, holder.binding, position, getItem(position), payloads)
            holder.itemView.also { it.tag = position }.setOnClickListener(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<V>, position: Int) {
        bindView(holder, holder.binding, position, getItem(position))
        holder.itemView.also { it.tag = position }.setOnClickListener(this)
    }

    override fun getItemViewType(position: Int) = if (position == itemCount) VIEW_TYPE_FOOTER else 0
    //endregion

    /**
     * 設置item點擊監聽
     */
    @JvmName("setOnItemClickListener1")
    fun setOnItemClickListener(listener: OnItemClickListener<T>) {
        onItemClickListener = listener
    }

    override fun onClick(v: View?) {
        v?.also {
            val position = it.tag as Int
            if (position >= itemCount) return
            onItemClickListener?.onItemClick(it, position, getItem(position))
        }
    }

    companion object {
        private const val VIEW_TYPE_FOOTER = 100
    }
}