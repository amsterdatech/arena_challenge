package br.com.flyingdutchman.arena_challenge.data.remote.model

import br.com.flyingdutchman.arena_challenge.data.remote.model.Item
import com.google.gson.annotations.SerializedName

data class RepositoryResult(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("total_count")
    val totalCount: Int
)