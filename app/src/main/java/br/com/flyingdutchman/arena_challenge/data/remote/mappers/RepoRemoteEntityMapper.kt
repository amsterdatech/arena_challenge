package br.com.flyingdutchman.arena_challenge.data.remote.mappers

import br.com.flyingdutchman.arena_challenge.data.model.RepoData
import br.com.flyingdutchman.arena_challenge.data.remote.model.Item

class RepoRemoteEntityMapper :
    RemoteEntityMapper<List<Item>, List<RepoData>> {
    override fun mapFromRemote(type: List<Item>): List<RepoData> {
        return type.map {
            RepoData(
                it.id,
                it.name,
                it.description,
                it.owner.login,
                it.owner.avatarUrl,
                it.forksCount,
                it.stargazersCount
            )
        }
    }
}
