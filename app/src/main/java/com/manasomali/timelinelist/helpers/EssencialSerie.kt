package com.manasomali.timelinelist.helpers

import java.io.Serializable

data class EssencialSerie(
    var id: String,
    val serieid: String,
    val name: String,
    val backdropPath: String,
    var dataAssistidoPessoal: String,
    var statusPessoal: String,
    var notaPessoal: String
): Serializable