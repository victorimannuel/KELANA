package com.example.kelana.UI.BottomSheet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelana.Activity.DetailActivity
import com.example.kelana.Adapter.ListDestinasiKategoriAdapter
import com.example.kelana.Model.Destinasi
import com.example.kelana.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class BottomSheetKategoriFragment : BottomSheetDialogFragment() , ListDestinasiKategoriAdapter.IUDestinasiPilihan {

    private lateinit var tv1 : TextView

    private lateinit var rv_Destinasi : RecyclerView

    private lateinit var rfbaseDestinasi: FirebaseFirestore
    private lateinit var DestinasiAdapter: ListDestinasiKategoriAdapter

    private var DestinasiArrayList: ArrayList<Destinasi> = arrayListOf<Destinasi>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_kategori, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_Destinasi = view.findViewById(R.id.rv_destinasiPerKategori)
        rv_Destinasi.setHasFixedSize(true)

        tv1 = view.findViewById(R.id.tvatas)
        tv1.setOnClickListener {

        }
        loadData()
    }

    private fun loadData() {
        rfbaseDestinasi = FirebaseFirestore.getInstance()
        rfbaseDestinasi.collection("destinasi")
            .whereEqualTo("kategori",tag).addSnapshotListener { data, error ->
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }
                var i = 0
                println("Data Size : ${data?.size()}")
                for (dc: DocumentChange in data?.documentChanges!!) {

                    if (dc.type == DocumentChange.Type.ADDED) {
                        println("Data ke-${i++} Added")
                        DestinasiArrayList.add(dc.document.toObject(Destinasi::class.java))
                    }
                }
                DestinasiAdapter.notifyDataSetChanged()
                println("Banyak data di array list : ${DestinasiArrayList.size}")

            }
        rv_Destinasi.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        DestinasiAdapter = ListDestinasiKategoriAdapter(DestinasiArrayList,this)
        rv_Destinasi.adapter = DestinasiAdapter
    }

    override fun ToDestinasiTerpilih(id: String) {
        super.ToDestinasiTerpilih(id)

        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.idDestinasi, id)
        startActivity(intent)

//        val toDetail = DetailFragment()
//        toDetail.id = id
//
//        val mFragmentManager = fragmentManager
//        mFragmentManager?.beginTransaction()?.apply {
//
//            replace(R.id.fragmentHomeContainerView,toDetail,DetailFragment::class.java.simpleName)
//            addToBackStack(null)
//            commit()
//            dialog?.dismiss()
//        }
    }
}