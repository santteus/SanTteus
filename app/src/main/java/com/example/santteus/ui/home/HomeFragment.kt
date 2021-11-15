package com.example.santteus.ui.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle

import android.os.SystemClock
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.santteus.databinding.FragmentHomeBinding
import com.example.santteus.ui.run.RunFinishFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.*


class HomeFragment : Fragment(), OnMapReadyCallback, SensorEventListener, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("Binding이 초기화되지 않았습니다.")

    private lateinit var mView: MapView

    // 전달용 변수
    private var roadName = ""
    private var roadPosition = LatLng(37.568291, 126.997780)

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index: Int = 1
    private var mSteps = 0
    private var mStepsCount = 0

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var detailBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var sensorManager :SensorManager

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        mView = binding.map
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)
        setListeners()
        setBottomSheet()
        setSensorCount()
        setDetailBottomSheet()

        return binding.root
    }

    private fun setSensorCount() {
        sensorManager = (context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?)!!
        val sensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensor?.let {
            sensorManager?.registerListener(
                this@HomeFragment,
                it,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    private fun setListeners() {

        //운동 시작버튼 클릭이벤트
        binding.detailBottom.btnRunStart.setOnClickListener {

        }

        binding.btnRun.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.isDraggable = false
            start()
            mSteps = 0
            mStepsCount = 0
        }
        binding.mypageBottom.btnStartFinish.setOnClickListener {
            reset()
            RunFinishFragment().show(parentFragmentManager, "run")
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.mypageBottom.btnStartStop.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) pause() else start()
        }
    }

    private fun start() {

        timerTask = kotlin.concurrent.timer(period = 1000) {
            time++

            val h = time / 3600
            val m = time % 3600 / 60
            val s = time % 60

            activity?.runOnUiThread {

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
        val marker = LatLng(37.568291, 126.997780)
        googleMap.addMarker(MarkerOptions().position(marker).title("여기"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))


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

        googleMap.setOnMarkerClickListener(this)

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
        sensorManager?.unregisterListener(this)
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
                // initial value
                mStepsCount = it.toInt()
            }
            mSteps = it.toInt() - mStepsCount
            binding.mypageBottom.tvRunStepCount.text = mSteps.toString()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onMarkerClick(p0: Marker): Boolean {

        // 마커 클릭 후 변수에 위치 이름, 경/위도 저장
        roadName = p0.title.toString()
        roadPosition = p0.position

        binding.detailBottom.explain.setMovementMethod(ScrollingMovementMethod());

        var distanceLong : Long
        var distanceDouble : Double
        var time : String
        var toilet : String
        var conven : String

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = database.getReference("road")

        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {
                    if(snapshot.child("WLK_COURS_NM").value == roadName){

                        time = snapshot.child("COURS_TIME_CN").value as String
                        if (snapshot.child("COURS_DETAIL_LT_CN").value is Long){
                            distanceLong = snapshot.child("COURS_DETAIL_LT_CN").value as Long
                            binding.detailBottom.distanceTime.setText(distanceLong.toString() + "km | " + time + "분")
                        }else if(snapshot.child("COURS_DETAIL_LT_CN").value is Double){
                            distanceDouble = snapshot.child("COURS_DETAIL_LT_CN").value as Double
                            binding.detailBottom.distanceTime.setText(distanceDouble.toString() + "km | " + time + "분")
                        }

                        toilet = snapshot.child("TOILET_DC").value as String
                        conven = snapshot.child("CVNTL_NM").value as String

                        binding.detailBottom.roadName.setText(snapshot.child("WLK_COURS_NM").value as String)
                        binding.detailBottom.roadLevel.setText(snapshot.child("COURS_LEVEL_NM").value as String)


                        if(toilet!="" && toilet!="없음"){
                            if(conven!="" && conven!="없음"){
                                binding.detailBottom.toiletConve.setText("화장실 | 편의점")
                            }else{
                                binding.detailBottom.toiletConve.setText("화장실 | 편의점없음")
                            }
                        }else{
                            if(conven!="" && conven!="없음"){
                                binding.detailBottom.toiletConve.setText("화장실없음 | 편의점")
                            }else{
                                binding.detailBottom.toiletConve.setText("화장실없음 | 편의점없음")
                            }
                        }

                        binding.detailBottom.route.setText(snapshot.child("COURS_DC").value as String)
                        binding.detailBottom.explain.setText(snapshot.child("ADIT_DC").value as String)

                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        })


        detailBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        detailBottomSheetBehavior.isDraggable = true

        return true

    }



    private fun setDetailBottomSheet() {
        detailBottomSheetBehavior = BottomSheetBehavior.from(binding.detailBottom.bottomView)
        detailBottomSheetBehavior.isGestureInsetBottomIgnored = true
        detailBottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isDraggable = true
        }
        detailBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    //detailBottomSheetBehavior.peekHeight = 430
//                    if (newState != BottomSheetBehavior.STATE_EXPANDED){
//                        detailBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//                        detailBottomSheetBehavior.peekHeight = 430
//                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

}