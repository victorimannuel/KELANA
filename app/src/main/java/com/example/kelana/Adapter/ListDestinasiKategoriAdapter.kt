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
import com.example.kelana.R

class ListDestinasiKategoriAdapter(
    private val listDestinasi: ArrayList<Destinasi>,
    mListener: ListDestinasiKategoriAdapter.IUDestinasiPilihan
) :
    RecyclerView.Adapter<ListDestinasiKategoriAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    private var mListener = mListener

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(
        itemView: View,
        mListener: ListDestinasiKategoriAdapter.IUDestinasiPilihan
    ) : RecyclerView.ViewHolder(itemView) {

        var mListener = mListener

        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)

        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvLocation: TextView = itemView.findViewById(R.id.tv_item_loc)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_row_destinasiperkategori, viewGroup, false)
        return ListViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentdestinasi = listDestinasi[position]

        holder.itemView.apply {
            holder.tvName.text = currentdestinasi.nama
            holder.tvLocation.text = currentdestinasi.lokasi

            Glide.with(holder.itemView.context)
                .load(currentdestinasi.photo)
                .apply(RequestOptions())
                .into(holder.imgPhoto)
        }.setOnClickListener { mView ->
            mListener.ToDestinasiTerpilih(currentdestinasi.id)
        }
    }

    override fun getItemCount(): Int {
        return listDestinasi.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Destinasi)

    }

    interface IUDestinasiPilihan {
        fun ToDestinasiTerpilih(id: String) {}
    }


}