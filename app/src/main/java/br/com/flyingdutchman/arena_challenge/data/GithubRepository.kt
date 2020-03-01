package br.com.flyingdutchman.arena_challenge.data

import br.com.flyingdutchman.arena_challenge.data.model.IssueData
import br.com.flyingdutchman.arena_challenge.data.model.IssueDetailData
import br.com.flyingdutchman.arena_challenge.data.model.RepoData
import br.com.flyingdutchman.arena_challenge.data.remote.GithubApi
import br.com.flyingdutchman.arena_challenge.data.remote.mappers.IssueDetailRemoteEntityMapper
import br.com.flyingdutchman.arena_challenge.data.remote.mappers.IssuesRemoteEntityMapper
import br.com.flyingdutchman.arena_challenge.data.remote.mappers.RepoRemoteEntityMapper
import br.com.flyingdutchman.arena_challenge.extensions.parseIsoDateFormat
import io.reactivex.Scheduler
import io.reactivex.Single

class GithubRepository(
    private val api: GithubApi,
    private val issuesMapper: IssuesRemoteEntityMapper,
    private val issuesDetailMapper: IssueDetailRemoteEntityMapper,
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

    fun getIssueDetail(
        repoOwner: String,
        repoName: String,
        issueNumber: Int
    ): Single<IssueDetailData> {
        return api.getIssueDetail(repoOwner, repoName, issueNumber.toString())
            .subscribeOn(ioScheduler)
            .map {
                issuesDetailMapper.mapFromRemote(it)
            }

    }

    fun searchRepos(): Single<List<RepoData>> {
        return api.searchRepos()
            .subscribeOn(ioScheduler)
            .map { result ->
                repoMapper.mapFromRemote(result.items)
            }
    }
}