package com.example.santteus.ui.run

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.santteus.databinding.FragmentRunStartBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*


class RunStartFragment : BottomSheetDialogFragment(), SensorEventListener {


    lateinit var binding: FragmentRunStartBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index: Int = 1

    /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateDialog(savedInstanceState)

    }*/

    override fun getTheme(): Int {
        return com.example.santteus.R.style.CustomBottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRunStartBinding.inflate(requireActivity().layoutInflater)
        setListeners()
        start()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //(requireView().parent.parent.parent as View).fitsSystemWindows = false

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomView)


        val display = requireActivity().windowManager.defaultDisplay // in case of Activity
/* val display = activity!!.windowManaver.defaultDisplay */ // in case of Fragment
        val size = Point()
        display.getRealSize(size) // or getSize(size)
        val width = size.x
        val height = size.y

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        //bottomSheetBehavior.peekHeight=height/2
        // 드래그해도 팝업이 종료되지 않도록
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        var sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        val sensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensor?.let {
            sensorManager?.registerListener(
                this@RunStartFragment,
                it,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
        if (sensor == null) {
            Toast.makeText(context, "asdfasdf", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }


    fun setListeners() {
        binding.btnStartFinish.setOnClickListener {
            reset()
            RunFinishFragment().show(parentFragmentManager, "run")
            dialog?.dismiss()

        }
        binding.btnStartStop.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) pause() else start()
        }

    }

    private fun start() {

        timerTask = kotlin.concurrent.timer(period = 1000) {
            time++
            val sec = time
            val milli = time / 60
            val hour = (time / 60) / 60

            requireActivity().runOnUiThread {
                binding.tvRunTimeSecond.text = "$sec"
                binding.tvRunTimeMinute.text = "$milli"
            }
        }
    }

    private fun pause() {
        timerTask?.cancel()    // 안전한 호출(?.)로 timerTask가 null이 아니면 cancel() 호출
    }

    private fun reset() {
        timerTask?.cancel()

        time = 0
        isRunning = false
        binding.tvRunTimeSecond.text = "00"
        binding.tvRunTimeMinute.text = "00"
        index = 1
    }


    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return
        // Data 1: According to official documentation, the first value of the `SensorEvent` value is the step count
        sensorEvent.values.firstOrNull()?.let {
            Log.d("aaaa", "Step count: $it ")
            binding.tvRunDistanceCount.text = it.toString()
        }

        // Data 2: The number of nanosecond passed since the time of last boot
        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInNanos =
            sensorEvent.timestamp // The number of nanosecond passed since the time of last boot
        val sensorEventTimeInMillis = sensorEventTimeInNanos / 1000_000

        val actualSensorEventTimeInMillis = lastDeviceBootTimeInMillis + sensorEventTimeInMillis
        val displayDateStr =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(actualSensorEventTimeInMillis)
        Log.d("bbbb", "Sensor event is triggered at $displayDateStr")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("cccc", "onAccuracyChanged: Sensor: $sensor; accuracy: $accuracy")
    }

}