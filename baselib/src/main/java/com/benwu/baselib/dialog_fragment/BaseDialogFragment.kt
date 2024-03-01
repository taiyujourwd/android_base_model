package com.benwu.baselib.dialog_fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.application.BaseApplication
import com.benwu.baselib.dialog.LoadingDialog
import com.benwu.baselib.utils.IUiInit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseDialogFragment<V : ViewBinding> : AppCompatDialogFragment(), IUiInit<V> {

    protected val loadingDialog by lazy {
        LoadingDialog(_mActivity)
    }

    private lateinit var _mActivity: BaseActivity<*>

    private lateinit var _binding: V

    protected lateinit var mDialog: Dialog
        private set

    protected var mWindow: Window? = null
        private set

    protected var height = ViewGroup.LayoutParams.WRAP_CONTENT // 高度
    protected var isWindowMatch = false // 是否與螢幕同寬

    override val mActivity get() = _mActivity

    override val mApplication get() = _mActivity.application as BaseApplication

    override val binding get() = _binding

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
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mWindow?.setBackgroundDrawable(null)

        // 調整View占用屏幕比例
        mWindow?.setLayout(
            if (isWindowMatch) { // 滿版
                ViewGroup.LayoutParams.MATCH_PARENT
            } else { // 寬占85%
                (resources.displayMetrics.widthPixels * 0.85).toInt()
            },
            height
        )

        arguments?.also { getBundle(it) }
        initView()
        getData()
        observer()
    }
    //endregion

    protected fun viewScope(scope: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                scope.invoke(this)
            }
        }
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