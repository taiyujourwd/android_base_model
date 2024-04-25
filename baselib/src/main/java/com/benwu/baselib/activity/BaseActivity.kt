package com.benwu.baselib.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.benwu.baselib.R
import com.benwu.baselib.application.BaseApplication
import com.benwu.baselib.dialog.LoadingDialog
import com.benwu.baselib.extension.getIntentWithSingleTop
import com.benwu.baselib.extension.openActivity
import com.benwu.baselib.extension.toast
import com.benwu.baselib.utils.IUiInit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseActivity<V : ViewBinding> : AppCompatActivity(), IUiInit<V> {

    protected val loadingDialog by lazy {
        LoadingDialog(mActivity)
    }

    private lateinit var _binding: V

    private var backPressedTime = 0L // 按返回時間

    override val mActivity get() = this

    override val mApplication get() = application as BaseApplication

    override val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        setEnableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 解決當使用者撤銷權限 app重啟不會回首頁
        if (mApplication.isStartWithHome()) { // app重啟畫面 == 首頁
            _binding = bindViewBinding(layoutInflater).also { setContentView(it.root) }
            isAppearanceLightStatusBars()

            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                setViewPadding(v, insets.getInsets(WindowInsetsCompat.Type.systemBars()))
                insets
            }

            intent.extras?.also { getBundle(it) }
            initView()
            observer()

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBack()
                }
            })

            binding.root.setOnClickListener(this)
        } else { // app重啟畫面 != 首頁
            openActivity(mApplication.getHomeActivity())
            ActivityCompat.finishAffinity(mActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    protected open fun setEnableEdgeToEdge() {
        val statusBarStyle = mApplication.getStatusBarStyle()
        val navigationBarStyle = mApplication.getNavigationBarStyle()

        if (statusBarStyle != null && navigationBarStyle != null) {
            enableEdgeToEdge(
                statusBarStyle = statusBarStyle,
                navigationBarStyle = navigationBarStyle
            )
        } else if (statusBarStyle != null) {
            enableEdgeToEdge(statusBarStyle = statusBarStyle)
        } else if (navigationBarStyle != null) {
            enableEdgeToEdge(navigationBarStyle = navigationBarStyle)
        } else {
            enableEdgeToEdge()
        }
    }

    protected open fun isAppearanceLightStatusBars() {
        mApplication.isAppearanceLightStatusBars()?.also {
            WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = it
        }
    }

    protected open fun setViewPadding(v: View, insets: Insets) {
        v.setPadding(insets.left, insets.top, insets.right, insets.bottom)
    }

    protected fun viewScope(scope: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                scope.invoke(this)
            }
        }
    }

    /**
     * 返回上一頁
     * 回傳資料給請求頁
     *
     * @param resultCode 回傳代碼
     */
    protected fun finishActivityForResult(resultCode: Int, bundle: Bundle? = null) {
        setResult(resultCode, getIntentWithSingleTop(bundle = bundle))
        finish()
    }

    /**
     * 退出app
     */
    protected fun exitApp() {
        if (System.currentTimeMillis() - backPressedTime >= 2000) { // 再按一次退出
            getString(R.string.exit_app).toast(mActivity).show()
            backPressedTime = System.currentTimeMillis()
        } else {
            ActivityCompat.finishAffinity(mActivity)
        }
    }

    /**
     * 返回
     */
    open fun onBack() {
        finish()
    }
}