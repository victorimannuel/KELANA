package com.example.kelana.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kelana.Model.Destinasi
import com.example.kelana.R

class ListDestinasiAdapter(private val listDestinasi: ArrayList<Destinasi>, mListener : IUDestinasi) :
    RecyclerView.Adapter<ListDestinasiAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    private var mListener = mListener

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View, mListener : IUDestinasi) : RecyclerView.ViewHolder(itemView) {

        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvLocation: TextView = itemView.findViewById(R.id.tv_item_loc)

        var mListener = mListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_row_destinasi, viewGroup, false)
        return ListViewHolder(view,mListener)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentdestinasi = listDestinasi[position]
        if (currentdestinasi.equals(null)) {
            println("KOSONG")
        }

        holder.itemView.apply {
            holder.tvName.text = currentdestinasi.nama
            holder.tvLocation.text = currentdestinasi.lokasi

            Glide.with(holder.itemView.context)
                .load(currentdestinasi.photo)
                .apply(RequestOptions())
                .into(holder.imgPhoto)
        }.setOnClickListener{ mView ->

            mListener.ToDetail(currentdestinasi.id)
//            val toDetail = HomeFragmentDirections.actionHomeFragmentToDetailFragment2(currentdestinasi.id)
//            mView.findNavController().navigate(toDetail)



        }
    }

    override fun getItemCount(): Int {
        return listDestinasi.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Destinasi)

    }

    interface IUDestinasi {
        fun ToDetail(idDestinasi : String){}
    }


}