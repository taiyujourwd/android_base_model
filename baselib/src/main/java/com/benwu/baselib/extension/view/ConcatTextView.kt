package com.benwu.baselib.extension.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.benwu.baselib.R
import com.google.android.material.textview.MaterialTextView

class ConcatTextView : MaterialTextView {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ConcatTextView)

        text = TextUtils.concat(
            attributes.getString(R.styleable.ConcatTextView_startText) ?: "",
            text,
            attributes.getString(R.styleable.ConcatTextView_endText) ?: ""
        )

        attributes.recycle()
    }
}