package com.example.kelana.UI.home

import android.Manifest
import android.content.Intent
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
import com.example.kelana.Activity.DetailActivity
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
import com.google.maps.android.SphericalUtil
import com.google.android.gms.maps.model.LatLng;


class HomeFragment : Fragment(), ListKategoriAdapter.IUDestinasiRecyler,
    ListDestinasiAdapter.IUDestinasi {

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

        val bottomsheetnews = BottomSheetNewsFragment()
        btnsheet.setOnClickListener {
            fragmentManager?.let { it1 -> bottomsheetnews.show(it1, "BottomSheet Showing") }
        }


        btnCustomerService = view.findViewById(R.id.btn_callCS)
        btnCustomerService.setOnClickListener {
            CallCS()
        }

        tvUserLocation = view.findViewById(R.id.tv_location)
        etSearchBar = view.findViewById(R.id.et_searchbar)

        rvDestinasi = view.findViewById(R.id.rv_heroes)
        rvDestinasi.setHasFixedSize(true)

        rvKategori = view.findViewById(R.id.rv_kategori)
        rvKategori.setHasFixedSize(true)

        list2.addAll(KategoriData.listData)

        showRecyclerList()

        Dexter.withContext(context)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    tvUserLocation.visibility = View.GONE
                    initLocation()
                    ShowAddress()

                    println("User Location, lat : $lat & long : $lng")
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

    private fun CallCS() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra("address", "081334520958")
        intent.putExtra(Intent.EXTRA_TEXT, "Saya Ingin Berkonsultasi")
        intent.type = "text/plain"

        startActivity(Intent.createChooser(intent, "Please Select app :"))
    }

    override fun ToKategoriTerpilih(Kategori: String) {
        super.ToKategoriTerpilih(Kategori)
        val bottomSheetPerKategori = BottomSheetKategoriFragment()
        fragmentManager?.let { it1 -> bottomSheetPerKategori.show(it1, Kategori) }
    }

    private fun LoadData(lat: Double, lng: Double) {

        val locationUser = Location("")
        locationUser.latitude = lat
        locationUser.longitude = lng



        println("User Location : $locationUser")

        rfbaseDestinasi = FirebaseFirestore.getInstance()
        rfbaseDestinasi.collection("destinasi").addSnapshotListener { data, error ->
            if (error != null) {
                Log.e("Firestore Error", error.message.toString())
                return@addSnapshotListener
            }


            for (dc: DocumentChange in data?.documentChanges!!) {

                if (dc.type == DocumentChange.Type.ADDED) {
                    val locationDestinasi = Location("")

                    val latDestinasi = dc.document.getString("latitude")
                    val lngDestinasi = dc.document.getString("longitude")

                    locationDestinasi.longitude = lngDestinasi?.toDouble() ?: 0.0
                    locationDestinasi.latitude = latDestinasi?.toDouble() ?: 0.0

                    println("Lokasi Destinasi ${dc.document.getString("nama")} : $locationDestinasi")
                    val distance = locationUser.distanceTo(locationDestinasi) / 1000
                    val loc1 = LatLng(lat,lng)

                    val loc2 = LatLng(latDestinasi?.toDouble() ?: 0.0,lngDestinasi?.toDouble() ?: 0.0)

                    val test = SphericalUtil.computeDistanceBetween(loc1, loc2)
                    println("Distance between user and location : $distance")
                    println("Distance between user and location with spherical : $test")

                    if (distance < 100) {
                        DestinasiArrayList.add(dc.document.toObject(Destinasi::class.java))
                    }
                }
            }
            DestinasiAdapter.notifyDataSetChanged()

        }

        rvDestinasi.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        DestinasiAdapter = ListDestinasiAdapter(DestinasiArrayList, this)
        rvDestinasi.adapter = DestinasiAdapter

        DestinasiAdapter.setOnItemClickCallback(object : ListDestinasiAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Destinasi) {
                list2.clear()
            }
        })
    }

    override fun ToDetail(idDestinasi: String) {
        super.ToDetail(idDestinasi)
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.idDestinasi, idDestinasi)
        startActivity(intent)
    }

    private fun showRecyclerList() {

        rvKategori.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val listKategoriAdapter = ListKategoriAdapter(list2, this)
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

                            LoadData(lat, lng)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}