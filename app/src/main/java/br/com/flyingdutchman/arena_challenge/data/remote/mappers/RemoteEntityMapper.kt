package br.com.flyingdutchman.arena_challenge.data.remote.mappers

interface RemoteEntityMapper< M,  E> {
    fun mapFromRemote(type: M): E
}
