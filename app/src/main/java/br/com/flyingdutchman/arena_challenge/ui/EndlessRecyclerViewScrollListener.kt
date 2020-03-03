package br.com.flyingdutchman.arena_challenge.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class EndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener {

    companion object {
        const val PAGE_START = 1
        const val PER_PAGE = 20
    }

    private var paginationCallback: PaginationCallback? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    constructor(layoutManager: LinearLayoutManager, paginationCallback: PaginationCallback) {
        this.layoutManager = layoutManager
        this.paginationCallback = paginationCallback
    }


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = (layoutManager as LinearLayoutManager).itemCount
        val visibleItemCount = (layoutManager as LinearLayoutManager).childCount
        val firstVisibleItemPosition: Int =
            (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        if (paginationCallback?.isLoading() == false &&
            paginationCallback?.hasNextPage() == true
        ) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                && firstVisibleItemPosition >= 0
            ) {
                paginationCallback?.loadMore()
            }
        }
    }


    fun clear() {
        layoutManager = null
        paginationCallback = null
    }

    interface PaginationCallback {
        fun loadMore()

        fun hasNextPage(): Boolean

        fun isLoading(): Boolean
    }
}