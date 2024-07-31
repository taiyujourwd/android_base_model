package com.benwu.baselib.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.extension.hideKeyboard
import com.benwu.baselib.extension.recyclerview.ViewHolder

interface IUiInit<V : ViewBinding> : View.OnClickListener {

    val mActivity: BaseActivity<*>

    val binding: V

    //region 初始化
    /**
     * 綁定layout
     *
     * @return layout
     */
    fun bindViewBinding(inflater: LayoutInflater, container: ViewGroup? = null): V

    /**
     * 取得bundle
     */
    fun getBundle(bundle: Bundle)

    /**
     * 初始化view
     */
    fun initView()
    //endregion

    /**
     * 取得data
     */
    fun getData()

    /**
     * 觀察者
     */
    fun observer()

    /**
     * 設置view的點擊事件
     */
    fun setOnClickListeners(vararg views: View) {
        views.forEach { it.setOnClickListener(this) }
    }

    override fun onClick(v: View?) {
        hideKeyboard(mActivity, v)
    }
}

interface IAdapterInit<T, V : ViewBinding> {

    val mRecyclerView: RecyclerView

    val mContext: Context

    val onItemClickListener: ((View?, Int, T?) -> Unit)?

    //region 初始化
    /**
     * 綁定layout
     *
     * @return layout
     */
    fun bindViewBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewBinding

    /**
     * 綁定viewHolder
     *
     * @param position 資料位置
     */
    fun bindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>? = null)

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

    /**
     * 綁定點擊事件
     */
    fun bindOnClickListener(holder: ViewHolder, binding: V) {
        setOnClickListeners(holder, binding.root)
    }
    //endregion

    fun getPosition(holder: ViewHolder) = holder.bindingAdapterPosition

    fun getData(holder: ViewHolder): T?

    fun setOnItemClickListener(listener: (View?, Int, T?) -> Unit)

    /**
     * 設置view的點擊事件
     */
    fun setOnClickListeners(holder: ViewHolder, vararg views: View) {
        views.forEach { view ->
            view.setOnClickListener {
                onClick(it, getPosition(holder), getData(holder))
            }
        }
    }

    fun onClick(view: View?, position: Int, data: T?) {
        hideKeyboard(mContext, view)
    }

    companion object {
        const val VIEW_TYPE_DATA_EMPTY = 999
    }
}

interface IDialogResult<T> {
    val onDialogResultListener: ((AppCompatDialogFragment, T?) -> Unit)?

    fun setOnDialogResultListener(listener: (AppCompatDialogFragment, T?) -> Unit): AppCompatDialogFragment
}