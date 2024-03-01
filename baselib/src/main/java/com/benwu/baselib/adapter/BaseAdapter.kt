@file:Suppress("UNCHECKED_CAST")

package com.benwu.baselib.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.recyclerview.OnItemClickListener
import com.benwu.baselib.recyclerview.ViewHolder
import com.benwu.baselib.utils.IAdapterInit

abstract class BaseAdapter<T, V : ViewBinding>(diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, ViewHolder<ViewBinding>>(diffCallback), IAdapterInit<T, V> {

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(bindViewBinding(LayoutInflater.from(mContext), parent, viewType))

    override fun onBindViewHolder(
        holder: ViewHolder<ViewBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            bindView(holder.binding as V, position, getItem(position), payloads)
            holder.itemView.also { it.tag = position }.setOnClickListener(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<ViewBinding>, position: Int) {
        bindView(holder.binding as V, position, getItem(position))
        holder.itemView.also { it.tag = position }.setOnClickListener(this)
    }
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
}