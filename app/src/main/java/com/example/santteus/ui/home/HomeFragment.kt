package com.example.santteus.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.santteus.databinding.FragmentHomeBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("Binding이 초기화되지 않았습니다.")

    private lateinit var mView: MapView

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        mView = binding.map
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)



        return binding.root
    }


    override fun onMapReady(googleMap: GoogleMap) {

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = database.getReference("road")
        var latitude: Double
        var longitude: Double
        var name: String
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {

                    latitude = snapshot.child("COURS_SPOT_LA").value as Double
                    longitude = snapshot.child("COURS_SPOT_LO").value as Double
                    name = snapshot.child("WLK_COURS_NM").value as String

                    val marker = LatLng(latitude,longitude)
                    googleMap.addMarker(MarkerOptions().position(marker).title(name))

                    // moveCamera 현위치로 수정 필요
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        })


        //val marker = LatLng(37.568291,126.997780)
        //googleMap.addMarker(MarkerOptions().position(marker).title("여기"))
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
        //googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        mView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mView.onStop()
    }
    override fun onResume() {
        super.onResume()
        mView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }
    override fun onDestroy() {
        super.onDestroy()
        mView.onDestroy()
    }

}