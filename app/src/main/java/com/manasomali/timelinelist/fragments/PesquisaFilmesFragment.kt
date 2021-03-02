package com.manasomali.timelinelist.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.manasomali.timelinelist.R
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.manasomali.timelinelist.activities.DetalheFilmeActivity
import com.manasomali.timelinelist.adapters.ListaFilmePesquisaAdapter
import com.manasomali.timelinelist.viewmodels.PesquisaViewModel
import kotlinx.android.synthetic.main.fragment_pesquisafilmes.*
import kotlinx.android.synthetic.main.fragment_pesquisafilmes.view.*

class PesquisaFilmesFragment : Fragment(), ListaFilmePesquisaAdapter.OnObraFilmeClickListener {
    private val viewModel: PesquisaViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_pesquisafilmes, container, false)

        viewModel.getPopularFilmes()

        view.recyclerview_filmes_pesquisa.layoutManager = LinearLayoutManager(context)
        view.recyclerview_filmes_pesquisa.setHasFixedSize(true)
        viewModel.listaFilmes.observe(viewLifecycleOwner) {
            println(it.toString())
            var adapter =  ListaFilmePesquisaAdapter(it, this)
            view.recyclerview_filmes_pesquisa.adapter = adapter
            view.progressbar_loading_filmes.visibility = INVISIBLE
            adapter.notifyDataSetChanged()
        }
        return view

    }
    override fun obraFilmeClick(position: Int) {
        var idClick = viewModel.listaFilmes.value?.results?.get(position)?.id as Int
        viewModel.getFilmesFromId(idClick)
        viewModel.listaFilmesDetalhe.observe(viewLifecycleOwner) {
            val intent = Intent(context, DetalheFilmeActivity::class.java)
            intent.putExtra("filmeClick", it)
            intent.putExtra("origem", "Pesquisa")
            startActivity(intent)
        }

    }
    internal fun atualizaListaFilmes(text:String) {
        recyclerview_filmes_pesquisa.removeAllViewsInLayout()
        progressbar_loading_filmes.visibility = VISIBLE
        viewModel.getFilmesFromApi(text)
    }
}