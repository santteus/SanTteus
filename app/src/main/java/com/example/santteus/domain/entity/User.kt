package com.example.santteus.domain.entity

data class User(
    val email:String,
    val password:String,
    val birth:String,
    val sex:Boolean,
    val kg:Int,
    val profile:String,
    val data:Walk,
)
