package com.example.kelana.UI.Detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.kelana.R
import com.google.firebase.firestore.FirebaseFirestore

class DeskripsiFragment : Fragment() {

    companion object {
        const val DESTINASI_ID= "IDdestinasi"

        fun newInstance(DestinasiID : String): DeskripsiFragment{
            val fragment = DeskripsiFragment().apply{

                arguments =  Bundle().apply {
                    putString(DESTINASI_ID, DestinasiID)
                }

            }

            return fragment
        }
    }


    private lateinit var namaDestinasi2: TextView
    private lateinit var JamBuka: TextView
    private lateinit var deskripsi: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_deskripsi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        namaDestinasi2 = view.findViewById(R.id.tv_namaDestinasi2)
        JamBuka = view.findViewById(R.id.tv_jam_buka)
        deskripsi = view.findViewById(R.id.tv_deskripsi_destinasi)


        val queue =
            FirebaseFirestore.getInstance().collection("destinasi").document(arguments?.getString(
                DESTINASI_ID).toString())

        queue.get().addOnSuccessListener { document ->
            namaDestinasi2.text = document.getString("nama")
            JamBuka.text = document.getString("time")

            if (document.getString("deskripsi") != null) {

            deskripsi.text = document.getString("deskripsi")
                ?.replace("/n", "\n")
                ?.replace("/t", "\t\t\t")
                }

        }

    }
}