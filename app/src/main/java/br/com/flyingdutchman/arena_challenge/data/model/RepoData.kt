package br.com.flyingdutchman.arena_challenge.data.model

data class RepoData(
    val id: Int,
    var repoName: String,
    var repoDescription: String,
    var ownerName: String,
    var ownerAvatar: String,
    var forksCount: Int,
    var starsCount: Int
)