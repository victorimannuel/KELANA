package com.example.kelana.Activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kelana.Adapter.DetailAdapter
import com.example.kelana.R
import com.example.kelana.helper.DynamicPagerCustom
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView

class DetailActivity : AppCompatActivity() {

    companion object {
        const val idDestinasi = "extra_id"
    }


    private lateinit var namaDestinasi1: TextView
    private lateinit var lokasiDestinasi: TextView
    private lateinit var FotoDestinasi: RoundedImageView
    private lateinit var btnToMaps: MaterialButton
    private lateinit var btnFavorite: MaterialButton
    private lateinit var btnBack: ImageView
    private lateinit var btnPesan: MaterialButton

    lateinit var tabLayout: TabLayout
    lateinit var viewPagerCustom: DynamicPagerCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        namaDestinasi1 = findViewById(R.id.tv_nama_Destinasi)
        lokasiDestinasi = findViewById(R.id.tv_lokasi_destinasi)
        FotoDestinasi = findViewById(R.id.img_item_photo)
        btnToMaps = findViewById(R.id.btnMaps)
        btnFavorite = findViewById(R.id.btnFavorite)
        btnBack = findViewById(R.id.back_button)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnPesan = findViewById(R.id.btn_pesan)
        btnPesan.setOnClickListener {
            showDialog()
        }

        //configuring tablayout and viewpager
        tabLayout = findViewById(R.id.tabs)
        viewPagerCustom = findViewById(R.id.view_pager)
        tabLayout.addTab(tabLayout.newTab().setText("Deskripsi"))
        tabLayout.addTab(tabLayout.newTab().setText("Peraturan"))
        tabLayout.addTab(tabLayout.newTab().setText("Review"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = DetailAdapter(
            this, supportFragmentManager,
            tabLayout.tabCount, intent.getStringExtra(idDestinasi).toString()
        )
        viewPagerCustom.adapter = adapter
        viewPagerCustom.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPagerCustom.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val queue =
            FirebaseFirestore.getInstance().collection("destinasi").document(
                intent.getStringExtra(
                    idDestinasi
                ).toString()
            )
        queue.get().addOnSuccessListener { document ->
            namaDestinasi1.text = document.getString("nama")
            lokasiDestinasi.text = document.getString("lokasi")

            Glide.with(this)
                .load(document.getString("photo"))
                .apply(RequestOptions())
                .into(FotoDestinasi)

            val lat = document.getString("latitude")
            val lng = document.getString("longitude")
            //opening google maps
            btnToMaps.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:$lat,$lng")

                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                mapIntent.setPackage("com.google.android.apps.maps")

                startActivity(mapIntent)
            }
        }
    }

    private fun showDialog() {
        println("btnPesan Tertekan")
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_pesan)

        val btn_ok = dialog.findViewById<Button>(R.id.btn_setuju)
        btn_ok.setOnClickListener {
            //intent to messenggers Apps

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra("address", "081334520958")
            intent.putExtra(Intent.EXTRA_TEXT, "Saya Ingin Memesan.")
            intent.type = "text/plain"

            startActivity(Intent.createChooser(intent, "Please Select app :"))
        }
        val btn_tidak = dialog.findViewById<Button>(R.id.btn_tidak)
        btn_tidak.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}