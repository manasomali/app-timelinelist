package com.manasomali.timelinelist.helpers


import android.annotation.SuppressLint
import androidx.room.Entity
import com.manasomali.timelinelist.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat

@Entity(tableName="filmes")
data class BaseFilmeDetalhe(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("belongs_to_collection")
    val belongsToCollection: Any,
    @SerializedName("budget")
    val budget: Int,
    @SerializedName("genres")
    val genres: ArrayList<Genre>,
    @SerializedName("homepage")
    val homepage: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: Any,
    @SerializedName("production_companies")
    val productionCompanies: ArrayList<ProductionCompany>,
    @SerializedName("production_countries")
    val productionCountries: ArrayList<ProductionCountry>,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("revenue")
    val revenue: Int,
    @SerializedName("runtime")
    val runtime: Int,
    @SerializedName("spoken_languages")
    val spokenLanguages: ArrayList<SpokenLanguage>,
    @SerializedName("status")
    val status: String,
    @SerializedName("tagline")
    val tagline: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("video")
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
): Serializable {
    data class Genre(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String
    ): Serializable

    data class ProductionCompany(
        @SerializedName("id")
        val id: Int,
        @SerializedName("logo_path")
        val logoPath: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("origin_country")
        val originCountry: String
    ): Serializable

    data class ProductionCountry(
        @SerializedName("iso_3166_1")
        val iso31661: String,
        @SerializedName("name")
        val name: String
    ): Serializable

    data class SpokenLanguage(
        @SerializedName("iso_639_1")
        val iso6391: String,
        @SerializedName("name")
        val name: String
    ): Serializable

    fun getId(): String {
        return id.toString()
    }
    fun getTitulo(): String {
        return title
    }
    fun getTempo(): String {
        return if (runtime.toString()==""||runtime.toString()=="0") {
            ""
        } else {
            "$runtime min"
        }
    }
    fun getEstado(): String {
        for (stat in Constants.STATUS_FILME) {
            if(status==stat.key)
                return stat.value
        }
        return ""
    }
    @SuppressLint("SimpleDateFormat")
    fun getDataDeLancamento(): String {
        val formatter = SimpleDateFormat("yyyy")
        val dateFormat = SimpleDateFormat("yyyy-mm-dd")
        return if (releaseDate!="") {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            formatter.format(dateFormat.parse(releaseDate))
        } else {
            ""
        }
    }
    fun getMediaVotos(): String {
        return "${voteAverage}/10"
    }
    fun getSinopse(): String {
        return overview
    }
    fun getWallpaper(): String {
        return backdropPath
    }
    fun getPoster(): String {
        return posterPath.toString()
    }
}