package com.example.santteus.ui.run

import android.graphics.Bitmap
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.santteus.data.FirebaseService
import com.example.santteus.domain.entity.User
import com.example.santteus.domain.entity.Walk
import com.example.santteus.util.DistanceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunViewModel : ViewModel() {

    private val repo=FirebaseService()

    private val _userWalk = MutableLiveData<Walk>()
    val userWalk: LiveData<Walk> = _userWalk

    private val _imageRoad = MutableLiveData<Bitmap>()
    val imageRoad: LiveData<Bitmap> = _imageRoad

    private val _distanceRoad = MutableLiveData<String>()
    val distanceRoad: LiveData<String> = _distanceRoad

    var latitude1= MutableLiveData<Double>()
    var longitude1= MutableLiveData<Double>()
    var latitude2= MutableLiveData<Double>()
    var longitude2= MutableLiveData<Double>()

    fun requestUserWalk(fragment:Fragment,time: String, timeSeconds: Int, distance: String, step: Int,name:String) {
        repo.getUserWalk(time, timeSeconds, distance, step,name)
        repo.userWalk.observe(fragment, Observer {
            _userWalk.postValue(it)
        })

    }


    fun requestSetUserWalk(walk:Walk){
        viewModelScope.launch(Dispatchers.IO) {
            repo.setUserWalk(walk)

        }
    }

    fun requestBitmap(image: Bitmap){
        _imageRoad.value=image
    }

    fun saveDistanceFirst(lati:Double,long:Double){
        latitude1.value=lati
        longitude1.value=long

    }

    fun saveDistanceSecond(lati:Double,long:Double){
        latitude2.value=lati
        longitude2.value=long
    }

    fun requestDistance(){

        _distanceRoad.value= String.format("%.2f", DistanceManager.getDistance(latitude1.value!!, longitude1.value!!, latitude2.value!!, longitude2.value!!))
        Log.d("aaaaaaa", _distanceRoad.value.toString())
    }
}