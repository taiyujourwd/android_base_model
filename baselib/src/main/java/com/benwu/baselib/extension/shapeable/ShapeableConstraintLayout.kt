package com.benwu.baselib.extension.shapeable

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.benwu.baselib.R
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

open class ShapeableConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var strokeWidth: Float
    private var strokeColor: Int
    private var shapeAppearance: Int
    private var shapeAppearanceOverlay: Int

    private var shapeDrawable: MaterialShapeDrawable

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.ShapeableConstraintLayout)

        strokeWidth = attributes.getDimension(R.styleable.ShapeableConstraintLayout_strokeWidth, 0f)
        strokeColor = attributes.getColor(
            R.styleable.ShapeableConstraintLayout_strokeColor,
            Color.TRANSPARENT
        )
        shapeAppearance = attributes.getResourceId(
            R.styleable.ShapeableConstraintLayout_shapeAppearance,
            com.google.android.material.R.style.ShapeAppearance_Material3_Corner_None
        )
        shapeAppearanceOverlay = attributes.getResourceId(
            R.styleable.ShapeableConstraintLayout_shapeAppearanceOverlay,
            0
        )

        shapeDrawable = buildBackground()

        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                shapeDrawable.bounds = Rect(0, 0, view.width, view.height)
                shapeDrawable.getOutline(outline)
            }
        }

        clipToOutline = true

        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        shapeDrawable.setBounds(0, 0, width, height)
        shapeDrawable.draw(canvas)
    }

    /**
     * 創建背景
     *
     * @return 背景
     */
    private fun buildBackground() = MaterialShapeDrawable(
        ShapeAppearanceModel.builder(context, shapeAppearance, shapeAppearanceOverlay)
            .build()
    ).also {
        it.setStroke(strokeWidth, strokeColor)
        it.fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    /**
     * 設置邊框
     *
     * @param width 寬度
     * @param color 顏色
     */
    fun setStroke(width: Float, @ColorInt color: Int) {
        strokeWidth = width
        strokeColor = color
        shapeDrawable.setStroke(strokeWidth, strokeColor)
        invalidate()
    }

    /**
     * 設置形狀
     *
     * @param shape 形狀
     */
    fun setShapeAppearance(@StyleRes shape: Int) {
        shapeAppearance = shape
        shapeDrawable = buildBackground()
        invalidate()
    }

    /**
     * 設置覆蓋形狀
     *
     * @param shape 形狀
     */
    fun setShapeAppearanceOverlay(@StyleRes shape: Int) {
        shapeAppearanceOverlay = shape
        shapeDrawable = buildBackground()
        invalidate()
    }
}