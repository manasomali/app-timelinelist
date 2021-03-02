package com.manasomali.timelinelist.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manasomali.timelinelist.Constants.API_KEY
import com.manasomali.timelinelist.Constants.LANG
import com.manasomali.timelinelist.helpers.*
import com.manasomali.timelinelist.repository
import kotlinx.coroutines.launch

class PesquisaViewModel(application: Application): AndroidViewModel(application) {

    var listaFilmes = MutableLiveData<BaseFilmeBusca>()
    var listaSeries = MutableLiveData<BaseSerieBusca>()
    var listaFilmesDetalhe = MutableLiveData<BaseFilmeDetalhe>()
    var listaSeriesDetalhe = MutableLiveData<BaseSerieDetalhe>()


    fun getSeriesFromApi(query: String) {
        viewModelScope.launch {
            listaSeries.setValue(repository.getSeries(API_KEY,LANG,query,false))
        }
    }
    fun getFilmesFromApi(query: String) {
        viewModelScope.launch {
            listaFilmes.setValue(repository.getFilmes(API_KEY,LANG,query,false))
        }
    }
    fun getPopularFilmes() {
        viewModelScope.launch {
            listaFilmes.setValue(repository.getPopularFilmes(API_KEY,LANG))
        }
    }
    fun getPopularSeries() {
        viewModelScope.launch {
            listaSeries.setValue(repository.getPopularSeries(API_KEY,LANG))
        }
    }
    fun getFilmesFromId(id: Int) {
        viewModelScope.launch {
            listaFilmesDetalhe.setValue(repository.getFilmeById(id,API_KEY,LANG))
        }
    }
    fun getSeriesFromId(id: Int) {
        viewModelScope.launch {
            listaSeriesDetalhe.setValue(repository.getSerieById(id,API_KEY,LANG))
        }
    }
}