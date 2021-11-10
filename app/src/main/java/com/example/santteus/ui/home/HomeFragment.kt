package com.example.santteus.ui.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.santteus.databinding.FragmentHomeBinding
import com.example.santteus.ui.run.RunFinishFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback, SensorEventListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("Binding이 초기화되지 않았습니다.")

    private lateinit var mView: MapView

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index: Int = 1
    private var mSteps = 0
    private var mStepsCount = 0

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
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

}