package com.example.santteus.domain.entity

import com.google.firebase.database.PropertyName

data class Walk(
    @PropertyName("MESURE_AGRDE_FLAG_NM") val age:String="",
    @PropertyName("SEXDSTN_FLAG_CD")val sex:String="",
    @PropertyName("MESURE_NMPR_CO")val num:Int=0,
    @PropertyName("AVRG_PACE_CO")val step:Int=0,
    @PropertyName("WEEK_ODR")val week:String="",
    @PropertyName("DALY_ODR")val daily:Int=0
)
