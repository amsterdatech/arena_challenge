package br.com.flyingdutchman.arena_challenge.ui.features.repositories

import android.os.Parcel
import android.os.Parcelable

data class Repository(
    val id: Int,
    var repoName: String,
    var repoDescription: String,
    var ownerName: String,
    var ownerAvatar: String,
    var forksCount: Int,
    var starsCount: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(repoName)
        parcel.writeString(repoDescription)
        parcel.writeString(ownerName)
        parcel.writeString(ownerAvatar)
        parcel.writeInt(forksCount)
        parcel.writeInt(starsCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Repository> {
        override fun createFromParcel(parcel: Parcel): Repository {
            return Repository(
                parcel
            )
        }

        override fun newArray(size: Int): Array<Repository?> {
            return arrayOfNulls(size)
        }
    }

}