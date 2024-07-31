package com.benwu.baselib.extension.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.benwu.baselib.R
import com.benwu.baselib.databinding.IncludeLoadingBinding
import com.benwu.baselib.extension.hideKeyboard

class LoadingView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes), View.OnClickListener {

    private val binding: IncludeLoadingBinding =
        IncludeLoadingBinding.inflate(LayoutInflater.from(context), this)

    private var retry: (() -> Unit)? = null

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)

        setMessage(
            attributes.getString(R.styleable.LoadingView_message)
                ?: context.getString(R.string.api_error)
        )
        setRetryButton(
            attributes.getString(R.styleable.LoadingView_retryButton)
                ?: context.getString(R.string.retry)
        )

        binding.retry.setOnClickListener(this)

        attributes.recycle()
    }

    /**
     * 顯示/隱藏讀取
     *
     * @param loading 顯示/隱藏
     */
    fun toggleLoading(loading: Boolean) {
        isVisible = loading
        binding.progress.isVisible = loading
        binding.error.isVisible = false
    }

    /**
     * 顯示/隱藏錯誤UI
     *
     * @param error 顯示/隱藏
     */
    fun toggleError(error: Boolean) {
        isVisible = error
        binding.progress.isVisible = false
        binding.error.isVisible = error
    }

    /**
     * 顯示/隱藏讀取與錯誤UI
     *
     * @param loading 顯示/隱藏讀取
     * @param error 顯示/隱藏錯誤UI
     */
    fun toggle(loading: Boolean = false, error: Boolean = false) {
        toggleLoading(loading)
        toggleError(error)
    }

    /**
     * 設置訊息
     *
     * @param message 訊息
     */
    fun setMessage(message: String) {
        binding.message.text = message
    }

    /**
     * 設置重試按鈕名稱
     *
     * @param name 名稱
     */
    fun setRetryButton(name: String) {
        binding.retry.text = name
    }

    /**
     * 重試
     */
    fun retry(action: () -> Unit) {
        retry = action
    }

    override fun onClick(v: View?) {
        hideKeyboard(context, v)
        if (v != binding.retry) return
        retry?.invoke()
    }
}