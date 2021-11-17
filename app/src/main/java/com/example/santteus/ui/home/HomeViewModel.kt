package com.example.santteus.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _imageRoad = MutableLiveData<Bitmap>()
    val imageRoad: LiveData<Bitmap> = _imageRoad

    fun requestBitmap(image:Bitmap){
        _imageRoad.value=image
    }



}