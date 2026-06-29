package com.example.androidconcepts.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setDynamicSpacing(spacePx: Int) {
    while (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }

    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) return

            val layoutManager = parent.layoutManager ?: return
            val totalCount = parent.adapter?.itemCount ?: 0

            when (layoutManager) {
                is GridLayoutManager -> {
                    val spanCount = layoutManager.spanCount
                    val column = position % spanCount
                    outRect.left = if (column == 0) 0 else spacePx
                    outRect.right = if (column == spanCount - 1) 0 else spacePx
                    outRect.top = if (position < spanCount) 0 else spacePx
                    outRect.bottom = spacePx
                }
                is LinearLayoutManager -> {
                    val orientation = layoutManager.orientation
                    if (orientation == LinearLayoutManager.VERTICAL) {
                        outRect.top = if (position == 0) 0 else spacePx
                        outRect.bottom = if (position == totalCount - 1) 0 else spacePx
                    } else {
                        outRect.left = if (position == 0) 0 else spacePx
                        outRect.right = if (position == totalCount - 1) 0 else spacePx
                    }
                }
            }
        }
    })
}