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
import com.benwu.baselib.databinding.IncludeDataEmptyBinding
import com.benwu.baselib.extension.isNullOrEmpty
import com.benwu.baselib.extension.recyclerview.ViewHolder
import com.benwu.baselib.utils.IAdapterInit
import com.benwu.baselib.utils.IAdapterInit.Companion.VIEW_TYPE_DATA_EMPTY

abstract class BaseAdapter<T, V : ViewBinding>(diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, ViewHolder>(diffCallback), IAdapterInit<T, V> {

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = if (viewType == VIEW_TYPE_DATA_EMPTY) {
            IncludeDataEmptyBinding.inflate(layoutInflater, parent, false)
        } else {
            bindViewBinding(layoutInflater, parent, viewType)
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (isNullOrEmpty(payloads)) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            bindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindViewHolder(holder, position, null)
    }

    override fun bindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        if (holder.binding !is IncludeDataEmptyBinding) {
            val binding = holder.binding as V
            bindView(binding, position, getItem(position), payloads)
            bindOnClickListener(holder, binding)
        }
    }

    override fun getItemCount() = if (isNullOrEmpty(currentList)) {
        1
    } else {
        super.getItemCount()
    }

    override fun getItemViewType(position: Int) = if (isNullOrEmpty(currentList)) {
        VIEW_TYPE_DATA_EMPTY
    } else {
        super.getItemViewType(position)
    }
    //endregion

    override fun getData(holder: ViewHolder): T? = getItem(getPosition(holder))

    override fun setOnItemClickListener(listener: (View?, Int, T?) -> Unit) {
        _onItemClickListener = listener
    }

    override fun onClick(view: View?, position: Int, data: T?) {
        super.onClick(view, position, data)
        onItemClickListener?.invoke(view, position, data)
    }
}