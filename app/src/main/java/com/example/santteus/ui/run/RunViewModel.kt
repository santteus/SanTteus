package com.example.santteus.ui.run

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santteus.data.FirebaseService
import com.example.santteus.domain.entity.User
import kotlinx.coroutines.launch

class RunViewModel : ViewModel() {


    private val _userWalk = MutableLiveData<User.Walk>()
    val userWalk: LiveData<User.Walk> = _userWalk

    fun requestUserWalk(time: String, timeSeconds: Int, distance: String, step: Int) {
        viewModelScope.launch {
            _userWalk.postValue(FirebaseService.getUserWalk(time, timeSeconds, distance, step))
        }
    }
}