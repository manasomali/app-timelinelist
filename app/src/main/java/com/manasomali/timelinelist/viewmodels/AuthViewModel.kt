package com.manasomali.timelinelist.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.manasomali.timelinelist.helpers.Usuario


class AuthViewModel(): ViewModel() {
    var erromsg: MutableLiveData<String> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var stateRegister: MutableLiveData<Boolean> = MutableLiveData()
    var stateLogin: MutableLiveData<Boolean> = MutableLiveData()
    var stateGoogleLogin: MutableLiveData<Boolean> = MutableLiveData()
    var stateFaceLogin: MutableLiveData<Boolean> = MutableLiveData()
    var fotoUri: MutableLiveData<String> = MutableLiveData()
    var usuario: MutableLiveData<Usuario> = MutableLiveData()
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val storageReference = FirebaseStorage.getInstance().reference
    fun cadastroUsuarioFirebase(email: String, senha: String, nome: String, sobrenome: String) {
        loading.value = true
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        addUser(task.result.user?.uid.toString(), nome, sobrenome, email, "")
                    }
                    else -> {
                        Log.e("cadastroUsuarioFirebase", task.exception?.message.toString())
                        erromsg.value = task.exception?.message.toString()
                        stateRegister.value = false
                        loading.value = false
                    }
                }
            }
    }

    fun loginUsuarioFirebase(email: String, senha: String) {
        loading.value = true
        firebaseAuth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                when {
                    task.isSuccessful -> {
                        Log.d("loginUsuarioFirebase", "Usuário logado com sucesso.")
                        stateLogin.value = true
                    }
                    else -> {
                        Log.e("loginUsuarioFirebase", task.exception?.message.toString())
                        erromsg.value = task.exception?.message.toString()
                        stateLogin.value = false
                        loading.value = false
                    }
                }
            }
    }
    fun handleGoogleAccessToken(idToken: String) {
        loading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("handleGoogle", "Usuário logado com google com sucesso.")
                    val user = firebaseAuth.currentUser
                    var nomes = user?.displayName.toString().split(" ").map { it.trim() }
                    addUser(user?.uid.toString(),
                        nomes[0],
                        nomes[1],
                        user?.email.toString(),
                        user?.photoUrl.toString())
                } else {
                    Log.e("handleGoogle", task.exception?.message.toString())
                    erromsg.value = task.exception?.message.toString()
                    stateGoogleLogin.value = false
                    loading.value = false
                }
            }
    }
    fun handleFacebookAccessToken(token: AccessToken) {
        loading.value = true
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("handleFacebook", "Usuário logado com facebook com sucesso.")
                    val user = firebaseAuth.currentUser
                    var nomes = user?.displayName.toString().split(" ").map { it.trim() }
                    addUser(user?.uid.toString(),
                        nomes[0],
                        nomes[1],
                        user?.email.toString(),
                        user?.photoUrl.toString())
                } else {
                    Log.e("handleFacebook", task.exception?.message.toString())
                    erromsg.value = task.exception?.message.toString()
                    stateFaceLogin.value = false
                    loading.value = false
                }
            }
    }
    fun addUser(uid: String, nome: String, sobrenome: String, email: String, foto: String) {
        var docRef = db.collection("users").document(uid)
        docRef.get().addOnSuccessListener { result ->
                fotoUri.value = result.get("foto").toString()
                Log.d("addUser", "foto usuário recebido com sucesso: ${fotoUri.value}")
            }
            .addOnFailureListener { e ->
                Log.w("getUser", "Erro ao receber foto usuário.", e)
                erromsg.value = e.toString()
            }
            .addOnCompleteListener {
                var pic = foto
                if (fotoUri.value.toString().contains("firebasestorage")) {
                    pic = fotoUri.value.toString()
                    val user = hashMapOf<String, Any>(
                        "nome" to nome,
                        "sobrenome" to sobrenome,
                        "email" to email,
                        "foto" to pic,
                        "uid" to uid)
                    docRef.update(user)
                        .addOnSuccessListener {
                            Log.d("addUser", "Usuário adicionado com sucesso.")
                            stateLogin.value = true
                            loading.value = false
                        }
                        .addOnFailureListener { e ->
                            Log.w("addUser", "Erro ao adicionar usuário.", e)
                            stateLogin.value = false
                        }
                        .addOnCompleteListener {
                            loading.value = false
                        }
                } else {
                    val user = hashMapOf<String, Any>(
                        "nome" to nome,
                        "sobrenome" to sobrenome,
                        "email" to email,
                        "foto" to pic,
                        "uid" to uid)
                    docRef.set(user)
                        .addOnSuccessListener {
                            Log.d("addUser", "Usuário adicionado com sucesso.")
                            stateLogin.value = true
                            stateRegister.value = true
                            loading.value = false
                        }
                        .addOnFailureListener { e ->
                            Log.w("addUser", "Erro ao adicionar usuário.", e)
                            erromsg.value = e.toString()
                            stateLogin.value = false
                            stateRegister.value = false
                        }
                        .addOnCompleteListener {
                            loading.value = false
                        }
                }
            }
    }
    fun getUser(uid: String) {
        loading.value = true
        var docRef = db.collection("users").document(uid)
        docRef.get()
            .addOnSuccessListener { result ->
                usuario.value = Usuario(result.get("nome").toString(),
                    result.get("sobrenome").toString(),
                    result.get("email").toString(),
                    result.get("uid").toString(),
                    result.get("foto").toString())
                Log.d("getUser", "Usuário recebido com sucesso.")

            }
            .addOnFailureListener { e ->
                Log.w("getUser", "Erro ao receber usuário.", e)
                erromsg.value = e.toString()
                loading.value = false
            }
            .addOnCompleteListener {
                loading.value = false
            }
    }
    fun editDados(uid: String, nome: String, sobrenome: String) {
        val docRef = db.collection("users").document(uid)
        docRef.update("nome", nome)
            .addOnSuccessListener {
                Log.d("editDados", "Nome do usuário editado com sucesso.")
            }
            .addOnFailureListener { e ->
                Log.w("editDados", "Erro ao editar nome usuário.", e)
                erromsg.value = e.toString()
            }
        docRef.update("sobrenome", sobrenome)
            .addOnSuccessListener {
                Log.d("editDados", "Sobrenome do usuário editado com sucesso.")
            }
            .addOnFailureListener { e ->
                Log.w("editDados", "Erro ao editar sobrenome usuário.", e)
                erromsg.value = e.toString()
            }
        docRef.update("foto", fotoUri.value.toString())
                .addOnSuccessListener {
                    Log.d("editDados", "Foto do usuário editado com sucesso.")
                }
                .addOnFailureListener { e ->
                    Log.w("editDados", "Erro ao editar foto usuário.", e)
                    erromsg.value = e.toString()
                }
    }
    fun uploadFoto(filePath: Uri) {
        loading.value = true
        val ref = storageReference.child(usuario.value!!.uid + "/profilePicture/" + usuario.value!!.email)
        val uploadTask = ref.putFile(filePath)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fotoUri.value = task.result.toString()
            } else {
                Log.e("uploadFoto", task.exception?.message.toString())
                erromsg.value = task.exception?.message.toString()
            }
            loading.value = false
        }.addOnFailureListener { e ->
            Log.w("uploadFoto", "Erro ao carrregar imagem.", e)
            erromsg.value = e.toString()
            loading.value = false
        }
    }
}