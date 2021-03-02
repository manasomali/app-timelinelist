package com.manasomali.timelinelist.helpers

import java.io.Serializable

data class EssencialFilme(
    var id: String,
    val filmeid: String,
    val title: String,
    val backdropPath: String,
    var dataAssistidoPessoal: String,
    var cinema: Boolean,
    var dormiu: Boolean,
    var chorou: Boolean,
    var favorito: Boolean,
    var dislike: Boolean,
    var notaPessoal: String
): Serializable