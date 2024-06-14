package com.benwu.baselib.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.R
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.extension.hideKeyboard
import com.benwu.baselib.extension.toast
import com.benwu.baselib.recyclerview.ViewHolder

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
     * 是否啟用全部功能
     *
     * @return 是否啟用
     */
    fun isEnableAllFun(): Boolean {
        val isEnable = false
        if (!isEnable) mActivity.getString(R.string.un_open_fun).toast(mActivity).show()
        return isEnable
    }

    /**
     * 設置view的點擊事件
     */
    fun setOnClickListeners(vararg views: View) {
        views.forEach { it.setOnClickListener(this) }
    }

    override fun onClick(v: View?) {
        mActivity.hideKeyboard(v)
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
    fun bindViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): V

    /**
     * 綁定viewHolder
     *
     * @param position 資料位置
     */
    fun bindViewHolder(holder: ViewHolder<V>, position: Int, payloads: MutableList<Any>? = null)

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
    fun bindOnClickListener(holder: ViewHolder<V>) {
        setOnClickListeners(holder, holder.binding.root)
    }
    //endregion

    fun getPosition(holder: ViewHolder<V>) = holder.bindingAdapterPosition

    fun getData(holder: ViewHolder<V>): T?

    fun setOnItemClickListener(listener: ((View?, Int, T?) -> Unit))

    /**
     * 設置view的點擊事件
     */
    fun setOnClickListeners(holder: ViewHolder<V>, vararg views: View) {
        views.forEach { view ->
            view.setOnClickListener {
                onClick(it, getPosition(holder), getData(holder))
            }
        }
    }

    fun onClick(view: View?, position: Int, data: T?) {
        mContext.hideKeyboard(view)
    }
}

interface IDialogResult<T> {
    val onDialogResultListener: ((AppCompatDialogFragment, T?) -> Unit)?

    fun setOnDialogResultListener(listener: (AppCompatDialogFragment, T?) -> Unit): AppCompatDialogFragment
}