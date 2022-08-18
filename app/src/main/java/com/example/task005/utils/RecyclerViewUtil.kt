package com.example.task005.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewUtil {
    fun registerForLoadMore(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager,
        loadMore: () -> Unit,
        thresHold: Int = 2
    ) {
        recyclerView
            .addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItem = recyclerView.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                    if ((totalItemCount - ((firstVisibleItem + visibleItem) + thresHold)) <= 0) {
                        loadMore()
                    }
                }
            })
    }
}