package com.example.kelana.UI.BottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelana.Adapter.ListKategoriAdapter
import com.example.kelana.Data.KategoriData
import com.example.kelana.Model.Kategori
import com.example.kelana.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetNewsFragment : BottomSheetDialogFragment() {

    private lateinit var tv1 : TextView

    private lateinit var rvNews1 : RecyclerView

    private var listNews: ArrayList<Kategori> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv1 = view.findViewById(R.id.tv1)
        tv1.text = tag

        rvNews1 = view.findViewById(R.id.rv_Top3News)
        rvNews1.setHasFixedSize(true)

        listNews.addAll(KategoriData.listData)

        showRecyleList()
    }


    private fun showRecyleList() {


    }

}