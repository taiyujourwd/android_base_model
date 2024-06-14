package com.benwu.baselib.extension.view

import android.content.Context
import androidx.annotation.IntRange
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 解決recyclerView刷新加載閃退BUG
 * 因使用新的diff工具進行刷新加載
 * 而diff工具會使用notifyItem RangeInserted、RangeRemoved、Moved、RangeChanged...等刷新加載方法
 * 這些方法都會讓recyclerView有機會在刷新加載時閃退
 *
 * https://stackoverflow.com/questions/46563485/diffresult-dispatching-lead-to-inconsistency-detected-invalid-view-holder-adap
 */
class BaseGridLayoutManager(
    context: Context,
    @IntRange(from = 2) spanCount: Int,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL
) : GridLayoutManager(context, spanCount, orientation, false) {

    override fun supportsPredictiveItemAnimations() = false
}