package com.example.santteus.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.santteus.MainActivity
import com.example.santteus.databinding.FragmentHomeBinding
import com.example.santteus.ui.run.RunFinishFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*
import com.google.firebase.database.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.example.santteus.R;

class HomeFragment : Fragment(), OnMapReadyCallback, SensorEventListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("Binding이 초기화되지 않았습니다.")

    private lateinit var mView: MapView
    private val PERMISSIONS_REQUEST_CODE = 999
    private lateinit var mainActivity: MainActivity
    private var mMap: GoogleMap? = null

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.getReference("road")

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index: Int = 1
    private var mSteps = 0
    private var mStepsCount = 0

    private var userTime = ""
    private var userTimeSeconds = 0
    private var userDistance = ""
    private var userStep = 0

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var sensorManager: SensorManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        mainActivity = context as MainActivity
        mView = binding.map
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)

        setListeners()
        setBottomSheet()
        setSensorCount()

        mMap?.let { onMapReady(it) }
        checkCategory()

        return binding.root
    }

    private fun setSensorCount() {
        sensorManager = (context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?)!!
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensor?.let {
            sensorManager.registerListener(
                this@HomeFragment,
                it,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    private fun setListeners() {
        binding.btnRun.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.isDraggable = false
            start()
            mSteps = 0
            mStepsCount = 0
        }
        binding.mypageBottom.btnStartFinish.setOnClickListener {
            RunFinishFragment(userTime, userTimeSeconds, userDistance, userStep).show(
                parentFragmentManager,
                "run"
            )
            reset()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.mypageBottom.btnStartStop.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) pause() else start()
        }

        // 검색 버튼 클릭 시
        binding.ivHomeSearch.setOnClickListener {

        }
    }

    private fun start() {

        timerTask = kotlin.concurrent.timer(period = 1000) {
            time++

            val h = time / 3600
            val m = time % 3600 / 60
            val s = time % 60

            activity?.runOnUiThread {
                userTimeSeconds = s
                userTime = "%1$02d:%2$02d:%3$02d".format(h, m, s)
                binding.mypageBottom.tvRunTime.text = "%1$02d:%2$02d:%3$02d".format(h, m, s)

            }
        }
    }

    private fun pause() {
        timerTask?.cancel()
    }

    private fun reset() {
        timerTask?.cancel()
        time = 0
        isRunning = false
        binding.mypageBottom.tvRunTime.text = "00:00:00"
        index = 1
    }

    private fun setBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.mypageBottom.bottomView)
        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isDraggable = false
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val marker = LatLng(37.568291, 126.997780)
        mMap?.addMarker(MarkerOptions().position(marker).title("기본 위치"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(marker))
        mMap?.moveCamera(CameraUpdateFactory.zoomTo(15f))

        if (checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), // 1
                PERMISSIONS_REQUEST_CODE
            ) // 2
            return
        }

        mMap?.isMyLocationEnabled = true
        mMap?.moveCamera(CameraUpdateFactory.zoomTo(15f))

        createMark()
    }

    // 카테고리 선택
    private fun checkCategory() {
        binding.btnHomeDiet.setOnClickListener {
            binding.btnHomeDiet.isSelected = binding.btnHomeDiet.isSelected != true
            if(binding.btnHomeDiet.isSelected) {
                recommendedMark()
            }else {
                createMark()
            }
        }

        binding.btnHomeStrength.setOnClickListener {
            binding.btnHomeStrength.isSelected = binding.btnHomeStrength.isSelected != true
            if(binding.btnHomeStrength.isSelected) {
                recommendedMark()
            }else {
                createMark()
            }
        }

        binding.btnHomeMood.setOnClickListener {
            binding.btnHomeMood.isSelected = binding.btnHomeMood.isSelected != true
            if(binding.btnHomeMood.isSelected) {
                recommendedMark()
            }else {
                createMark()
            }
        }

    }

    // 산책로 마커 생성
    private fun createMark(){
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {

                    var latitude = snapshot.child("COURS_SPOT_LA").value as Double
                    var longitude = snapshot.child("COURS_SPOT_LO").value as Double
                    var name = snapshot.child("WLK_COURS_NM").value as String

                    // custom marker
                    val bitmapdraw = context!!.resources.getDrawable(
                        R.drawable.pin_normal,
                        context!!.theme
                    ) as BitmapDrawable
                    val b = bitmapdraw.bitmap
                    val smallMarker = Bitmap.createScaledBitmap(b, 95, 140, false)

                    val marker = LatLng(latitude, longitude)

                    mMap?.addMarker(
                        MarkerOptions().position(marker).title(name)
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.tException())); // 에러문 출력
            }
        })
    }

    // 카테고리 클릭 시 마커 변경
    private fun recommendedMark() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {

                    val latitude = snapshot.child("COURS_SPOT_LA").value as Double
                    val longitude = snapshot.child("COURS_SPOT_LO").value as Double
                    val name = snapshot.child("WLK_COURS_NM").value as String
                    val level = snapshot.child("COURS_LEVEL_NM").value as String

                    if (binding.btnHomeDiet.isSelected) {
                       if (level == "어려움" || level == "매우 어려움") {
                            val bitmapdraw2 = context!!.resources.getDrawable(
                                R.drawable.pin_recommend,
                                context!!.theme
                            ) as BitmapDrawable
                            val b2 = bitmapdraw2.bitmap
                            val smallMarker2 = Bitmap.createScaledBitmap(b2, 95, 140, false)

                            val marker2 = LatLng(latitude, longitude)
                            mMap?.addMarker(
                                MarkerOptions().position(marker2).title(name)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2))
                            )
                        }
                    }

                    if (binding.btnHomeStrength.isSelected) {
                        if (level == "보통") {
                            val bitmapdraw2 = context!!.resources.getDrawable(
                                R.drawable.pin_recommend,
                                context!!.theme
                            ) as BitmapDrawable
                            val b2 = bitmapdraw2.bitmap
                            val smallMarker2 = Bitmap.createScaledBitmap(b2, 95, 140, false)

                            val marker2 = LatLng(latitude, longitude)
                            mMap?.addMarker(
                                MarkerOptions().position(marker2).title(name)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2))
                            )
                        }
                    }

                    if (binding.btnHomeMood.isSelected) {
                        if (level == "쉬움" || level == "매우 쉬움") {
                            val bitmapdraw2 = context!!.resources.getDrawable(
                                R.drawable.pin_recommend,
                                context!!.theme
                            ) as BitmapDrawable
                            val b2 = bitmapdraw2.bitmap
                            val smallMarker2 = Bitmap.createScaledBitmap(b2, 95, 140, false)

                            val marker2 = LatLng(latitude, longitude)
                            mMap?.addMarker(
                                MarkerOptions().position(marker2).title(name)
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker2))
                            )
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        mView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mView.onPause()
        pause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mView.onDestroy()
    }


    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return
        sensorEvent.values.firstOrNull()?.let {
            Log.d("aaaa", "Step count: $it ")
            if (mStepsCount < 1) {
                mStepsCount = it.toInt()
            }
            mSteps = it.toInt() - mStepsCount
            userStep = mSteps
            binding.mypageBottom.tvRunStepCount.text = mSteps.toString()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}