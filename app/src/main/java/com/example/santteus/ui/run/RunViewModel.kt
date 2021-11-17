package com.example.santteus.ui.run

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.santteus.data.FirebaseService
import com.example.santteus.domain.entity.User
import com.example.santteus.domain.entity.Walk
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
}