package br.com.flyingdutchman.arena_challenge.ui

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class EndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener {

    companion object {
        const val PAGE_START = 1
        const val PER_PAGE = 10
    }

    private var paginationCallback: PaginationCallback? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    constructor(layoutManager: LinearLayoutManager, paginationCallback: PaginationCallback) {
        this.layoutManager = layoutManager
        this.paginationCallback = paginationCallback
    }


    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = layoutManager?.itemCount ?: 0

        val lastVisibleItem: Int = when (layoutManager) {
            is GridLayoutManager -> (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            else -> (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }


        if (paginationCallback?.isLoading() == false && paginationCallback?.hasNextPage() == true && (lastVisibleItem + 1) == totalItemCount) {
            paginationCallback?.loadMore(paginationCallback?.nextPage() ?: PAGE_START)
        }
    }


    fun clear() {
        layoutManager = null
        paginationCallback = null
    }

    interface PaginationCallback {
        fun loadMore(page: Int)

        fun nextPage(): Int

        fun hasNextPage(): Boolean

        fun isLoading(): Boolean
    }
}