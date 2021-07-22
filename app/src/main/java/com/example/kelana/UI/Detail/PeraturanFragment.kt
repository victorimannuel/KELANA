package com.example.kelana.UI.Detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.kelana.R
import com.google.firebase.firestore.FirebaseFirestore

class PeraturanFragment : Fragment() {

    companion object {
        const val DESTINASI_ID = "IDdestinasi"

        fun newInstance(DestinasiID: String): PeraturanFragment {
            val fragment = PeraturanFragment().apply {

                arguments = Bundle().apply {
                    putString(DESTINASI_ID, DestinasiID)
                }

            }

            return fragment
        }
    }

    private lateinit var tvPeraturan: TextView
    private lateinit var namaDestinasi2: TextView
    private lateinit var JamBuka: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_peraturan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        namaDestinasi2 = view.findViewById(R.id.tv_namaDestinasi2)
        JamBuka = view.findViewById(R.id.tv_jam_buka)
        tvPeraturan = view.findViewById(R.id.tv_peraturan_destinasi)

        val destinasiID = arguments?.getString(DESTINASI_ID)

        val queue =
            FirebaseFirestore.getInstance().collection("destinasi").document(
                arguments?.getString(
                    DESTINASI_ID
                ).toString()
            )
        queue.get().addOnSuccessListener { document ->

            namaDestinasi2.text = document.getString("nama")
            JamBuka.text = document.getString("time")


            var temp = document.get("peraturan")
            if (temp != null) {
                tvPeraturan.text = ""
                temp = temp.toString().replace("[", "").replace("]", "").split(", ")

                var i = 0

                for (peraturan in temp) {
                    tvPeraturan.append("${++i}. $peraturan\n")

                }
            }else{

            }


        }

    }

}