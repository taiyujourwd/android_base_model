package com.benwu.baselib.dialog_fragment

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.dialog.LoadingDialog
import com.benwu.baselib.utils.IDialogResult
import com.benwu.baselib.utils.IUiInit

abstract class BaseDialogFragment<T, V : ViewBinding> : AppCompatDialogFragment(), IUiInit<V>,
    IDialogResult<T> {

    val loadingDialog by lazy { LoadingDialog(mActivity) }

    private lateinit var _mActivity: BaseActivity<*>
    private lateinit var _binding: V
    private var _onDialogResultListener: ((AppCompatDialogFragment, T?) -> Unit)? = null

    protected lateinit var mDialog: Dialog
        private set

    protected var mWindow: Window? = null
        private set

    open val width = (Resources.getSystem().displayMetrics.widthPixels * 0.85).toInt() // 寬度
    open val height = ViewGroup.LayoutParams.WRAP_CONTENT // 高度

    override val mActivity get() = _mActivity

    override val binding get() = _binding

    override val onDialogResultListener get() = _onDialogResultListener

    //region 生命週期
    override fun onAttach(context: Context) {
        super.onAttach(context)
        _mActivity = context as BaseActivity<*>
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDialog = super.onCreateDialog(savedInstanceState)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mWindow = mDialog.window

        return mDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        mWindow?.setBackgroundDrawable(null)
        mWindow?.setLayout(width, height)

        arguments?.also { getBundle(it) }
        initView()
        observer()

        setOnClickListeners(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (isAutoGetDataEnable) getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingDialog.toggle(false)
    }
    //endregion

    override fun setOnDialogResultListener(listener: (AppCompatDialogFragment, T?) -> Unit) = also {
        _onDialogResultListener = listener
    }

    /**
     * 防止因Can not perform this action after onSaveInstanceState閃退
     */
    override fun show(manager: FragmentManager, tag: String?) {
        val transaction = manager.beginTransaction()
        transaction.add(this, tag)
        transaction.commitAllowingStateLoss()
    }
}