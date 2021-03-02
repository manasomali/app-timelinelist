package com.manasomali.timelinelist.helpers

import java.io.Serializable

data class BaseGenres (
    val genres : ArrayList<Genres>
): Serializable {
    data class Genres (
        val id : Int,
        val name : String
    )
}
