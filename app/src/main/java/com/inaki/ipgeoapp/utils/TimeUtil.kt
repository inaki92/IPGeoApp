package com.inaki.ipgeoapp.utils

object TimeUtil {

    fun isLocationOld(savedTimeStamp: Long): Boolean {
        val fiveAgo = System.currentTimeMillis() - FIVE_MINUTES
        return savedTimeStamp < fiveAgo
    }
}