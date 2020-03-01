package br.com.flyingdutchman.arena_challenge.ui.features.issues

data class Issue(
    val id: Int,
    var title: String,
    var description: String,
    var state: String,
    var updatedAt: String,
    var userAvatar: String,
    var userName: String,
    var number: Int
)

