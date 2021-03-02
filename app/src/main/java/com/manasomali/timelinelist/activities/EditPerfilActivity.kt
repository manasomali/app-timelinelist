package com.manasomali.timelinelist.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.viewmodels.AuthViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_editperfil.*

class EditPerfilActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private val COD_IMG = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editperfil)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        if(!isOnline(this)) {
            button_editaperfil_save.visibility = GONE
            button_editaperfil_mudarfoto.visibility = GONE
            Toast.makeText(applicationContext, "Sem conexão com internet", Toast.LENGTH_LONG).show()
        }
        imageview_voltar_editpefiltoperfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        button_editaperfil_save.setOnClickListener {
            val nome = edittext_editaperfil_nome.text.toString()
            val sobrenome = edittext_editaperfil_sobrenome.text.toString()
            if (validaCampos(nome, sobrenome)) {
                viewModel.editDados(FirebaseAuth.getInstance().currentUser!!.uid, nome, sobrenome)
            }

            Toast.makeText(this, "Dados Atualizados.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso)
        imageview_exitfirebase.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener{
                FirebaseAuth.getInstance().signOut()
            }
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        viewModel.getUser(FirebaseAuth.getInstance().currentUser!!.uid)
        viewModel.usuario.observe(this) {
            edittext_editaperfil_nome.setText(it.nome)
            edittext_editaperfil_sobrenome.setText(it.sobrenome)
            Picasso.get().load(Uri.parse(it.foto)).placeholder(R.mipmap.ic_person).into(circularimageview_editperfil)
        }
        viewModel.fotoUri.observe(this) {
            Picasso.get().load(Uri.parse(it)).placeholder(R.mipmap.ic_person).into(circularimageview_editperfil)
        }
        button_editaperfil_mudarfoto.setOnClickListener {
            setImgFirebase()
        }
        initViewModel()
    }
    fun setImgFirebase() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecione a Imagem"), COD_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == COD_IMG && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            viewModel.uploadFoto(data.data!!)
        }
    }
    private fun initViewModel() {
        viewModel.loading.observe(this) {
            if(it) {
                startLoading()
            } else {
                endLoading()
            }
        }
        viewModel.erromsg.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }
    private fun startLoading() {
        button_editaperfil_mudarfoto.isClickable = false
        button_editaperfil_save.isClickable = false
        edittext_editaperfil_nome.isClickable = false
        edittext_editaperfil_sobrenome.isClickable = false
        progressbar_editaperfil.visibility = View.VISIBLE
        progressbar_editaperfil.background.alpha = 150
    }
    private fun endLoading() {
        button_editaperfil_mudarfoto.isClickable = true
        button_editaperfil_save.isClickable = true
        edittext_editaperfil_nome.isClickable = true
        edittext_editaperfil_sobrenome.isClickable = true
        progressbar_editaperfil.visibility = View.GONE
    }
    private fun validaCampos(nome: String, sobrenome: String): Boolean {
        return when {
            nome.isBlank() || sobrenome.isBlank() -> {
                false
            }
            else -> true
        }
    }
    private fun isOnline(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }
        return result
    }
}