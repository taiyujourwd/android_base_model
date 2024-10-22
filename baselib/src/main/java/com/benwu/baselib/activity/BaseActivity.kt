package com.benwu.baselib.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.application.BaseApplication
import com.benwu.baselib.dialog.LoadingDialog
import com.benwu.baselib.extension.getIntentWithSingleTop
import com.benwu.baselib.extension.lifecycleScope
import com.benwu.baselib.extension.openActivity
import com.benwu.baselib.utils.IUiInit

abstract class BaseActivity<V : ViewBinding> : AppCompatActivity(), IUiInit<V> {

    val loadingDialog by lazy { LoadingDialog(mActivity) }

    private lateinit var _mApplication: BaseApplication
    private lateinit var _binding: V

    override val mActivity get() = this

    override val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mApplication = application as BaseApplication
        setEnableEdgeToEdge()

        // 解決當使用者撤銷權限 app重啟不會回首頁
        if (_mApplication.isStartWithHome()) { // app重啟畫面 == 首頁
            _binding = bindViewBinding(layoutInflater).also { setContentView(it.root) }

            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                setViewPadding(v, insets)
                insets
            }

            intent.extras?.also { getBundle(it) }
            initView()
            observer()
            lifecycleScope(dataWithLifecycleState) { getData() }

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBack()
                }
            })

            setOnClickListeners(binding.root)
        } else { // app重啟畫面 != 首頁
            openActivity(mActivity, _mApplication.getHomeActivity())
            ActivityCompat.finishAffinity(mActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.toggle(false)
    }

    protected open fun setEnableEdgeToEdge() {
        enableEdgeToEdge(
            statusBarStyle = _mApplication.getStatusBarStyle(),
            navigationBarStyle = _mApplication.getNavigationBarStyle()
        )
    }

    protected open fun setViewPadding(v: View, windowInsets: WindowInsetsCompat) {
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(insets.left, insets.top, insets.right, insets.bottom)
    }

    /**
     * 返回上一頁
     * 回傳資料給請求頁
     *
     * @param resultCode 回傳代碼
     */
    protected fun finishActivityForResult(resultCode: Int, bundle: Bundle? = null) {
        setResult(resultCode, getIntentWithSingleTop(mActivity, bundle = bundle))
        finish()
    }

    /**
     * 返回
     */
    open fun onBack() {
        finish()
    }
}