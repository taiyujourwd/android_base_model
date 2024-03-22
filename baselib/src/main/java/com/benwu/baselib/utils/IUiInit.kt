package com.benwu.baselib.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.R
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.application.BaseApplication
import com.benwu.baselib.extension.hideKeyboard
import com.benwu.baselib.extension.snackbar

interface IUiInit<V : ViewBinding> : View.OnClickListener {

    val mActivity: BaseActivity<*>

    val mApplication: BaseApplication

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

    /**
     * 是否啟用全部功能
     *
     * @return 是否啟用
     */
    fun isEnableAllFun(): Boolean {
        val isEnable = false
        if (!isEnable) mActivity.getString(R.string.un_open_fun).snackbar(binding.root).show()
        return isEnable
    }

    override fun onClick(v: View?) {
        mActivity.hideKeyboard()
    }
}