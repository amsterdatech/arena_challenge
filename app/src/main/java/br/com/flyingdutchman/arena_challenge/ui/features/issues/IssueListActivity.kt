package br.com.flyingdutchman.arena_challenge.ui.features.issues

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import br.com.flyingdutchman.arena_challenge.R
import br.com.flyingdutchman.arena_challenge.extensions.hide
import br.com.flyingdutchman.arena_challenge.extensions.show
import br.com.flyingdutchman.arena_challenge.extensions.snackBar
import br.com.flyingdutchman.arena_challenge.presentation.livedata.SingleLiveEvent
import br.com.flyingdutchman.arena_challenge.ui.common.DividerItemDecoration
import br.com.flyingdutchman.arena_challenge.ui.features.repositories.Repository
import br.com.flyingdutchman.arena_challenge.presentation.IssuesViewModel
import kotlinx.android.synthetic.main.activity_issue_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class IssueListActivity : AppCompatActivity() {
    private val adapter by lazy {
        IssuesAdapter {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
            )
        }
    }

    var repository: Repository? = null


    private val viewModel: IssuesViewModel by viewModel()

    companion object {

        const val REPOSITORY = "repository"

        @JvmStatic
        fun createIntent(context: Context, repo: Repository): Intent {
            return Intent(context, IssueListActivity::class.java).apply {
                putExtras(bundleOf(REPOSITORY to repo))
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_issue_list)

        repository = intent?.extras?.getParcelable<Repository>(REPOSITORY)

        setupObservers()

        viewModel.loadIssues(
            repository?.ownerName ?: "",
            repository?.repoName ?: ""
        )

        setupToolbar()
        setupRecyclerView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupObservers() {
        viewModel.success.observe(this, Observer {
            handleSuccess(it)
        })

        viewModel.error.observe(this, Observer {
            handleError(it)
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                activity_issues_loading.show()
            } else {
                activity_issues_loading.hide()
            }
        })
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = repository?.repoName
    }

    private fun setupRecyclerView() {
        val dividerDecor =
            DividerItemDecoration(
                this,
                VERTICAL
            )
        dividerDecor.setDrawable(
            ContextCompat.getDrawable(this, R.drawable.divider)
                ?: getDrawable(R.drawable.divider)
        )
        with(activity_issues_recycler_view) {
            setHasFixedSize(true)
            layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(
                    this@IssueListActivity,
                    androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                    false
                )
            addItemDecoration(dividerDecor)
            adapter = this@IssueListActivity.adapter
        }
    }

    private fun handleError(it: SingleLiveEvent<Throwable>?) {
        it?.getContentIfNotHandled()?.let {
            activity_issues_recycler_view.hide()
            activity_issues_loading.hide()
            activity_issues_root
                .snackBar(getString(R.string.generic_error)) {
                    viewModel.loadIssues(
                        repository?.ownerName ?: "",
                        repository?.repoName ?: ""
                    )
                }
                .show()
        }
    }

    private fun handleSuccess(it: List<Issue>) {
        activity_issues_recycler_view.show()
        adapter.updateItems(it)
    }
}
