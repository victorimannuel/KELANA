package com.example.kelana.UI.home

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelana.Adapter.ListDestinasiAdapter
import com.example.kelana.Adapter.ListKategoriAdapter
import com.example.kelana.Data.KategoriData
import com.example.kelana.Model.Destinasi
import com.example.kelana.Model.Kategori
import com.example.kelana.R
import com.example.kelana.UI.BottomSheet.BottomSheetKategoriFragment
import com.example.kelana.UI.BottomSheet.BottomSheetNewsFragment
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() , ListKategoriAdapter.IUDestinasiRecyler{

    private lateinit var tvUserLocation: TextView
    private lateinit var etSearchBar: TextInputLayout
    private lateinit var btnsheet: Button
    private lateinit var btnCustomerService: ImageButton

    private lateinit var rvDestinasi: RecyclerView
    private lateinit var rvKategori: RecyclerView

    private lateinit var rfbaseDestinasi: FirebaseFirestore
    private lateinit var DestinasiAdapter: ListDestinasiAdapter

    private var DestinasiArrayList: ArrayList<Destinasi> = arrayListOf<Destinasi>()
    private var DestinasiArrayListPilihan: ArrayList<Destinasi> = arrayListOf<Destinasi>()
    private var list2: ArrayList<Kategori> = arrayListOf()
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var currentLocation: Location

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnsheet = view.findViewById(R.id.btn_openSheet)

        val bottomsheetnews  = BottomSheetNewsFragment()
        btnsheet.setOnClickListener{
            fragmentManager?.let { it1 -> bottomsheetnews.show(it1,"BottomSheet Showing") }
        }


        btnCustomerService = view.findViewById(R.id.btn_callCS)
        btnCustomerService.setOnClickListener {

        }

        tvUserLocation = view.findViewById(R.id.tv_location)
        etSearchBar = view.findViewById(R.id.et_searchbar)

        rvDestinasi = view.findViewById(R.id.rv_heroes)
        rvDestinasi.setHasFixedSize(true)

        rvKategori = view.findViewById(R.id.rv_kategori)
        rvKategori.setHasFixedSize(true)

        LoadData()

        list2.addAll(KategoriData.listData)

        showRecyclerList()

        Dexter.withContext(context)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    tvUserLocation.visibility = View.GONE
                    initLocation()
                    ShowAddress()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(context, "You Must Enable This Permission", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

            }).check()
    }

    override fun ToKategoriTerpilih(Kategori: String) {
        super.ToKategoriTerpilih(Kategori)
        val bottomSheetPerKategori = BottomSheetKategoriFragment()
        fragmentManager?.let { it1 -> bottomSheetPerKategori.show(it1,Kategori) }
    }

    private fun LoadData() {

        val locationUser = Location("")
        locationUser.latitude = lat
        locationUser.longitude = lng

        rfbaseDestinasi = FirebaseFirestore.getInstance()
        rfbaseDestinasi.collection("destinasi").addSnapshotListener { data, error ->
            if (error != null) {
                Log.e("Firestore Error", error.message.toString())
                return@addSnapshotListener
            }

            for (dc: DocumentChange in data?.documentChanges!!) {

                if (dc.type == DocumentChange.Type.ADDED) {

                    DestinasiArrayList.add(dc.document.toObject(Destinasi::class.java))
                }
            }
            DestinasiAdapter.notifyDataSetChanged()

        }

        rvDestinasi.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        DestinasiAdapter = ListDestinasiAdapter(DestinasiArrayList)
        rvDestinasi.adapter = DestinasiAdapter

        DestinasiAdapter.setOnItemClickCallback(object : ListDestinasiAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Destinasi) {
                list2.clear()
            }
        })

//        val queue =
//            FirebaseFirestore.getInstance().collection("destinasi").document("8sz6633qe0BTzwRKaJnv")
//        queue.get().addOnSuccessListener { document ->
//            val temp = document.getString("nama")
//            println("nama destinasi pilihan : $temp")
//        }


    }

    private fun showRecyclerList() {

        rvKategori.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val listKategoriAdapter = ListKategoriAdapter(list2,this)
        rvKategori.adapter = listKategoriAdapter

    }

    private fun initLocation() {
        BuildLocationRequest()
        BuildLocationCallback()
        fusedLocationProviderClient =
            context?.let { LocationServices.getFusedLocationProviderClient(it) }

        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.getMainLooper()
            )

        }

    }

    private fun BuildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                currentLocation = p0!!.lastLocation
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
            }
        }
    }

    private fun BuildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5000)
        locationRequest.setFastestInterval(3000)
        locationRequest.setSmallestDisplacement(10f)

    }

    private fun ShowAddress() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.getLastLocation()
                .addOnFailureListener { e ->
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener { task ->
                    val location = task.result
                    if (location != null) {
                        try {
                            var geocoder = Geocoder(context, Locale.getDefault())
                            var Adress =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            lat = Adress[0].latitude
                            lng = Adress[0].longitude

                            val Coordinate = StringBuilder()
                                .append(lat)
                                .append("/")
                                .append(lng)
                                .toString()

                            var City_Country: String

                            City_Country =
                                "${Adress.get(0).subAdminArea}, ${Adress.get(0).adminArea}, ${
                                    Adress.get(0).countryName
                                }"

                            tvUserLocation.setText(City_Country)
                            tvUserLocation.visibility = View.VISIBLE

                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "Location null", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    override fun onStop() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
        }
        super.onStop()
    }

    override fun onResume() {

        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest, locationCallback!!,
                Looper.getMainLooper()
            )

        }
        super.onResume()
    }

    private fun showSelectedHero(photo: Destinasi) {
//        Toast.makeText(this, "Kamu memilih " + photo.title, Toast.LENGTH_SHORT).show()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMode(selectedMode: Int) {
        when (selectedMode) {
//            R.id.action_settings -> {
//                val intent = Intent(this, AboutActivity::class.java)
//                startActivity(intent)
//            }
        }
    }
}