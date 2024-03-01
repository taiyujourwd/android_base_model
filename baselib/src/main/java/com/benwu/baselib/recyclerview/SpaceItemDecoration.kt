package com.benwu.baselib.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    private val spacing: Int,
    private val orientation: Int = RecyclerView.VERTICAL,
    private val spanCount: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (spacing <= 0) return

        val manager = parent.layoutManager

        if (manager is GridLayoutManager) { // grid
            (view.layoutParams as GridLayoutManager.LayoutParams).also {
                outRect.left = it.spanIndex * spacing / spanCount
                outRect.right = spacing - (it.spanIndex + it.spanSize) * spacing / spanCount
            }

            if (parent.getChildAdapterPosition(view) >= spanCount) {
                outRect.top = spacing
                return
            }

            var position = 0
            var spanSize = 0

            for (i in 0 until (parent.adapter?.itemCount ?: 0)) {
                position += 1
                spanSize += (parent.layoutManager as GridLayoutManager).spanSizeLookup.getSpanSize(i)
                if (spanSize >= spanCount) break
            }

            if (parent.getChildAdapterPosition(view) >= position) outRect.top = spacing
        } else { // linear
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) return

            when (orientation) {
                RecyclerView.HORIZONTAL -> { // 橫向
                    outRect.left = spacing
                }

                RecyclerView.VERTICAL -> { // 縱向
                    outRect.top = spacing
                }
            }
        }
    }
}