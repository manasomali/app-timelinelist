package com.manasomali.timelinelist.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.manasomali.timelinelist.Constants.KEY_THEME
import com.manasomali.timelinelist.Constants.PREFS_NAME
import com.manasomali.timelinelist.Constants.THEME_DARK
import com.manasomali.timelinelist.Constants.THEME_LIGHT
import com.manasomali.timelinelist.Constants.THEME_UNDEFINED
import com.manasomali.timelinelist.R
import kotlinx.coroutines.*
import com.manasomali.timelinelist.viewmodels.AuthViewModel


class SplashActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        val temaAtual = sharedPrefs.getInt(KEY_THEME, THEME_UNDEFINED)
        if(temaAtual==1) {
            setTema(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
        }
        if(temaAtual==0) {
            setTema(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)
        }
        if(temaAtual==-1) {
            setTema(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
        }

        var intent = Intent(this, LoginActivity::class.java)
        val user = Firebase.auth.currentUser
        if (user != null) {
            intent = Intent(this, ListaActivity::class.java)
        }
        scope.launch {
            delay(1000)
            startActivity(intent)
            finish()
        }
    }
    override fun onPause() {
        scope.cancel()
        super.onPause()
    }
    private fun setTema(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveTheme(prefsMode)
    }
    private fun saveTheme(theme: Int) = sharedPrefs.edit().putInt(KEY_THEME, theme).apply()
    override fun onBackPressed() {}

}