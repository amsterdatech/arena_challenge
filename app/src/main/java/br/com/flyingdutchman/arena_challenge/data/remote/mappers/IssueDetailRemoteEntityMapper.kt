package br.com.flyingdutchman.arena_challenge.data.remote.mappers

import br.com.flyingdutchman.arena_challenge.data.model.IssueDetailData
import br.com.flyingdutchman.arena_challenge.data.remote.mappers.RemoteEntityMapper
import br.com.flyingdutchman.arena_challenge.data.remote.model.IssueDetail

class IssueDetailRemoteEntityMapper :
    RemoteEntityMapper<IssueDetail, IssueDetailData> {
    override fun mapFromRemote(type: IssueDetail): IssueDetailData {
        return IssueDetailData(
            type.id,
            type.title,
            type.state,
            type.updatedAt,
            type.user.avatarUrl,
            type.user.login,
            type.number,
            type.body,
            type.htmlUrl
        )
    }
}