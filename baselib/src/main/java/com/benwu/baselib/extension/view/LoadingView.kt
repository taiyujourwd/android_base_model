package com.benwu.baselib.extension.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.benwu.baselib.R
import com.benwu.baselib.databinding.MergeLoadingBinding
import com.benwu.baselib.extension.getOrDefault
import com.benwu.baselib.extension.hideKeyboard
import com.benwu.baselib.extension.isNullOrEmpty

class LoadingView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes), View.OnClickListener {

    private val binding = MergeLoadingBinding.inflate(LayoutInflater.from(context), this)

    private var _retry: (() -> Unit)? = null

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)

        binding.message.text = attributes.getString(R.styleable.LoadingView_message)
            .getOrDefault(context.getString(R.string.api_error))

        binding.retry.text = attributes.getString(R.styleable.LoadingView_retryButton)
            .getOrDefault(context.getString(R.string.retry))

        binding.retry.setOnClickListener(this)

        attributes.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?) = true

    /**
     * 顯示/隱藏讀取與錯誤UI
     *
     * @param loading 顯示/隱藏讀取
     * @param error 顯示/隱藏錯誤UI
     * @param message 訊息
     * @param buttonName 按鈕名稱
     */
    fun toggle(
        loading: Boolean = false,
        error: Boolean = false,
        message: String? = null,
        buttonName: String? = null,
        retry: (() -> Unit)? = null
    ) {
        isVisible = loading || error
        binding.progress.isVisible = loading
        binding.error.isVisible = error
        if (!isNullOrEmpty(message)) binding.message.text = message
        if (!isNullOrEmpty(buttonName)) binding.retry.text = buttonName
        _retry = retry
    }

    override fun onClick(v: View?) {
        hideKeyboard(context, v)
        if (v != binding.retry) return
        _retry?.invoke()
    }
}