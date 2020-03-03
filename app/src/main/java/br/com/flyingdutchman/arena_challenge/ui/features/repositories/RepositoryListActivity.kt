package br.com.flyingdutchman.arena_challenge.ui.features.repositories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.flyingdutchman.arena_challenge.*
import br.com.flyingdutchman.arena_challenge.extensions.snackBar
import br.com.flyingdutchman.arena_challenge.presentation.RepositoyViewModel
import br.com.flyingdutchman.arena_challenge.presentation.ViewState
import br.com.flyingdutchman.arena_challenge.ui.EndlessRecyclerViewScrollListener
import br.com.flyingdutchman.arena_challenge.ui.EndlessRecyclerViewScrollListener.Companion.PAGE_START
import br.com.flyingdutchman.arena_challenge.ui.EndlessRecyclerViewScrollListener.Companion.PER_PAGE
import br.com.flyingdutchman.arena_challenge.ui.features.issues.IssueListActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepositoryListActivity : AppCompatActivity(),
    EndlessRecyclerViewScrollListener.PaginationCallback {

    private var hasNextPage = true
    private var isLoading = false
    private var currentPage = PAGE_START
    private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null

    private val adapter by lazy {
        RepositoryAdapter {
            startActivity(IssueListActivity.createIntent(this, it))
        }
    }

    private val verticalLayoutManager by lazy {
        LinearLayoutManager(
            this@RepositoryListActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private val viewModel: RepositoyViewModel by viewModel()

    companion object {
        const val REPO_RESULT = "REPO_RESULT"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            if (it.containsKey(REPO_RESULT)) {
                adapter.clear()
                adapter.updateItems(savedInstanceState.getParcelableArrayList(REPO_RESULT))
            }

        } ?: run {
            viewModel.loadRepositories(currentPage)
        }


        setupObservers()
        setupRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (adapter.items.isNotEmpty()) outState.putParcelableArrayList(
            REPO_RESULT,
            adapter.items.slice(0 until PER_PAGE) as ArrayList
        )

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        endlessRecyclerViewScrollListener?.clear()
        adapter.clear()
    }

    private fun setupObservers() {
        viewModel.viewState.observe(this, Observer { state ->
            when (state.status) {
                ViewState.Status.LOADING -> {
                    isLoading = true

                    handleLoading()
                }

                ViewState.Status.ERROR -> {
                    isLoading = false
                    handleError(state)
                }

                ViewState.Status.SUCCESS -> {
                    isLoading = false
                    activity_results_loading.hide()
                    //Add EmptyStateView
                    adapter.updateItems(state.data ?: emptyList())
                }
            }
        })
    }

    private fun handleLoading() {
        activity_results_loading.show()
    }

    private fun handleError(state: ViewState<List<Repository>>) {
        activity_results_loading.hide()
        activity_content_root
            .snackBar(
                snackBarText = "Error ${(state.error as Throwable).message}",
                listener = {
                    viewModel.loadRepositories()
                }
            )
            .show()
    }

    private fun setupRecyclerView() {
        with(activity_results_recycler_view) {
            setHasFixedSize(true)

            layoutManager = verticalLayoutManager

            adapter = this@RepositoryListActivity.adapter

            addItemDecoration(divider())

            endlessRecyclerViewScrollListener = EndlessRecyclerViewScrollListener(
                verticalLayoutManager,
                this@RepositoryListActivity
            )

            addOnScrollListener(endlessRecyclerViewScrollListener as EndlessRecyclerViewScrollListener)
        }

    }

    private fun divider(): DividerItemDecoration {
        val dividerDecor =
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        dividerDecor.setDrawable(
            ContextCompat.getDrawable(this, R.drawable.divider)
                ?: getDrawable(R.drawable.divider)
        )
        return dividerDecor
    }

    override fun nextPage(): Int = currentPage++

    override fun hasNextPage(): Boolean = hasNextPage

    override fun isLoading(): Boolean = isLoading

    override fun loadMore(page: Int) {
        viewModel.loadRepositories(page)
    }
}
