package com.manasomali.timelinelist.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuth
import com.manasomali.timelinelist.Constants.KEY_THEME
import com.manasomali.timelinelist.Constants.PREFS_NAME
import com.manasomali.timelinelist.Constants.THEME_DARK
import com.manasomali.timelinelist.Constants.THEME_LIGHT
import com.manasomali.timelinelist.Constants.THEME_UNDEFINED
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.viewmodels.AuthViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_lista.*
import kotlinx.android.synthetic.main.activity_perfil.*


class PerfilActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        button_estatisticas.visibility = GONE

        initTheme()
        initThemeListener()

        imageview_voltar_pefiltolista.setOnClickListener { startActivity(Intent(this,
            ListaActivity::class.java)) }
        imageview_editperfil.setOnClickListener { startActivity(Intent(this,
                    EditPerfilActivity::class.java)) }
        button_estatisticas.setOnClickListener { startActivity(Intent(this,
            EstatisticasActivity::class.java)) }

        viewModel.getUser(FirebaseAuth.getInstance().currentUser!!.uid)
        viewModel.usuario.observe(this) {
            textview_perfil_nome.text = it.nome
            textview_perfil_sobrenome.text = it.sobrenome
            textview_perfil_email.text = it.email
            Picasso.get().load(Uri.parse(it.foto)).placeholder(R.mipmap.ic_person).into(circularimageview_perfil)
        }
        viewModel.loading.observe(this) {
            if(it) {
                startLoading()
            } else {
                endLoading()
            }
        }
    }

    private fun initThemeListener(){
        themeDark.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        themeLight.isChecked = false
                        themeDark.isChecked = true
                        Toast.makeText(applicationContext,"Tema alterado: Escuro", Toast.LENGTH_SHORT).show()
                        setTheme(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
        themeLight.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        themeDark.isChecked = false
                        themeLight.isChecked = true
                        Toast.makeText(applicationContext,"Tema alterado: Claro", Toast.LENGTH_SHORT).show()
                        setTheme(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveTheme(prefsMode)
    }

    private fun initTheme() {
        when (sharedPrefs.getInt(KEY_THEME, THEME_UNDEFINED)) {
            0 -> {
                themeDark.isChecked = true
                themeLight.isChecked = false
            }
            1 -> {
                themeLight.isChecked = true
                themeDark.isChecked = false
            }
            -1 -> {
                themeLight.isChecked = true
                themeDark.isChecked = false
            }
        }
    }

    private fun saveTheme(theme: Int) = sharedPrefs.edit().putInt(KEY_THEME, theme).apply()
    private fun startLoading() {
        progressbar_loading_perfil.visibility = View.VISIBLE
        progressbar_loading_perfil.background.alpha = 150
    }
    private fun endLoading() {
        progressbar_loading_perfil.visibility = GONE
    }
}