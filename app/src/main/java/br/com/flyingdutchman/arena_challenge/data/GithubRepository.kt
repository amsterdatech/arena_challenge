package br.com.flyingdutchman.arena_challenge.data

import br.com.flyingdutchman.arena_challenge.data.model.IssueData
import br.com.flyingdutchman.arena_challenge.data.model.IssueDetailData
import br.com.flyingdutchman.arena_challenge.data.model.RepoData
import br.com.flyingdutchman.arena_challenge.data.remote.GithubApi
import br.com.flyingdutchman.arena_challenge.data.remote.mappers.IssueDetailRemoteEntityMapper
import br.com.flyingdutchman.arena_challenge.data.remote.mappers.IssuesRemoteEntityMapper
import br.com.flyingdutchman.arena_challenge.data.remote.mappers.RepoRemoteEntityMapper
import br.com.flyingdutchman.arena_challenge.data.remote.model.Issue
import br.com.flyingdutchman.arena_challenge.data.remote.model.IssueDetail
import br.com.flyingdutchman.arena_challenge.extensions.parseIsoDateFormat
import io.reactivex.Scheduler
import io.reactivex.Single

class GithubRepository(
    private val api: GithubApi,
    private val issuesMapper: IssuesRemoteEntityMapper,
    private val repoMapper: RepoRemoteEntityMapper,
    private val ioScheduler: Scheduler
) {

    fun getIssues(repoOwner: String, repoName: String): Single<List<IssueData>> {
        return api
            .getIssues(repoOwner, repoName)

            .subscribeOn(ioScheduler)
            .map { apiResponse ->
                issuesMapper
                    .mapFromRemote(apiResponse)
                    .sortedByDescending { it.updatedAt.parseIsoDateFormat()?.time }
            }
    }

    fun searchRepos(page: Int = 1): Single<List<RepoData>> {
        return api.searchRepos(page = page)
            .subscribeOn(ioScheduler)
            .map { result ->
                repoMapper.mapFromRemote(result.items)
            }
    }
}