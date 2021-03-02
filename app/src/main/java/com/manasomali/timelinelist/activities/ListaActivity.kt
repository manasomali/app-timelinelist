package com.manasomali.timelinelist.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.manasomali.timelinelist.*
import com.manasomali.timelinelist.adapters.ViewPagerAdapter
import com.manasomali.timelinelist.fragments.FilmesFragment
import com.manasomali.timelinelist.fragments.SeriesFragment
import com.manasomali.timelinelist.viewmodels.AuthViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_lista.*
import kotlinx.android.synthetic.main.activity_login.*


@Suppress("DEPRECATION")
class ListaActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        val firebaseAuth = FirebaseAuth.getInstance()
        toolbar_button_left.setOnClickListener {
            cardview_creditos.visibility = VISIBLE
            cardview_creditos.background.alpha = 150
        }
        cardview_creditos.setOnClickListener {
            cardview_creditos.visibility = GONE
        }
        imageview_icologo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                intent.data = Uri.parse("https://www.themoviedb.org")
                startActivity(intent)
            }
        })
        setupViewPager()
        setupColorIcones()

        if(!isOnline(this)) {
            floatingactionbutton.visibility = GONE
            Toast.makeText(applicationContext, "Sem conexão com internet", Toast.LENGTH_LONG).show()
        }
        tablayout_tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.icon?.setTint(resources.getColor(R.color.colorRed))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        tab?.icon?.setTint(resources.getColor(R.color.colorWhite))
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        tab?.icon?.setTint(resources.getColor(R.color.colorBlack))
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        toolbar_button_right.setOnClickListener { startActivity(
            Intent(
                this,
                PerfilActivity::class.java
            )
        ) }
        floatingactionbutton.setOnClickListener {
            startActivity(Intent(this, PesquisaActivity::class.java))
        }
        viewModel.getUser(firebaseAuth.currentUser!!.uid)
        viewModel.usuario.observe(this) {
            Picasso.get().load(Uri.parse(it.foto)).placeholder(R.mipmap.ic_person).into(
                toolbar_button_right)
        }

    }

    private fun setupViewPager() {
        var adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FilmesFragment(), "Filmes")
        adapter.addFragment(SeriesFragment(), "Séries")
        viewpager_filmes_series.adapter = adapter
        tablayout_tabs.setupWithViewPager(viewpager_filmes_series)
        tablayout_tabs.getTabAt(0)?.setIcon(R.drawable.ic_movie)
        tablayout_tabs.getTabAt(1)?.setIcon(R.drawable.ic_serie)
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
    private fun setupColorIcones() {
        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                tablayout_tabs.getTabAt(0)?.icon?.setTint(resources.getColor(R.color.colorRed))
                tablayout_tabs.getTabAt(1)?.icon?.setTint(resources.getColor(R.color.colorWhite))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                tablayout_tabs.getTabAt(0)?.icon?.setTint(resources.getColor(R.color.colorRed))
                tablayout_tabs.getTabAt(1)?.icon?.setTint(resources.getColor(R.color.colorBlack))
            }
        }
    }

    override fun onBackPressed() {}


}