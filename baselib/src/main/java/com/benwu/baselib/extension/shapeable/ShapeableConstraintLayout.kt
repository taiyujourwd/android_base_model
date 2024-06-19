package com.benwu.baselib.extension.shapeable

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import com.benwu.baselib.R
import com.benwu.baselib.extension.getAttrValue
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

open class ShapeableConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.ShapeableConstraintLayout)

        val strokeWidth =
            attributes.getDimension(R.styleable.ShapeableConstraintLayout_strokeWidth, 0f)
        val strokeColor = attributes.getColor(
            R.styleable.ShapeableConstraintLayout_strokeColor,
            Color.TRANSPARENT
        )
        val shapeAppearance =
            attributes.getResourceId(R.styleable.ShapeableConstraintLayout_shapeAppearance, 0)
        val shapeAppearanceOverlay = attributes.getResourceId(
            R.styleable.ShapeableConstraintLayout_shapeAppearanceOverlay,
            0
        )

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