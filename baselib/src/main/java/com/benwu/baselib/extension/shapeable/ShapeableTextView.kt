package com.benwu.baselib.extension.shapeable

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.ViewOutlineProvider
import com.benwu.baselib.R
import com.benwu.baselib.extension.getAttrValue
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.textview.MaterialTextView

open class ShapeableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShapeableTextView)

        val strokeWidth = attributes.getDimension(R.styleable.ShapeableTextView_strokeWidth, 0f)
        val strokeColor =
            attributes.getColor(R.styleable.ShapeableTextView_strokeColor, Color.TRANSPARENT)
        val shapeAppearance =
            attributes.getResourceId(R.styleable.ShapeableTextView_shapeAppearance, 0)
        val shapeAppearanceOverlay =
            attributes.getResourceId(R.styleable.ShapeableTextView_shapeAppearanceOverlay, 0)

        if (shapeAppearance != 0 || shapeAppearanceOverlay != 0) {
            val color = when (val drawable = background) {
                is ColorDrawable -> {
                    drawable.color
                }

                else -> {
                    context.getAttrValue(com.google.android.material.R.attr.colorSurface)
                }
            }

            background = MaterialShapeDrawable(
                ShapeAppearanceModel.builder(context, shapeAppearance, shapeAppearanceOverlay)
                    .build()
            ).also {
                it.setStroke(strokeWidth, strokeColor)
                it.fillColor = ColorStateList.valueOf(color)
            }

            outlineProvider = ViewOutlineProvider.BACKGROUND
            clipToOutline = true
        }

        attributes.recycle()
    }
}