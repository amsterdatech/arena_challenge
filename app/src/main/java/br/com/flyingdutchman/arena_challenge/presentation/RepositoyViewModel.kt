package br.com.flyingdutchman.arena_challenge.presentation

import androidx.lifecycle.*
import br.com.flyingdutchman.arena_challenge.data.GithubRepository
import br.com.flyingdutchman.arena_challenge.ui.features.repositories.Repository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class RepositoyViewModel(
    private val repository: GithubRepository,
    private var mainScheduler: Scheduler
) :
    ViewModel(), LifecycleObserver {

    val viewState: MutableLiveData<ViewState<List<Repository>>> by lazy {
        MutableLiveData<ViewState<List<Repository>>>()
    }


    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun loadRepositories(page: Int = 1) {
        if (viewState.value == null) {
            viewState.value =
                ViewState(
                    status = ViewState.Status.LOADING
                )
        }

        repository
            .searchRepos(page)
            .observeOn(mainScheduler)
            .subscribe(
                { result ->
                    viewState.value =
                        ViewState(
                            status = ViewState.Status.SUCCESS,
                            data = result.map {
                                Repository(
                                    it.id,
                                    it.repoName,
                                    it.repoDescription,
                                    it.ownerName,
                                    it.ownerAvatar,
                                    it.forksCount,
                                    it.starsCount
                                )
                            })

                },
                {
                    viewState.value =
                        ViewState(
                            ViewState.Status.ERROR,
                            error = it
                        )

                })
            .apply {
                compositeDisposable.add(this)
            }
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}