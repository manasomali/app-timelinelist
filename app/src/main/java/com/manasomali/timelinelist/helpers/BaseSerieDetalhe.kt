package com.manasomali.timelinelist.helpers

import android.annotation.SuppressLint
import androidx.room.Entity
import com.manasomali.timelinelist.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat

@Entity(tableName="series")
data class BaseSerieDetalhe(
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("created_by")
    val createdBy: ArrayList<CreatedBy>,
    @SerializedName("episode_run_time")
    val episodeRunTime: ArrayList<Int>,
    @SerializedName("first_air_date")
    val firstAirDate: String,
    @SerializedName("genres")
    val genres: ArrayList<Genre>,
    @SerializedName("homepage")
    val homepage: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("in_production")
    val inProduction: Boolean,
    @SerializedName("languages")
    val languages: ArrayList<String>,
    @SerializedName("last_air_date")
    val lastAirDate: String,
    @SerializedName("last_episode_to_air")
    val lastEpisodeToAir: LastEpisodeToAir,
    @SerializedName("name")
    val name: String,
    @SerializedName("networks")
    val networks: ArrayList<Network>,
    @SerializedName("next_episode_to_air")
    val nextEpisodeToAir: Any,
    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int,
    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int,
    @SerializedName("origin_country")
    val originCountry: ArrayList<String>,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_name")
    val originalName: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("production_companies")
    val productionCompanies: ArrayList<ProductionCompany>,
    @SerializedName("production_countries")
    val productionCountries: ArrayList<ProductionCountry>,
    @SerializedName("seasons")
    val seasons: ArrayList<Season>,
    @SerializedName("spoken_languages")
    val spokenLanguages: ArrayList<SpokenLanguage>,
    @SerializedName("status")
    val status: String,
    @SerializedName("tagline")
    val tagline: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
): Serializable {
    data class CreatedBy(
        @SerializedName("credit_id")
        val creditId: String,
        @SerializedName("gender")
        val gender: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("profile_path")
        val profilePath: String
    ): Serializable

    data class Genre(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String
    ): Serializable

    data class LastEpisodeToAir(
        @SerializedName("air_date")
        val airDate: String,
        @SerializedName("episode_number")
        val episodeNumber: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("overview")
        val overview: String,
        @SerializedName("production_code")
        val productionCode: String,
        @SerializedName("season_number")
        val seasonNumber: Int,
        @SerializedName("still_path")
        val stillPath: String,
        @SerializedName("vote_average")
        val voteAverage: Double,
        @SerializedName("vote_count")
        val voteCount: Int
    ): Serializable

    data class Network(
        @SerializedName("id")
        val id: Int,
        @SerializedName("logo_path")
        val logoPath: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("origin_country")
        val originCountry: String
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

    data class Season(
        @SerializedName("air_date")
        val airDate: String,
        @SerializedName("episode_count")
        val episodeCount: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("overview")
        val overview: String,
        @SerializedName("poster_path")
        val posterPath: String,
        @SerializedName("season_number")
        val seasonNumber: Int
    ): Serializable

    data class SpokenLanguage(
        @SerializedName("english_name")
        val englishName: String,
        @SerializedName("iso_639_1")
        val iso6391: String,
        @SerializedName("name")
        val name: String
    ): Serializable
    fun getId(): String {
        return id.toString()
    }
    fun getTitulo(): String {
        return name
    }
    fun getTemporadas(): String {
        return if (numberOfSeasons==1) {
            "$numberOfSeasons Temp"
        } else if (numberOfSeasons.toString()=="") {
            ""
        } else {
            "$numberOfSeasons Temps"
        }
    }
    fun getEstado(): String {
        for (stat in Constants.STATUS_SERIE) {
            if(status==stat.key)
                return stat.value
        }
        return ""
    }
    @SuppressLint("SimpleDateFormat")
    fun getDataDeLancamento(): String {
        val formatter = SimpleDateFormat("yyyy")
        val dateFormat = SimpleDateFormat("yyyy-mm-dd")
        return if (firstAirDate!="") {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            formatter.format(dateFormat.parse(firstAirDate))
        } else {
            ""
        }
    }
    fun getMediaVotos(): String {
        return "$voteAverage/10"
    }
    fun getSinopse(): String {
        return overview
    }
    fun getWallpaper(): String {
        return backdropPath
    }
    fun getPoster(): String {
        return posterPath
    }
}