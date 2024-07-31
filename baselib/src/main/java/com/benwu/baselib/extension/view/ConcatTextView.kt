package com.benwu.baselib.extension.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.benwu.baselib.R
import com.google.android.material.textview.MaterialTextView

class ConcatTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ConcatTextView)

        text = TextUtils.concat(
            attributes.getString(R.styleable.ConcatTextView_prefixText) ?: "",
            text,
            attributes.getString(R.styleable.ConcatTextView_suffixText) ?: ""
        )

        attributes.recycle()
    }
}