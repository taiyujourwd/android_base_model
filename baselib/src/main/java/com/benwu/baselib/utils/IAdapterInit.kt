package com.benwu.baselib.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

interface IAdapterInit<T, V : ViewBinding> : View.OnClickListener {

    val mRecyclerView: RecyclerView

    val mContext: Context

    //region 初始化
    /**
     * 綁定layout
     *
     * @return layout
     */
    fun bindViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): V

    /**
     * 綁定view
     *
     * @param position 資料位置
     */
    fun bindView(
        binding: V,
        position: Int,
        data: T?,
        payloads: MutableList<Any>? = null
    )
    //endregion

    /**
     * 設置view的點擊事件
     */
    fun setOnClickListeners(vararg views: View) {
        views.forEach { it.setOnClickListener(this) }
    }
}