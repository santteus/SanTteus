package com.example.santteus.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel:ViewModel() {


    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val birth = MutableLiveData<String>()
    val sex = MutableLiveData<Boolean>()
    val kg = MutableLiveData<String>()

    
}