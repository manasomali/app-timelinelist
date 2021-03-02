package com.manasomali.timelinelist.activities

import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.util.Patterns
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.activity_cadastro.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    companion object {
        const val RC_SIGN_IN = 120
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        button_login.setOnClickListener {
            val email = edittext_login_email.text.toString()
            val senha = edittext_login_senha.text.toString()
            if(validaCampos(email, senha)) {
                viewModel.loginUsuarioFirebase(email, senha)
            } else {
                Toast.makeText(this, "Informe os campos corretamente.", Toast.LENGTH_LONG).show()
            }
        }

        button_cadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
        siginbutton_google.setOnClickListener {
            signinFirebaseGoogle()
            startLoading()
        }
        siginbutton_facebook.setOnClickListener {
            signinFirebaseFacebook()
            startLoading()
        }
        initViewModel()
    }

    private fun signinFirebaseGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun signinFirebaseFacebook() {
        callbackManager = CallbackManager.Factory.create()
        siginbutton_facebook.setPermissions(listOf("email", "public_profile"))
        siginbutton_facebook.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                viewModel.handleFacebookAccessToken(loginResult.accessToken)
            }
            override fun onCancel() {
                println("facebook:onCancel")
                Toast.makeText(this@LoginActivity, "Login Facebook cancelado.", Toast.LENGTH_SHORT).show()
                endLoading()
            }
            override fun onError(error: FacebookException) {
                println("facebook:onError $error")
                Toast.makeText(this@LoginActivity, "Login Facebook falhou: $error", Toast.LENGTH_SHORT).show()
                endLoading()
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != RC_SIGN_IN) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                println("firebaseAuthWithGoogle:" + account.id)
                viewModel.handleGoogleAccessToken(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Login falhou.", Toast.LENGTH_SHORT).show()
                endLoading()
            }
        }
    }
    private fun initViewModel() {
        viewModel.stateLogin.observe(this) {
            if(it) {
                startActivity(Intent(this, ListaActivity::class.java))
            }
        }
        viewModel.stateFaceLogin.observe(this) {
            if(!it) {
                Toast.makeText(this, "Login com Facebook falhou.", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.stateGoogleLogin.observe(this) {
            if(!it) {
                Toast.makeText(this, "Login com Google falhou.", Toast.LENGTH_SHORT).show()
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
    private fun startLoading() {
        Toast.makeText(this, "Aguarde...", Toast.LENGTH_SHORT).show()
        button_login.isClickable = false
        button_cadastro.isClickable = false
        siginbutton_google.isClickable = false
        siginbutton_facebook.isClickable = false
        edittext_login_email.isEnabled = false
        edittext_login_senha.isEnabled = false
        progressbar_loading_login.visibility = VISIBLE
        progressbar_loading_login.background.alpha = 150
    }
    private fun endLoading() {
        siginbutton_google.isClickable = true
        button_cadastro.isClickable = true
        siginbutton_google.isClickable = true
        siginbutton_facebook.isClickable = true
        edittext_login_email.isEnabled = true
        edittext_login_senha.isEnabled = true
        progressbar_loading_login.visibility = GONE
    }
    private fun validaCampos(email: String, password: String): Boolean {
        return when {
            email.isBlank() || password.isBlank() -> {
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                false
            }
            password.length < 6 -> {
                false
            }
            else -> true
        }
    }
    override fun onBackPressed() {}
}