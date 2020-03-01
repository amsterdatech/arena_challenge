package br.com.flyingdutchman.arena_challenge.ui.features

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.flyingdutchman.arena_challenge.*
import br.com.flyingdutchman.arena_challenge.extensions.hide
import br.com.flyingdutchman.arena_challenge.extensions.show
import br.com.flyingdutchman.arena_challenge.extensions.snackBar
import br.com.flyingdutchman.arena_challenge.presentation.RepositoyViewModel
import br.com.flyingdutchman.arena_challenge.presentation.ViewState
import br.com.flyingdutchman.arena_challenge.ui.RepositoryAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepositoryListActivity : AppCompatActivity() {

    private val adapter by lazy {
        RepositoryAdapter {

        }
    }

    private val viewModel: RepositoyViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.viewState.observe(this, Observer { state ->

            when (state.status) {
                ViewState.Status.LOADING -> {
                    activity_results_loading.show()
                    activity_results_recycler_view.hide()
                }

                ViewState.Status.ERROR -> {
                    activity_results_loading.hide()
                    activity_content_root
                        .snackBar(
                            snackBarText = "Error",
                            listener = {
                                viewModel.loadRepositories()
                            }
                        )
                        .show()
                }

                ViewState.Status.SUCCESS -> {
                    activity_results_loading.hide()
                    activity_results_recycler_view.show()
                    state.data?.let {
                        adapter.updateItems(it)
                    }
                }
            }
        })

        viewModel.loadRepositories()

        setupRecyclerView()
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
            layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(
                    this@RepositoryListActivity,
                    androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                    false
                )
            addItemDecoration(dividerDecor)
            adapter = this@RepositoryListActivity.adapter
        }
    }
}
