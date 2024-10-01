package com.benwu.baselib.extension.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.benwu.baselib.R
import com.benwu.baselib.extension.getOrDefault
import com.benwu.baselib.extension.isNullOrEmpty
import com.google.android.material.textview.MaterialTextView

class ConcatTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {

    private val _prefixText: String
    private val _suffixText: String

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ConcatTextView)

        _prefixText = attributes.getString(R.styleable.ConcatTextView_prefixText).getOrDefault("")
        _suffixText = attributes.getString(R.styleable.ConcatTextView_suffixText).getOrDefault("")
        setConcatText(text)

        attributes.recycle()
    }

    fun setConcatText(
        text: CharSequence?,
        prefixText: String = _prefixText,
        suffixText: String = _suffixText
    ) {
        if (isNullOrEmpty(text)) {
            this.text = text
            return
        }

        this.text = TextUtils.concat(prefixText, text, suffixText)
    }
}