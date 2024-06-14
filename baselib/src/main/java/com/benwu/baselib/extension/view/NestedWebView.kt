package com.benwu.baselib.extension.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView

/**
 * 解決webView手勢衝突
 */
class NestedWebView : WebView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            super.requestDisallowInterceptTouchEvent(true)
        }

        return super.onTouchEvent(event)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        if (clampedX || clampedY) {
            super.requestDisallowInterceptTouchEvent(false)
        }

        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    }
}