package com.benwu.baselib.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.application.BaseApplication
import com.benwu.baselib.dialog.LoadingDialog
import com.benwu.baselib.utils.IUiInit

abstract class BaseFragment<V : ViewBinding> : Fragment(), IUiInit<V> {

    protected val loadingDialog by lazy {
        LoadingDialog(_mActivity)
    }

    private lateinit var _mActivity: BaseActivity<*>

    private lateinit var _binding: V

    override val mActivity get() = _mActivity

    override val mApplication get() = _mActivity.application as BaseApplication

    override val binding get() = _binding

    //region 生命週期
    override fun onAttach(context: Context) {
        super.onAttach(context)

        _mActivity = context as BaseActivity<*>
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
}