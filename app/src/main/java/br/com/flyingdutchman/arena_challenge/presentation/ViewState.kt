package br.com.flyingdutchman.arena_challenge.presentation

class ViewState<D>(val status: Status, val data: D? = null, val error: Throwable? = null) {
    enum class Status {
        LOADING, SUCCESS, ERROR
    }
}