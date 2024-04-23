package com.benwu.baselib.dialog_fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.application.BaseApplication
import com.benwu.baselib.dialog.LoadingDialog
import com.benwu.baselib.utils.IDialogResult
import com.benwu.baselib.utils.IUiInit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseBottomSheetDialogFragment<T, V : ViewBinding> : BottomSheetDialogFragment(),
    IUiInit<V>, IDialogResult<T> {

    protected val loadingDialog by lazy {
        LoadingDialog(mActivity)
    }

    private lateinit var _mActivity: BaseActivity<*>

    private lateinit var _binding: V

    private var _onDialogResultListener: ((AppCompatDialogFragment, T?) -> Unit)? = null

    protected lateinit var mDialog: Dialog
        private set

    protected var mWindow: Window? = null
        private set

    protected var isExpanded = false // 是否展開

    override val mActivity get() = _mActivity

    override val mApplication get() = mActivity.application as BaseApplication

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

        mWindow?.setBackgroundDrawable(null)

        val bottomSheet =
            mDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        // 是否展開
        if (isExpanded) {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }

        arguments?.also { getBundle(it) }
        initView()
        getData()
        observer()
    }
    //endregion

    override fun setOnDialogResultListener(
        listener: ((AppCompatDialogFragment, T?) -> Unit)
    ) = also {
        _onDialogResultListener = listener
    }

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