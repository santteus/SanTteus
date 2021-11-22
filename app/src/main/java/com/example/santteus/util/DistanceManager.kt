package com.example.santteus.util

import java.lang.Math.*
import kotlin.math.pow

object DistanceManager {
    private const val R = 6372.8 * 1000

    /**
     * 두 좌표의 거리를 계산한다.
     *
     * @param lat1 위도1
     * @param lon1 경도1
     * @param lat2 위도2
     * @param lon2 경도2
     * @return 두 좌표의 거리(m)
     */
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double):Double {
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2).pow(2.0) + kotlin.math.sin(dLon / 2).pow(2.0) * kotlin.math.cos(
            toRadians(lat1)
        ) * kotlin.math.cos(toRadians(lat2))
        val c = 2 * kotlin.math.asin(sqrt(a))
        return (R * c)*0.001
    }
}