package com.example.santteus.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.santteus.MainActivity
import com.example.santteus.databinding.FragmentHomeBinding
import com.example.santteus.ui.run.RunFinishFragment
import com.google.android.gms.maps.*

import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*
import com.google.firebase.database.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.example.santteus.R;
import com.example.santteus.ui.run.RunViewModel
import com.example.santteus.util.DistanceManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import java.io.IOException
import java.lang.Exception




class HomeFragment : Fragment(), OnMapReadyCallback, SensorEventListener, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("Binding이 초기화되지 않았습니다.")

    private val viewModel:RunViewModel by activityViewModels()

    private lateinit var mView: MapView
    private val PERMISSIONS_REQUEST_CODE = 999
    private lateinit var mainActivity: MainActivity
    private var mMap: GoogleMap? = null

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.getReference("road")

    // 전달용 변수
    private var roadName = ""
    private var roadPosition = LatLng(37.568291, 126.997780)
    var isFirst = false
    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index: Int = 1
    private var mSteps = 0
    private var mStepsCount = 0
    private var mDistance=0

    private var userTime = ""
    private var userTimeSeconds = 0
    private var userDistance = ""
    private var userStep = 0

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var detailBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var sensorManager :SensorManager
    private lateinit var locationManager: LocationManager
    private val polyLineOptions= PolylineOptions().width(5f).color(Color.parseColor("#FBAB57"))
    var latitude:Double = 0.0
    var longitude:Double = 0.0
    var latitude1:Double = 0.0
    var longitude1:Double = 0.0
    var latitude2:Double = 0.0
    var longitude2:Double = 0.0
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val PERMISSIONS_REQUEST_CODE_GPS = 100

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient // 위치 요청 메소드 담고 있는 객체
    private lateinit var locationRequest:LocationRequest // 위치 요청할 때 넘겨주는 데이터에 관한 객체
    private lateinit var locationCallback:LocationCallback // 위치 확인되고 호출되는 객체
    private val REQUEST_ACCESS_FINE_LOCATION = 1000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.vm=viewModel
        binding.lifecycleOwner=viewLifecycleOwner
        mainActivity = context as MainActivity

        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

        requirePermissions(permissions, 999)


        mView.onCreate(savedInstanceState)
        setListeners()
        setBottomSheet()
        setSensorCount()
        setDetailBottomSheet()
        // mMap?.let { onMapReady(it) }
        //checkCategory()

        observers()
        // mView.getMapAsync(this)
        return binding.root
    }

    private fun observers(){
        viewModel.distanceRoad.observe(viewLifecycleOwner){
            if(it==null) {
                binding.mypageBottom.tvRunDistanceCount.text = "0.00"
            }else{
                binding.mypageBottom.tvRunDistanceCount.text = it
            }
        }
    }


    private fun capture(){
        val road =GoogleMap.SnapshotReadyCallback {
            if (it != null) {
                viewModel.requestBitmap(it)
            }
        }
        mMap?.snapshot(road)
    }
   /* @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
    }
    private fun locationInit(){
        fusedLocationProviderClient= FusedLocationProviderClient(requireActivity())
        locationCallback=MyLocationCallBack()
        locationRequest= LocationRequest()
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=10000
        locationRequest.fastestInterval=5000
    }
    inner class MyLocationCallBack: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val location = locationResult?.lastLocation
            location?.run{
                val latLng=LatLng(latitude,longitude)
                //mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
                //mMap.addMarker(MarkerOptions().position(latLng).title("Changed Location"))
                viewModel.saveDistanceSecond(latitude,longitude)
                viewModel.requestDistance()
                polyLineOptions.add(latLng)
                mMap?.addPolyline(polyLineOptions)
            }
        }
    }


    private fun getLocation(){
        locationManager = (context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!
        var userLocation: Location = getLatLng()
        if(userLocation != null){
            latitude = userLocation.latitude
            longitude = userLocation.longitude
            Log.d("CheckCurrentLocation", "현재 내 위치 값: ${latitude}, ${longitude}")
            //viewModel.saveDistanceFirst(latitude,longitude)
            var mGeoCoder =  Geocoder(context, Locale.KOREAN)
            var mResultList: List<Address>? = null
            try{
                mResultList = mGeoCoder.getFromLocation(
                    latitude!!, longitude!!, 1
                )
            }catch(e: IOException){
                e.printStackTrace()
            }
            if(mResultList != null){
                Log.d("CheckCurrentLocation", mResultList[0].getAddressLine(0))
            }
        }
    }

    private fun getLatLng(): Location{
        var currentLatLng: Location? = null
        var hasFineLocationPermission = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION)
        var hasCoarseLocationPermission = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION)

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            val locatioNProvider = LocationManager.GPS_PROVIDER
            currentLatLng = locationManager?.getLastKnownLocation(locatioNProvider)
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), REQUIRED_PERMISSIONS[0])){
                Toast.makeText(requireContext(), "앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE_GPS)
            }else{
                ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE_GPS)
            }
            currentLatLng = getLatLng()
        }
        return currentLatLng!!
    }*/

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

        //운동 시작버튼 클릭이벤트
        binding.detailBottom.btnRunStart.setOnClickListener {
            detailBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            //getLocation()
            //locationInit()
            //addLocationListener()
            updateLocation()
            val latLng=LatLng(latitude,longitude)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
            //viewModel.requestDistance()
            //latitude1=latitude
            //longitude1=longitude
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.isDraggable = false
            start()
            mSteps = 0
            mStepsCount = 0
        }

        binding.mypageBottom.btnStartFinish.setOnClickListener {
            capture()

            //getLocation()
            //viewModel.saveDistanceSecond(latitude,longitude)
            //latitude2=latitude
            //longitude2=longitude
            //userDistance=DistanceManager.getDistance(latitude1,longitude1,latitude2,longitude2).toString()
            userDistance=viewModel.distanceRoad.value!!
            Log.d("asdf12344",userDistance.toString())
            //RunFinishFragment(userTime,userTimeSeconds,userDistance,userStep,roadName).show(parentFragmentManager, "run")
            RunFinishFragment(userTime,userTimeSeconds,userDistance,userStep,roadName).show(parentFragmentManager, "run")
            reset()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.mypageBottom.btnStartStop.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) pause() else start()
        }

        // 검색 버튼 클릭 시
        binding.ivHomeSearch.setOnClickListener {
            val searchBox = binding.etHomeSearch

            val searchText = searchBox.text.toString()
            val geocoder = Geocoder(context)
            var addresses: List<Address?>? = null
            try {
                addresses = geocoder.getFromLocationName(searchText, 3)
                if (addresses != null && !addresses.equals(" ")) {
                    search(addresses)
                }
            } catch (e: Exception) {
            }
            searchBox.setText("")
        }

        checkCategory()
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

    private fun search(addresses: List<Address>) {
        val address = addresses[0]
        val latLng = LatLng(address.latitude, address.longitude)
        val addressText = String.format(
            "%s, %s",
            if (address.maxAddressLineIndex > 0) address
                .getAddressLine(0) else " ", address.featureName
        )

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(addressText)

        mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        updateLocation()
        //
        googleMap.setOnMarkerClickListener(this)
        createMark()
        //mMap = googleMap

        //mMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
        //mMap?.moveCamera(CameraUpdateFactory.zoomTo(15f))

        /*  if (checkSelfPermission(
                  mainActivity,
                  Manifest.permission.ACCESS_FINE_LOCATION
              ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                  mainActivity,
                  Manifest.permission.ACCESS_COARSE_LOCATION
              ) != PackageManager.PERMISSION_GRANTED
          ) {

          }

          mMap?.isMyLocationEnabled = true
          //mMap?.moveCamera(CameraUpdateFactory.zoomTo(15f))

          createMark()

          googleMap.setOnMarkerClickListener(this)*/
    }

    // 카테고리 선택
    private fun checkCategory() {
        binding.btnHomeDiet.setOnClickListener {
            binding.btnHomeDiet.isSelected = binding.btnHomeDiet.isSelected != true
            if(binding.btnHomeDiet.isSelected) {
                recommendedMark()
            }else {
                createMark()
                if(binding.btnHomeStrength.isSelected|| binding.btnHomeMood.isSelected){
                    recommendedMark()
                }
            }
        }

        binding.btnHomeStrength.setOnClickListener {
            binding.btnHomeStrength.isSelected = binding.btnHomeStrength.isSelected != true
            if(binding.btnHomeStrength.isSelected) {
                recommendedMark()
            }else {
                createMark()
                if(binding.btnHomeDiet.isSelected|| binding.btnHomeMood.isSelected){
                    recommendedMark()
                }
            }
        }

        binding.btnHomeMood.setOnClickListener {
            binding.btnHomeMood.isSelected = binding.btnHomeMood.isSelected != true
            if(binding.btnHomeMood.isSelected) {
                recommendedMark()
            }else {
                createMark()
                if(binding.btnHomeStrength.isSelected|| binding.btnHomeDiet.isSelected){
                    recommendedMark()
                }
            }
        }

    }

    // 산책로 마커 생성
    private fun createMark(){

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {

                    var latitude = snapshot.child("COURS_SPOT_LA").value as Double
                    var longitude = snapshot.child("COURS_SPOT_LO").value as Double
                    var name = snapshot.child("WLK_COURS_NM").value as String

                    // custom marker
                    val bitmapdraw = requireContext().resources.getDrawable(
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

   /* override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        mView.onStart()
    }*/

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
/*

    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mView.onDestroy()
    }

*/

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

    override fun onMarkerClick(p0: Marker): Boolean {
        //getLocation()
        // 마커 클릭 후 변수에 위치 이름, 경/위도 저장
        roadName = p0.title.toString()
        roadPosition = p0.position

        viewModel.saveDistanceFirst(p0.position.latitude,p0.position.longitude)
        val latLng=LatLng(p0.position.latitude,p0.position.longitude)
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))


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
        //detailBottomSheetBehavior.isGestureInsetBottomIgnored = true
        detailBottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isDraggable = true
        }
        detailBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    detailBottomSheetBehavior.peekHeight = 430
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


    fun startProcess() {
        // SupportMapFragment를 가져와서 지도가 준비되면 알림을 받습니다.
        // val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mView = binding.map
        mView.getMapAsync(this)
    }

    /** 권한 요청*/
    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionGranted(requestCode)
        } else {
            val isAllPermissionsGranted = permissions.all {it->checkSelfPermission(requireContext(),it)== PackageManager.PERMISSION_GRANTED }
            if (isAllPermissionsGranted) {
                permissionGranted(requestCode)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), permissions, requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            permissionGranted(requestCode)
        } else {
            permissionDenied(requestCode)
        }
    }

    // 권한이 있는 경우 실행
    fun permissionGranted(requestCode: Int) {
        startProcess() // 권한이 있는 경우 구글 지도를준비하는 코드 실행
    }

    // 권한이 없는 경우 실행
    fun permissionDenied(requestCode: Int) {
        Toast.makeText(requireContext()
            , "권한 승인이 필요합니다."
            , Toast.LENGTH_LONG)
            .show()
    }



    // 위치 정보를 받아오는 역할
    @SuppressLint("MissingPermission") //requestLocationUpdates는 권한 처리가 필요한데 현재 코드에서는 확인 할 수 없음. 따라서 해당 코드를 체크하지 않아도 됨.
    fun updateLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for(location in it.locations) {
                        Log.d("Location", "${location.latitude} , ${location.longitude}")
                        latitude=location.latitude
                        longitude=location.longitude
                        setLastLocation(location)
                        viewModel.saveDistanceSecond(latitude,longitude)
                        if(viewModel.latitude1.value!=null){
                            viewModel.requestDistance()
                        }
                        val latLng=LatLng(latitude,longitude)
                        polyLineOptions.add(latLng)
                        mMap?.addPolyline(polyLineOptions)
                        //viewModel.requestDistance()
                       /* val location = locationResult?.lastLocation
                        location?.run{
                            val latLng=LatLng(latitude,longitude)
                            //mMap.addMarker(MarkerOptions().position(latLng).title("Changed Location"))
                            polyLineOptions.add(latLng)
                            mMap?.addPolyline(polyLineOptions)
                        }*/
                    }
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        //mMap?.moveCamera(CameraUpdateFactory.zoomTo(15f))

    }
    fun observer(){
        viewModel.distanceRoad.observe(viewLifecycleOwner){

        }
    }

    fun setLastLocation(lastLocation: Location) {
        val LATLNG = LatLng(lastLocation.latitude, lastLocation.longitude)
        val markerOptions = MarkerOptions()
            .position(LATLNG)
            .title("Here!")

        //mMap?.clear()

        if (isFirst == false) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LATLNG, 15f))
            isFirst=true
        }

        //mMap?.addMarker(markerOptions)
        //mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap?.isMyLocationEnabled = true
        //createMark()



    }


}