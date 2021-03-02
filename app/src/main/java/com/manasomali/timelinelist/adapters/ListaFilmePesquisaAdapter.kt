package com.manasomali.timelinelist.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.manasomali.timelinelist.Constants
import com.manasomali.timelinelist.Constants.BASE_IMAGE_URL
import com.manasomali.timelinelist.R
import com.manasomali.timelinelist.helpers.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.obra_item.view.*
import java.text.SimpleDateFormat


class ListaFilmePesquisaAdapter(
    private val listObra: BaseFilmeBusca,
    val listener: OnObraFilmeClickListener
): RecyclerView.Adapter<ListaFilmePesquisaAdapter.ListaFilmePesquisaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaFilmePesquisaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.obra_item,
            parent,
            false)
        return ListaFilmePesquisaViewHolder(itemView)
    }

    override fun getItemCount() = listObra.results.size

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ListaFilmePesquisaViewHolder, position: Int) {

        var obra = listObra.results[position]
        holder.textview_nomeobra.text = obra.title


        val formatter = SimpleDateFormat("yyyy")
        val dateFormat = SimpleDateFormat("yyyy-mm-dd")
        if ((obra.releaseDate!="")&&(obra.releaseDate!=null)) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            holder.textview_dateobra.text = formatter.format(dateFormat.parse(obra.releaseDate))
        }

        var ids= arrayListOf<String>()
        for (id in obra.genreIds) {
            for (item in Constants.GENEROS_FILME)
                if(id==item.key)
                    ids.add(item.value)
        }
        holder.textview_generoobra.text = ids.toString().replace("[", "").replace("]", "")
        holder.textview_notaobra.text = "${obra.voteAverage}/10"
        Picasso.get().load(Uri.parse("$BASE_IMAGE_URL${obra.backdropPath}")).placeholder(R.drawable.ic_logo).into(
            holder.imageview_backdropobra)

    }
    inner class ListaFilmePesquisaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val textview_nomeobra: TextView = itemView.textview_nomeobra
        val imageview_backdropobra: ImageView = itemView.imageview_backdropobra
        val textview_dateobra: TextView = itemView.textview_dateobra
        val textview_generoobra: TextView = itemView.textview_generoobra
        val textview_notaobra: TextView = itemView.textview_notaobra

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (RecyclerView.NO_POSITION != position) {
                listener.obraFilmeClick(position)
            }
        }
    }
    interface OnObraFilmeClickListener {
        fun obraFilmeClick(position: Int)
    }

}