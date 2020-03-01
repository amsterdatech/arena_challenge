package br.com.flyingdutchman.arena_challenge.data.model

data class IssueData(
    val id: Int,
    var title: String,
    var state: String,
    var updatedAt:String,
    var userAvatar:String,
    var number:Int
)