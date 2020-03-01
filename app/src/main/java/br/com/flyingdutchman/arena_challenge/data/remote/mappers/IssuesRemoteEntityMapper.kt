package br.com.flyingdutchman.arena_challenge.data.remote.mappers

import br.com.flyingdutchman.arena_challenge.data.model.IssueData
import br.com.flyingdutchman.arena_challenge.data.remote.model.Issue

open class IssuesRemoteEntityMapper :
    RemoteEntityMapper<List<Issue>, List<IssueData>> {
    override fun mapFromRemote(type: List<Issue>): List<IssueData> {
        return type.map {
            IssueData(
                it.id,
                it.title,
                it.body,
                it.state,
                it.updatedAt,
                it.user.avatarUrl,
                it.user.login,
                it.number
            )
        }
    }
}