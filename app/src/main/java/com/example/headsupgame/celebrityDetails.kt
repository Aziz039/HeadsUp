package com.example.headsupgame

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//data class CelebrityDetails( val name: String, val taboo1: String, val taboo2: String, val taboo3: String, val pk: Int)

data class CelebrityDetails(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("taboo1")
    val taboo1: String? = null,

    @SerializedName("taboo2")
    val taboo2: String? = null,

    @SerializedName("taboo3")
    val taboo3: String? = null,

    @SerializedName("pk")
    val pk: String? = null
) : Serializable {

}