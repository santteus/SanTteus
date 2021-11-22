package com.example.santteus.util

import android.location.Location
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

    fun DistanceByDegree( _latitude1:Double,  _longitude1:Double,  _latitude2:Double,  _longitude2:Double):Double{
        //double theta, dist;
        var theta:Double
        var dist:Double
        theta = _longitude1 - _longitude2;
        dist = Math.sin(DegreeToRadian(_latitude1)) * Math.sin(DegreeToRadian(_latitude2)) + Math.cos(DegreeToRadian(_latitude1))* Math.cos(DegreeToRadian(_latitude2)) * Math.cos(DegreeToRadian(theta));
        dist = Math.acos(dist);
        dist = RadianToDegree(dist);

        dist = dist * 60 * 1.1515;
        //dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        //dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    fun  DegreeToRadian( degree:Double):Double{
        return degree * Math.PI / 180.0;
    }

    //randian -> degree 변환
    fun  RadianToDegree( radian:Double):Double{
        return radian * 180 / Math.PI
    }

    fun getDistance2(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val distance: Double
        val locationA = Location("point A")
        locationA.setLatitude(lat1)
        locationA.setLongitude(lng1)
        val locationB = Location("point B")
        locationB.latitude = lat2
        locationB.longitude = lng2
        distance = locationA.distanceTo(locationB).toDouble()
        return distance* 1.609344
    }



}