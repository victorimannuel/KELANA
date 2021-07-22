package com.example.kelana.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kelana.Model.Destinasi
import com.example.kelana.Model.Kategori
import com.example.kelana.R
import com.example.kelana.UI.BottomSheet.BottomSheetKategoriFragment

class ListKategoriAdapter (private val listKategori: ArrayList<Kategori>, mListener : IUDestinasiRecyler)  : RecyclerView.Adapter<ListKategoriAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private var mListener = mListener

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder (itemView: View,mListener: IUDestinasiRecyler) : RecyclerView.ViewHolder(itemView){

        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var name : TextView = itemView.findViewById(R.id.tv_namaKategori)

        var mListener = mListener


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_row_kategori, viewGroup, false)
        return ListViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val kategori = listKategori[position]
        Glide.with(holder.itemView.context)
            .load(kategori.photo)
            .apply(RequestOptions())
            .into(holder.imgPhoto)
        holder.name.text = kategori.namaKategori

        holder.itemView.setOnClickListener {
            mListener.ToKategoriTerpilih(kategori.namaKategori)
        }
    }

    override fun getItemCount(): Int {
        return listKategori.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Destinasi)
    }

    interface IUDestinasiRecyler{
        fun ToKategoriTerpilih(Kategori : String) {
        }
    }

}