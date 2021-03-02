package com.manasomali.timelinelist.viewmodels

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.manasomali.timelinelist.Constants
import com.manasomali.timelinelist.helpers.*
import com.manasomali.timelinelist.repository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter


class FirestoreViewModel(application: Application): AndroidViewModel(application) {

    var listaFilme = MutableLiveData<ArrayList<EssencialFilme>>()
    var filmeDetalhe = MutableLiveData<BaseFilmeDetalhe>()
    var listaSerie = MutableLiveData<ArrayList<EssencialSerie>>()
    var serieDetalhe = MutableLiveData<BaseSerieDetalhe>()

    var erromsg: MutableLiveData<String> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var stateGetFilmes: MutableLiveData<Boolean> = MutableLiveData()
    var stateGetSeries: MutableLiveData<Boolean> = MutableLiveData()

    val db = FirebaseFirestore.getInstance()

    fun getFilmesFromId(id: String) {
        viewModelScope.launch {
            filmeDetalhe.setValue(repository.getFilmeById(id.toInt(),
                Constants.API_KEY,
                Constants.LANG))
        }
    }
    fun getSeriesFromId(id: String) {
        viewModelScope.launch {
            serieDetalhe.setValue(repository.getSerieById(id.toInt(),
                Constants.API_KEY,
                Constants.LANG))
        }
    }

    fun getAllFilmes(uid: String) {
        loading.value = true
        var listFilmes = ArrayList<EssencialFilme>()
        db.collection("users").document(uid).collection("filmes").orderBy("id")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var filme = EssencialFilme(
                        document.get("id").toString(),
                        document.get("filmeid").toString(),
                        document.get("title").toString(),
                        document.get("backdropPath").toString(),
                        document.get("dataAssistidoPessoal").toString(),
                        document.get("cinema") as Boolean,
                        document.get("dormiu") as Boolean,
                        document.get("chorou") as Boolean,
                        document.get("favorito") as Boolean,
                        document.get("dislike") as Boolean,
                        document.get("notaPessoal").toString()
                    )
                    listFilmes.add(filme)
                }
                stateGetFilmes.value = true
            }
            .addOnFailureListener { exception ->
                stateGetFilmes.value = false
                erromsg.value = exception.toString()
                Log.d("TAG", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                loading.value = false
                listaFilme.value = listFilmes
            }
    }
    fun getAllSeries(uid: String) {
        loading.value = true
        var listSeries = ArrayList<EssencialSerie>()
        db.collection("users").document(uid).collection("series").orderBy("id")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var serie = EssencialSerie(
                        document.get("id").toString(),
                        document.get("serieid").toString(),
                        document.get("name").toString(),
                        document.get("backdropPath").toString(),
                        document.get("dataAssistidoPessoal").toString(),
                        document.get("statusPessoal").toString(),
                        document.get("notaPessoal").toString()
                    )
                    listSeries.add(serie)
                }
                stateGetSeries.value = true
            }
            .addOnFailureListener { exception ->
                stateGetSeries.value = false
                erromsg.value = exception.toString()
                Log.d("TAG", "Error getting documents: ", exception)
            }
            .addOnCompleteListener {
                loading.value = false
                listaSerie.value = listSeries
            }
    }
    fun addFilme(uid: String, filme: EssencialFilme) {
        loading.value = true
        var docRef = db.collection("users").document(uid).collection("filmes").document(filme.id)
        docRef.set(filme)
            .addOnSuccessListener {
                Log.d("addFilme", "Filme adicionado com sucesso.")
                loading.value = false
            }
            .addOnFailureListener { e ->
                Log.w("addFilme", "Erro ao adicionar filme.", e)
            }
            .addOnCompleteListener {
                loading.value = false
            }
    }
    fun addSerie(uid: String, serie: EssencialSerie) {
        loading.value = true
        var docRef = db.collection("users").document(uid).collection("series").document(serie.id)
        docRef.set(serie)
            .addOnSuccessListener {
                Log.d("addSerie", "Serie adicionado com sucesso.")
                loading.value = false
            }
            .addOnFailureListener { e ->
                Log.w("addSerie", "Erro ao adicionar serie.", e)
            }
            .addOnCompleteListener {
                loading.value = false
            }
    }
    fun editaFilme(uid: String, filme: EssencialFilme) {
        loading.value = true
        var docRef = db.collection("users").document(uid).collection("filmes").document(filme.id)
        docRef.set(filme)
            .addOnSuccessListener {
                Log.d("editaFilme", "DocumentSnapshot successfully edited!")
            }
            .addOnFailureListener { e ->
                Log.w("editaFilme", "Error writing document", e)
            }
            .addOnCompleteListener {
                loading.value = false
            }
    }
    fun editaSerie(uid: String, serie: EssencialSerie) {
        loading.value = true
        var docRef = db.collection("users").document(uid).collection("series").document(serie.id)
        docRef.set(serie)
            .addOnSuccessListener {
                Log.d("editaSerie", "DocumentSnapshot successfully edited!")
            }
            .addOnFailureListener { e ->
                Log.w("editaSerie", "Error writing document", e)
            }
            .addOnCompleteListener {
                loading.value = false
            }
    }
    fun delFilme(uid: String, id: String) {
        var docRef = db.collection("users").document(uid).collection("filmes").document(id)
        docRef.delete()
            .addOnSuccessListener {
                Log.d("delFilme", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("delFilme", "Error deleting document", e)
            }
            .addOnCompleteListener {
                loading.value = false
            }
    }
    fun delSerie(uid: String, id: String) {
        var docRef = db.collection("users").document(uid).collection("series").document(id)
        docRef.delete()
            .addOnSuccessListener {
                Log.d("delFilme", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("delFilme", "Error deleting document", e)
            }
            .addOnCompleteListener {
                loading.value = false
            }
    }
}