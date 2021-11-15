package com.example.santteus.domain.entity

data class User(
    val email:String,
    val password:String,
    val birth:String,
    val sex:Boolean,
    val kg:Int,
    val profile:String,
    val data:Walk

){
    data class Walk(
        val time:String,
        val distance:String,
        val age:Int,
        val step:Int,
        val cal:Int,
        val top:Int,
    )
}
