package com.benwu.baselib.dialog_fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benwu.baselib.R
import com.benwu.baselib.databinding.DialogFragmentWebViewBinding
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.message

class WebViewDialogFragment : BaseBottomSheetDialogFragment<Any, DialogFragmentWebViewBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    private val wvLoad get() = binding.wvLoad
    private val srl get() = binding.srl
    private val wv get() = binding.wv

    private var url = "" // 網址
    private var toolbarTitle = "" // toolbar標題

    //region 生命週期
    override fun onResume() {
        super.onResume()

        wv.onResume()
        wv.resumeTimers()
    }

    override fun onPause() {
        super.onPause()

        wv.onPause()
        wv.pauseTimers()
    }

    override fun onDestroyView() {
        binding.root.removeView(wv)
        wv.destroy()

        super.onDestroyView()
    }
    //endregion

    //region 初始化
    override fun bindViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DialogFragmentWebViewBinding.inflate(inflater, container, false).also {
        isExpanded = true
    }

    override fun getBundle(bundle: Bundle) {
        url = bundle.getString("data", "")
        toolbarTitle = bundle.getString("title", "")
    }

    override fun initView() {
        mDialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (wv.canGoBack()) wv.goBack() else dismiss()
                return@setOnKeyListener true
            }

            false
        }

        binding.includeToolbar.toolbar.init(toolbarTitle, navigationRes = R.drawable.ic_close) {
            dismiss()
        }.background = null

        wv.init().loadUrl(url)

        wv.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) { // 加載開始
                super.onPageStarted(view, url, favicon)

                srl.isEnabled = srl.isRefreshing
                wvLoad.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) { // 加載完成
                super.onPageFinished(view, url)

                srl.isRefreshing = false
                srl.isEnabled = true
                wvLoad.hide()
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                getString(R.string.open_web).message(
                    mActivity,
                    negative = getString(R.string.no),
                    positive = getString(R.string.yes)
                ) {
                    when (it) {
                        DialogInterface.BUTTON_NEGATIVE -> {
                            handler?.cancel()
                        }

                        DialogInterface.BUTTON_POSITIVE -> {
                            handler?.proceed()
                        }
                    }
                }.show()
            }
        }

        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                wvLoad.progress = newProgress
            }
        }

        binding.srl.setOnRefreshListener(this)
    }

    override fun getData() {
        //
    }

    override fun observer() {
        //
    }

    override fun onRefresh() {
        wv.reload()
    }
    //endregion

    companion object {
        fun newInstance(url: String, toolbarTitle: String? = null) =
            WebViewDialogFragment().also { fragment ->
                fragment.arguments = Bundle().also {
                    it.putString("data", url)
                    it.putString("title", toolbarTitle)
                }
            }
    }
}