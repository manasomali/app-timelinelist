package com.manasomali.timelinelist.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.manasomali.timelinelist.Constants.BASE_IMAGE_URL
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.helpers.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.obra_item.view.*

class ListaObrasAdapter(private val listObra: BaseFilmeBusca, val listener: OnObraClickListener): RecyclerView.Adapter<ListaObrasAdapter.ListaObraViewHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaObraViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.obra_item, parent, false)
        return ListaObraViewHolder(itemView)
    }

    override fun getItemCount() = obraFilterList.size

    override fun onBindViewHolder(holder: ListaObraViewHolder, position: Int) {

        var obra = obraFilterList[position]
        holder.textview_nomeobra.text = obra.title
        Picasso.get().load(Uri.parse("$BASE_IMAGE_URL${obra.backdropPath}")).into(holder.imageview_backdropobra)

    }
    inner class ListaObraViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val textview_nomeobra: TextView = itemView.textview_nomeobra
        val imageview_backdropobra: ImageView = itemView.imageview_backdropobra

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (RecyclerView.NO_POSITION != position) {
                listener.obraClick(position)
            }
        }
    }
    interface OnObraClickListener {
        fun obraClick(position: Int)

    }

    var obraFilterList = ArrayList<BaseFilmeBusca.Result>()

    init {
        obraFilterList = listObra.results
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    obraFilterList = listObra.results
                } else {
                    val resultList = ArrayList<BaseFilmeBusca.Result>()
                    for (row in listObra.results) {
                        if (row.title.toLowerCase().contains(charSearch.toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    obraFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = obraFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                obraFilterList = results?.values as ArrayList<BaseFilmeBusca.Result>
                notifyDataSetChanged()
            }

        }
    }

}