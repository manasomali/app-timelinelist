package com.manasomali.timelinelist.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.*
import androidx.appcompat.app.AppCompatActivity
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.adapters.ViewPagerAdapter
import com.manasomali.timelinelist.fragments.PesquisaFilmesFragment
import com.manasomali.timelinelist.fragments.PesquisaSeriesFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_estatisticas.*
import kotlinx.android.synthetic.main.activity_lista.*
import kotlinx.android.synthetic.main.activity_pesquisa.*
import kotlinx.android.synthetic.main.fragment_filmes.view.*
import kotlinx.android.synthetic.main.fragment_pesquisafilmes.*
import kotlinx.android.synthetic.main.fragment_pesquisafilmes.view.*
import kotlinx.android.synthetic.main.fragment_pesquisaseries.*
import kotlinx.android.synthetic.main.fragment_pesquisaseries.view.*
import kotlinx.android.synthetic.main.obra_item.*

@Suppress("DEPRECATION")
class PesquisaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        var adapter = ViewPagerAdapter(supportFragmentManager)
        setupViewPager(adapter)
        setupColorIcones()

        tablayout_tabs_pesquisa.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.icon?.setTint(resources.getColor(R.color.colorRed))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        tab.icon?.setTint(resources.getColor(R.color.colorWhite))
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        tab.icon?.setTint(resources.getColor(R.color.colorBlack))
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        imageview_voltar_pesquisatolista.setOnClickListener{ startActivity(Intent(this, ListaActivity::class.java)) }

        edittext_busca.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoftKeyboard(this)
                val fragmentPesquisaFilmes = adapter.getItem(0) as PesquisaFilmesFragment
                val fragmentPesquisaSeries = adapter.getItem(1) as PesquisaSeriesFragment
                fragmentPesquisaFilmes.atualizaListaFilmes(edittext_busca.text.toString())
                fragmentPesquisaSeries.atualizaListaSeries(edittext_busca.text.toString())

                return@OnEditorActionListener true
            }
            false
        })

    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    }
    private fun setupViewPager(adapter: ViewPagerAdapter) {
        adapter.addFragment(PesquisaFilmesFragment(), "Filmes")
        adapter.addFragment(PesquisaSeriesFragment(), "Séries")
        viewpager_filmes_series_pesquisa.adapter = adapter
        tablayout_tabs_pesquisa.setupWithViewPager(viewpager_filmes_series_pesquisa)
        tablayout_tabs_pesquisa.getTabAt(0)?.setIcon(R.drawable.ic_movie)
        tablayout_tabs_pesquisa.getTabAt(1)?.setIcon(R.drawable.ic_serie)
    }
    private fun setupColorIcones() {
        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                tablayout_tabs_pesquisa.getTabAt(0)?.icon?.setTint(resources.getColor(R.color.colorRed))
                tablayout_tabs_pesquisa.getTabAt(1)?.icon?.setTint(resources.getColor(R.color.colorWhite))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                tablayout_tabs_pesquisa.getTabAt(0)?.icon?.setTint(resources.getColor(R.color.colorRed))
                tablayout_tabs_pesquisa.getTabAt(1)?.icon?.setTint(resources.getColor(R.color.colorBlack))
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,ListaActivity::class.java))
    }
}