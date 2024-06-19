package com.benwu.baselib.extension.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.benwu.baselib.R
import com.benwu.baselib.extension.shapeable.ShapeableTextView

class ConcatTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableTextView(context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ConcatTextView)

        text = TextUtils.concat(
            attributes.getString(R.styleable.ConcatTextView_startText) ?: "",
            text,
            attributes.getString(R.styleable.ConcatTextView_endText) ?: ""
        )

        attributes.recycle()
    }
}