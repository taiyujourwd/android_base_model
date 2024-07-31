package com.benwu.baselib.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.WindowInsetsCompat
import com.benwu.baselib.R
import com.benwu.baselib.databinding.ActivityWebViewBinding
import com.benwu.baselib.extension.init
import com.benwu.baselib.extension.showNotice

class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {

    private val wvLoad get() = binding.wvLoad
    private val wv get() = binding.wv

    private var toolbarTitle = "" // toolbar標題
    private var url = "" // 網址

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

    override fun onDestroy() {
        binding.root.removeView(wv)
        wv.destroy()

        super.onDestroy()
    }
    //endregion

    //region 初始化
    override fun bindViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = ActivityWebViewBinding.inflate(inflater)

    override fun getBundle(bundle: Bundle) {
        toolbarTitle = bundle.getString("title", "")
        url = bundle.getString("data", "")
    }

    override fun initView() {
        binding.toolbar.init(mActivity, toolbarTitle)

        wv.init().loadUrl(url)

        wv.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) { // 加載開始
                super.onPageStarted(view, url, favicon)
                wvLoad.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) { // 加載完成
                super.onPageFinished(view, url)
                wvLoad.hide()
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                showNotice(
                    mActivity,
                    getString(R.string.open_web),
                    positive = getString(R.string.yes),
                    negative = getString(R.string.no)
                ) {
                    when (it) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            handler?.proceed()
                        }

                        else -> {
                            handler?.cancel()
                        }
                    }
                }
            }
        }

        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                wvLoad.progress = newProgress
            }
        }
    }

    override fun getData() {
        //
    }

    override fun observer() {
        //
    }
    //endregion

    override fun setViewPadding(v: View, windowInsets: WindowInsetsCompat) {
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(insets.left, insets.top, insets.right, 0)
    }

    override fun onBack() {
        if (wv.canGoBack()) wv.goBack() else super.onBack()
    }
}