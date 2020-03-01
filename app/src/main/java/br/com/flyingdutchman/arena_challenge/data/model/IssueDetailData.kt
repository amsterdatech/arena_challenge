package br.com.flyingdutchman.arena_challenge.data.model

data class IssueDetailData(
    val id: Int,
    var title: String,
    var state: String,
    var updatedAt: String,
    var userAvatar: String,
    var userName: String,
    var number: Int,
    var description: String,
    var url: String
)