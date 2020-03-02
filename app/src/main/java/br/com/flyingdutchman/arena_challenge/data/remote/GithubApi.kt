package br.com.flyingdutchman.arena_challenge.data.remote

import br.com.flyingdutchman.arena_challenge.data.remote.model.Issue
import br.com.flyingdutchman.arena_challenge.data.remote.model.IssueDetail
import br.com.flyingdutchman.arena_challenge.data.remote.model.RepositoryResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("repos/{repoOwner}/{repoName}/issues")
    fun getIssues(
        @Path("repoOwner") repoOwner: String,
        @Path("repoName") repoName: String
    ): Single<List<Issue>>

    @GET("repos/{repoOwner}/{repoName}/issues/{issueNumber}")
    fun getIssueDetail(
        @Path("repoOwner") repoOwner: String,
        @Path("repoName") repoName: String,
        @Path("issueNumber") issueNumber: String
    ): Single<IssueDetail>

    @GET("search/repositories")
    fun searchRepos(
        @Query("q") q: String = "language:Java",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("sort") sort: String = "stars"
    ): Single<RepositoryResult>
}