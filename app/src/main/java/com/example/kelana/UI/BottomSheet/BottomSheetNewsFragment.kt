package com.example.kelana.UI.BottomSheet

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelana.Adapter.ListDestinasiAdapter
import com.example.kelana.Adapter.ListSecondaryNewsAdapter
import com.example.kelana.Adapter.ListTop3NewsAdapter
import com.example.kelana.Data.KategoriData
import com.example.kelana.Model.Destinasi
import com.example.kelana.Model.Kategori
import com.example.kelana.Model.News
import com.example.kelana.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore


class BottomSheetNewsFragment : BottomSheetDialogFragment() , ListTop3NewsAdapter.IUnews, ListSecondaryNewsAdapter.IUnews{

    private lateinit var tv1 : TextView

    private lateinit var rvNews1 : RecyclerView
    private lateinit var rvNews2 : RecyclerView

    private lateinit var rfbaseNews: FirebaseFirestore
    private lateinit var Top3NewsAdapter: ListTop3NewsAdapter
    private lateinit var SecondaryNewsAdapter: ListSecondaryNewsAdapter

    private var listNews: ArrayList<News> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNews1 = view.findViewById(R.id.rv_Top3News)
        rvNews1.setHasFixedSize(true)

        rvNews2 = view.findViewById(R.id.rv_SecondaryNews)
        rvNews2.setHasFixedSize(true)

        LoadNews()
    }

    override fun ToBrowser(link: String) {
        super<ListSecondaryNewsAdapter.IUnews>.ToBrowser(link)
        super<ListTop3NewsAdapter.IUnews>.ToBrowser(link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    private fun LoadNews() {

        rfbaseNews = FirebaseFirestore.getInstance()
        rfbaseNews.collection("news").addSnapshotListener { data, error ->
            if (error != null) {
                Log.e("Firestore Error", error.message.toString())
                return@addSnapshotListener
            }

            for (dc: DocumentChange in data?.documentChanges!!) {

                if (dc.type == DocumentChange.Type.ADDED) {

                        listNews.add(dc.document.toObject(News::class.java))

                }
            }
            Top3NewsAdapter.notifyDataSetChanged()
        }

        rvNews1.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        Top3NewsAdapter = ListTop3NewsAdapter(listNews,this)
        rvNews1.adapter = Top3NewsAdapter

        rvNews2.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        SecondaryNewsAdapter = ListSecondaryNewsAdapter(listNews,this)
        rvNews2.adapter = SecondaryNewsAdapter

    }

}