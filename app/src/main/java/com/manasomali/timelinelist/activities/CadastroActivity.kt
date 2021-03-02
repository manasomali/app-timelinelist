package com.manasomali.timelinelist.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.manasomali.timelinelist.Constants
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.activity_cadastro.*
import kotlinx.android.synthetic.main.activity_login.*


class CadastroActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        firebaseAuth = FirebaseAuth.getInstance()

        imageview_voltar_cadastrotologin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        button_cadastro_cadastrese.setOnClickListener {
            val nome = edittext_cadastro_nome.text.toString()
            val sobrenome = edittext_cadastro_sobrenome.text.toString()
            val email = edittext_cadastro_email.text.toString()
            val senha1 = edittext_cadastro_senha1.text.toString()
            val senha2 = edittext_cadastro_senha2.text.toString()
            if(validaCampos(nome, sobrenome, email, senha1, senha2)) {
                viewModel.cadastroUsuarioFirebase(email,senha1,nome,sobrenome)
            } else {
                Toast.makeText(this, "Informe os campos corretamente.", Toast.LENGTH_LONG).show()
            }
        }
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.stateRegister.observe(this) {
            if(it) {
                startActivity(Intent(this, ListaActivity::class.java))
            }
        }
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
    private fun validaCampos(nome: String, sobrenome:String, email: String, password1: String, password2: String): Boolean {
        return when {
            nome.isBlank() || sobrenome.isBlank() || email.isBlank() || password1.isBlank() -> {
                false
            }
            password1!=password2 -> {
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                false
            }
            password1.length < 6 -> {
                false
            }
            else -> true
        }
    }
    private fun startLoading() {
        Toast.makeText(this, "Aguarde...", Toast.LENGTH_SHORT).show()
        button_cadastro_cadastrese.isClickable = false
        edittext_cadastro_nome.isFocusable = false
        edittext_cadastro_email.isFocusable = false
        edittext_cadastro_senha1.isFocusable = false
        edittext_cadastro_senha2.isFocusable = false
        progressbar_loading_cadastro.visibility = View.VISIBLE
        progressbar_loading_cadastro.background.alpha = 150
    }
    private fun endLoading() {
        button_cadastro_cadastrese.isClickable = true
        edittext_cadastro_nome.isEnabled = true
        edittext_cadastro_email.isEnabled = true
        edittext_cadastro_senha1.isEnabled = true
        edittext_cadastro_senha2.isEnabled = true
        progressbar_loading_cadastro.visibility = View.GONE
    }
}