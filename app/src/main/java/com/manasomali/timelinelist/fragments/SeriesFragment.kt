package com.manasomali.timelinelist.fragments


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.manasomali.timelinelist.*
import com.manasomali.timelinelist.activities.DetalheSerieActivity
import com.manasomali.timelinelist.adapters.ListaSeriesAdapter
import com.manasomali.timelinelist.helpers.EssencialSerie
import com.manasomali.timelinelist.viewmodels.FirestoreViewModel
import kotlinx.android.synthetic.main.fragment_filmes.*
import kotlinx.android.synthetic.main.fragment_series.*
import kotlinx.android.synthetic.main.fragment_series.view.*

class SeriesFragment : Fragment(), ListaSeriesAdapter.OnSerieClickListener {
    private val viewModel: FirestoreViewModel by viewModels()
    var uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_series, container, false)

        viewModel.getAllSeries(uid)

        viewModel.listaSerie.observe(viewLifecycleOwner) {
            var adapter =  ListaSeriesAdapter(it, this)
            view.recyclerview_series.adapter = adapter
            view.recyclerview_series.layoutManager = LinearLayoutManager(context)
            view.recyclerview_series.setHasFixedSize(true)
            view.searchview_pesquisa_series.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }
            })
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            if(it) {
                startLoading()
            } else {
                endLoading()
            }
        }

        return view
    }
    override fun serieClick(position: Int) {
        if(!context?.let { isOnline(it) }!!) {
            Toast.makeText(context, "Sem conexão com internet", Toast.LENGTH_LONG).show()
        } else {
            var serieClick = viewModel.listaSerie.value?.get(position) as EssencialSerie
            viewModel.getSeriesFromId(serieClick.serieid)
            viewModel.serieDetalhe.observe(viewLifecycleOwner) {
                val intent = Intent(context, DetalheSerieActivity::class.java)
                intent.putExtra("serieClick", it)
                intent.putExtra("serieDB", serieClick)
                intent.putExtra("origem", "ListaPessoal")
                startActivity(intent)
            }
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
    private fun startLoading() {
        progressbar_loading_fragseries.visibility = View.VISIBLE
        progressbar_loading_fragseries.background.alpha = 150
    }
    private fun endLoading() {
        progressbar_loading_fragseries.visibility = View.GONE
    }
}