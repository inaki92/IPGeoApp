package com.inaki.ipgeoapp.rest

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

class NetworkConnection @Inject constructor(
    private val connectivityManager: ConnectivityManager
) {

    fun isNetworkAvailable(): Boolean =
        connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
}