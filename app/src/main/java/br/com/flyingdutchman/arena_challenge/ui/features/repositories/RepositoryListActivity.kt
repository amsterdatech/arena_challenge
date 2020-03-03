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
import br.com.flyingdutchman.arena_challenge.presentation.livedata.SingleLiveEvent
import br.com.flyingdutchman.arena_challenge.ui.EndlessRecyclerViewScrollListener
import br.com.flyingdutchman.arena_challenge.ui.EndlessRecyclerViewScrollListener.Companion.PAGE_START
import br.com.flyingdutchman.arena_challenge.ui.EndlessRecyclerViewScrollListener.Companion.PER_PAGE
import br.com.flyingdutchman.arena_challenge.ui.features.issues.IssueListActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepositoryListActivity : AppCompatActivity(),
    EndlessRecyclerViewScrollListener.PaginationCallback {

    private var isLoading = false
    private var currentPage = PAGE_START
    private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null

    private val adapter by lazy {
        RepositoryAdapter {
            startActivity(IssueListActivity.createIntent(this, it))
        }.apply {
            setHasStableIds(true)
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
        const val REPO_CURRENT_ITEMS = "REPO_CURRENT_ITEMS"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupObservers()

        savedInstanceState?.let {
            if (it.containsKey(REPO_CURRENT_ITEMS)) {
                val savedState: List<Repository> =
                    savedInstanceState.getParcelableArrayList(REPO_CURRENT_ITEMS)

                currentPage = PAGE_START + 1
                adapter.clear()
                viewModel.successState.value = savedState
            }

        } ?: run {
            if (viewModel.successState.value == null) {
                currentPage = PAGE_START
                viewModel.loadRepositories(currentPage)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (adapter.items.isNotEmpty()) outState.putParcelableArrayList(
            REPO_CURRENT_ITEMS,
            adapter.items.slice(0 until PER_PAGE) as ArrayList
        )

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        endlessRecyclerViewScrollListener?.clear()
    }

    private fun setupObservers() {
        viewModel.loadingState.observe(this, Observer {
            handleLoading()
        })

        viewModel.errorState.observe(this, Observer {
            isLoading = false
            handleError(it)
            if (currentPage > 2) {
                currentPage -= 1
            }
        })

        viewModel.successState.observe(this, Observer {
            isLoading = false
            activity_results_loading.hide()
            adapter.updateItems(it)
        })
    }

    private fun handleLoading() {
        if (currentPage == PAGE_START) activity_results_loading.show()
    }

    private fun handleError(state: SingleLiveEvent<Throwable>) {
        state.getContentIfNotHandled()?.let {
            activity_results_loading.hide()
            activity_content_root
                .snackBar(
                    snackBarText = "Error ${it.message}",
                    listener = {
                        viewModel.loadRepositories(currentPage)
                    }
                )
                .show()
        }
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


    override fun hasNextPage(): Boolean = currentPage < 10

    override fun isLoading(): Boolean = isLoading

    override fun loadMore() {
        isLoading = true
        currentPage += 1
        viewModel.loadRepositories(currentPage)
    }
}
