package com.example.kelana.Adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kelana.Model.Destinasi
import com.example.kelana.Model.News
import com.example.kelana.R

class ListTop3NewsAdapter(private val listBerita: ArrayList<News>,mListener : IUnews) : RecyclerView.Adapter<ListTop3NewsAdapter.ListViewHolder>() {

    private var mListener = mListener
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View,mListener : IUnews ) : RecyclerView.ViewHolder(itemView) {

        var tvHeadline: TextView = itemView.findViewById(R.id.tv_headline)
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)

        var mListener = mListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_row_topnews, viewGroup, false)
        return ListViewHolder(view,mListener)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentBerita = listBerita[position]
        if (currentBerita.equals(null)) {
            println("KOSONG")
        }

        holder.itemView.apply {
            holder.tvHeadline.text = currentBerita.headline

            Glide.with(holder.itemView.context)
                .load(currentBerita.photo)
                .apply(RequestOptions())
                .into(holder.imgPhoto)
        }.setOnClickListener{ mView ->
            listBerita.clear()
            mListener.ToBrowser(currentBerita.link)
        }
    }

    override fun getItemCount(): Int {
        return listBerita.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Destinasi)

    }

    interface IUnews{
        fun ToBrowser(link : String){}
    }


}