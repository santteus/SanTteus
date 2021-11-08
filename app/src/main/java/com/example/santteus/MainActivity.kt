package com.example.santteus

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.santteus.databinding.ActivityMainBinding
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            navView.isVisible =
                !(destination.id == R.id.signInFragment || destination.id == R.id.signUpFragment)

        }

        var sensorManager= getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        val sensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensor?.let {
            sensorManager?.registerListener(this@MainActivity, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }



    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return
        // Data 1: According to official documentation, the first value of the `SensorEvent` value is the step count
        sensorEvent.values.firstOrNull()?.let {
            Log.d("aaaa","Step count: $it ")

        }

        // Data 2: The number of nanosecond passed since the time of last boot
        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInNanos = sensorEvent.timestamp // The number of nanosecond passed since the time of last boot
        val sensorEventTimeInMillis = sensorEventTimeInNanos / 1000_000

        val actualSensorEventTimeInMillis = lastDeviceBootTimeInMillis + sensorEventTimeInMillis
        val displayDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(actualSensorEventTimeInMillis)
        Log.d("bbbb","Sensor event is triggered at $displayDateStr")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}