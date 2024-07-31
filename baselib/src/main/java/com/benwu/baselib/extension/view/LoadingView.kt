package com.benwu.baselib.extension.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.benwu.baselib.R
import com.benwu.baselib.databinding.IncludeLoadingBinding

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: IncludeLoadingBinding =
        IncludeLoadingBinding.inflate(LayoutInflater.from(context), this)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)

        setMessage(attributes.getString(R.styleable.LoadingView_message) ?: "")

        attributes.recycle()
    }

    /**
     * 顯示/隱藏讀取
     *
     * @param loading 顯示/隱藏
     */
    fun toggleLoading(loading: Boolean) {
        isVisible = loading
    }

    /**
     * 顯示/隱藏錯誤UI
     *
     * @param error 顯示/隱藏
     */
    fun toggleError(error: Boolean) {
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
}