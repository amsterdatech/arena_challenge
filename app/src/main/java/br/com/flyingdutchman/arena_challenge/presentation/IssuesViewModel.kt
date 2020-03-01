package br.com.flyingdutchman.arena_challenge.presentation

import androidx.lifecycle.*
import br.com.flyingdutchman.arena_challenge.data.GithubRepository
import br.com.flyingdutchman.arena_challenge.presentation.livedata.SingleLiveEvent
import br.com.flyingdutchman.arena_challenge.ui.features.issues.Issue
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class IssuesViewModel(
    private var repository: GithubRepository,
    private var compositeDisposable: CompositeDisposable,
    private var mainScheduler: Scheduler
) : ViewModel(), LifecycleObserver {

    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<SingleLiveEvent<Throwable>>()
    val success = MutableLiveData<List<Issue>>()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun loadIssues(repoOwner: String, repoName: String) {
        repository
            .getIssues(
                repoOwner, repoName
            )
            .observeOn(mainScheduler)
            .doOnSubscribe {
                loading.value = true
            }
            .subscribe(
                { issues ->
                    loading.value = false
                    success.value = issues.map {
                        Issue(
                            it.id,
                            it.title,
                            it.description,
                            it.state,
                            it.updatedAt,
                            it.userAvatar,
                            it.userLogin,
                            it.number
                        )
                    }

                },
                {
                    loading.value = false
                    error.value =
                        SingleLiveEvent(it)
                }
            )
            .apply {
                compositeDisposable.add(this)
            }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}