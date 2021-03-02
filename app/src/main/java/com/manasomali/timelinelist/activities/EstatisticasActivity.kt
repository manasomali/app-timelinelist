package com.manasomali.timelinelist.activities

import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.manasomali.timelinelist.R
import kotlinx.android.synthetic.main.activity_estatisticas.*

class EstatisticasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estatisticas)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        imageview_voltar_estatisticastoperfil.setOnClickListener{ startActivity(Intent(this, PerfilActivity::class.java)) }
    }
}