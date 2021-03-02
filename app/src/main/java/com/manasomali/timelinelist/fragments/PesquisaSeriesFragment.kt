package com.manasomali.timelinelist.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.activities.DetalheSerieActivity
import com.manasomali.timelinelist.adapters.ListaSeriePesquisaAdapter
import com.manasomali.timelinelist.viewmodels.PesquisaViewModel
import kotlinx.android.synthetic.main.fragment_pesquisaseries.*
import kotlinx.android.synthetic.main.fragment_pesquisaseries.view.*

class PesquisaSeriesFragment : Fragment(), ListaSeriePesquisaAdapter.OnObraSerieClickListener {
    private val viewModel: PesquisaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_pesquisaseries, container, false)
        viewModel.getPopularSeries()
        view.recyclerview_series_pesquisa.layoutManager = LinearLayoutManager(context)
        view.recyclerview_series_pesquisa.setHasFixedSize(true)
        viewModel.listaSeries.observe(viewLifecycleOwner) {
            var adapter =  ListaSeriePesquisaAdapter(it, this)
            view.recyclerview_series_pesquisa.adapter = adapter
            view.progressbar_loading_series.visibility = INVISIBLE
            adapter.notifyDataSetChanged()
        }
        return view
    }
    override fun obraSerieClick(position: Int) {
        var idClick = viewModel.listaSeries.value?.results?.get(position)?.id as Int
        viewModel.getSeriesFromId(idClick)
        viewModel.listaSeriesDetalhe.observe(viewLifecycleOwner) {
            val intent = Intent(context, DetalheSerieActivity::class.java)
            intent.putExtra("serieClick", it)
            intent.putExtra("origem", "Pesquisa")
            startActivity(intent)
        }

    }
    internal fun atualizaListaSeries(text:String) {
        recyclerview_series_pesquisa.removeAllViewsInLayout()
        progressbar_loading_series.visibility = VISIBLE
        viewModel.getSeriesFromApi(text)
    }
}