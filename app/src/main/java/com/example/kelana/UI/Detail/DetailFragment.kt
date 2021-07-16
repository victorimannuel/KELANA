package com.example.kelana.UI.Detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kelana.R
import com.example.kelana.databinding.FragmentDetailBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()

    private lateinit var namaDestinasi1: TextView
    private lateinit var namaDestinasi2: TextView
    private lateinit var lokasiDestinasi: TextView
    private lateinit var FotoDestinasi: RoundedImageView
    private lateinit var JamBuka: TextView
    private lateinit var deskripsi: TextView

    var id: String? = null

    private lateinit var btnToPeraturan: MaterialButton
    private lateinit var btnToReview: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        namaDestinasi1 = view.findViewById(R.id.tv_nama_Destinasi)
        namaDestinasi2 = view.findViewById(R.id.tv_namaDestinasi2)
        lokasiDestinasi = view.findViewById(R.id.tv_lokasi_destinasi)
        FotoDestinasi = view.findViewById(R.id.img_item_photo)
        JamBuka = view.findViewById(R.id.tv_jam_buka)
        deskripsi = view.findViewById(R.id.tv_deskripsi_destinasi)

        btnToPeraturan = view.findViewById(R.id.button_nav_peraturan)
        btnToReview = view.findViewById(R.id.button_nav_review)

        if (id == null) {
            id = args.idDestinasi
        }

        val queue =
            FirebaseFirestore.getInstance().collection("destinasi").document(id!!)
        queue.get().addOnSuccessListener { document ->
            namaDestinasi1.text = document.getString("nama")
            namaDestinasi2.text = document.getString("nama")
            JamBuka.text = document.getString("time")
            deskripsi.text = document.getString("deskripsi")
                ?.replace("/n", "\n")
                ?.replace("/t", "\t\t\t")
            lokasiDestinasi.text = document.getString("lokasi")

            Glide.with(view.context)
                .load(document.getString("photo"))
                .apply(RequestOptions())
                .into(FotoDestinasi)
        }

    }
}