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
        const val REPO_PAGE = "REPO_PAGE"
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

            if (it.containsKey(REPO_PAGE)) {
                viewModel.nextPage = savedInstanceState.getInt(REPO_PAGE)
            }
        } ?: run {
            lifecycle.addObserver(viewModel)
        }


        setupObservers()
        setupRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (adapter.items.isNotEmpty()) outState.putParcelableArrayList(
            REPO_RESULT,
            adapter.items.slice(0 until PER_PAGE) as ArrayList
        )

        outState.putInt(REPO_PAGE, viewModel.nextPage)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        endlessRecyclerViewScrollListener?.clear()
    }

    private fun setupObservers() {
        viewModel.viewState.observe(this, Observer { state ->
            when (state.status) {
                ViewState.Status.LOADING -> {
                    activity_results_loading.show()
                    isLoading = true
                }

                ViewState.Status.ERROR -> {
                    activity_results_loading.hide()
                    activity_content_root
                        .snackBar(
                            snackBarText = "Error ${(state.error as Throwable).message}",
                            listener = {
                                viewModel.loadRepositories()
                            }
                        )
                        .show()

                    isLoading = false
                }

                ViewState.Status.SUCCESS -> {
                    activity_results_loading.hide()
                    adapter.updateItems(state.data ?: emptyList())
                    viewModel.nextPage++
                    isLoading = false
                }
            }
        })
    }

    private fun setupRecyclerView() {
        val dividerDecor =
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        dividerDecor.setDrawable(
            ContextCompat.getDrawable(this, R.drawable.divider)
                ?: getDrawable(R.drawable.divider)
        )
        with(activity_results_recycler_view) {
            setHasFixedSize(true)

            layoutManager = verticalLayoutManager

            addItemDecoration(dividerDecor)
            adapter = this@RepositoryListActivity.adapter

            endlessRecyclerViewScrollListener = EndlessRecyclerViewScrollListener(
                verticalLayoutManager,
                this@RepositoryListActivity
            )

            addOnScrollListener(endlessRecyclerViewScrollListener as EndlessRecyclerViewScrollListener)
        }

    }

    override fun nextPage(): Int = viewModel.nextPage

    override fun hasNextPage(): Boolean = hasNextPage

    override fun isLoading(): Boolean = isLoading

    override fun loadMore(page: Int) {
        viewModel.loadRepositories()
    }
}
