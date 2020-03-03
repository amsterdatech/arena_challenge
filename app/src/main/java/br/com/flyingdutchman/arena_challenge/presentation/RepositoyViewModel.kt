package br.com.flyingdutchman.arena_challenge.presentation

import androidx.lifecycle.*
import br.com.flyingdutchman.arena_challenge.data.GithubRepository
import br.com.flyingdutchman.arena_challenge.data.model.RepoData
import br.com.flyingdutchman.arena_challenge.presentation.livedata.SingleLiveEvent
import br.com.flyingdutchman.arena_challenge.ui.features.repositories.Repository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class RepositoyViewModel(
    private val repository: GithubRepository,
    private var mainScheduler: Scheduler
) :
    ViewModel(), LifecycleObserver {
    val successState = MutableLiveData<List<Repository>>()
    val errorState = MutableLiveData<SingleLiveEvent<Throwable>>()
    val loadingState = MutableLiveData<Boolean>()


    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun loadRepositories(page: Int = 1) {
        loadingState.value = true

        repository
            .searchRepos(page)
            .observeOn(mainScheduler)
            .subscribe(
                { result ->
                    successState.value = mapResultToViewModel(result)
                },
                {
                    errorState.value = SingleLiveEvent(it)

                })
            .apply {
                compositeDisposable.add(this)
            }
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun mapResultToViewModel(result: List<RepoData>): List<Repository> {
        return result.map {
            Repository(
                it.id,
                it.repoName,
                it.repoDescription,
                it.ownerName,
                it.ownerAvatar,
                it.forksCount,
                it.starsCount
            )
        }
    }
}