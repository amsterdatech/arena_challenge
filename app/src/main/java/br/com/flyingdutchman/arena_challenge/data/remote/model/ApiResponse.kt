package br.com.flyingdutchman.arena_challenge.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("response")
    val response: List<Issue>
)