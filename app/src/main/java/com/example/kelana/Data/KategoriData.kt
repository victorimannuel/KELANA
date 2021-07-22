package com.example.kelana.Data

import com.example.kelana.Model.Kategori
import com.example.kelana.R

object KategoriData {
    private val nama = arrayOf("pantai" , "gunung" , "urban" , "cultural" , "religi")

    private val photos = intArrayOf(
        R.drawable.pantai_selatan2,
        R.drawable.kelud,
        R.drawable.kampung_warna,
        R.drawable.pura,
        R.drawable.masjid
    )

    val listData: ArrayList<Kategori>
        get() {
            val list = arrayListOf<Kategori>()
            for (position in photos.indices) {
                val kategori = Kategori()
                kategori.namaKategori = nama[position]
//                photos.creator = creator[position]
//                photos.detail = PhotoDetails[position]
                kategori.photo = photos[position]
                list.add(kategori)
            }
            return list
        }
}