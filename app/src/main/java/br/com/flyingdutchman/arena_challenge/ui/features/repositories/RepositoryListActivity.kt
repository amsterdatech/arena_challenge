package br.com.flyingdutchman.arena_challenge.ui.features.repositories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.flyingdutchman.arena_challenge.*
import br.com.flyingdutchman.arena_challenge.extensions.hide
import br.com.flyingdutchman.arena_challenge.extensions.show
import br.com.flyingdutchman.arena_challenge.extensions.snackBar
import br.com.flyingdutchman.arena_challenge.presentation.RepositoyViewModel
import br.com.flyingdutchman.arena_challenge.presentation.ViewState
import br.com.flyingdutchman.arena_challenge.ui.EndlessRecyclerViewScrollListener
import br.com.flyingdutchman.arena_challenge.ui.EndlessRecyclerViewScrollListener.Companion.PAGE_START
import br.com.flyingdutchman.arena_challenge.ui.features.issues.IssueListActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepositoryListActivity : AppCompatActivity(),
    EndlessRecyclerViewScrollListener.PaginationCallback {

    private var hasNextPage = true
    private var isLoading = false
    private var nextPage = PAGE_START
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)


        setupObservers()
        setupRecyclerView()

        if (adapter.items.isEmpty()) {
            viewModel.loadRepositories(nextPage)
        }
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
//                    activity_results_recycler_view.hide()
                }

                ViewState.Status.ERROR -> {
                    activity_results_loading.hide()
                    activity_content_root
                        .snackBar(
                            snackBarText = "Error",
                            listener = {
                                viewModel.loadRepositories(nextPage)
                            }
                        )
                        .show()
                }

                ViewState.Status.SUCCESS -> {
                    activity_results_loading.hide()
                    adapter.updateItems(state.data ?: emptyList())
                    this.nextPage++
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

    override fun nextPage(): Int = nextPage

    override fun hasNextPage(): Boolean = hasNextPage

    override fun isLoading(): Boolean = isLoading

    override fun loadMore(page: Int) {
        isLoading = true
        viewModel.loadRepositories(page)
    }
}
