package com.inaki.ipgeoapp.utils

import android.net.InetAddresses
import android.os.Build
import android.util.Patterns
import retrofit2.Response

suspend fun <T> makeNetworkCall(
    isNetworkAvailable: () -> Boolean,
    request: suspend () -> Response<T>,
    success: suspend (State.SUCCESS<T>) -> Unit,
    error: suspend (State.ERROR) -> Unit
) {

    try {
        if (isNetworkAvailable()) {
            val response = request.invoke()
            if (response.isSuccessful) {
                response.body()?.let {
                    success(State.SUCCESS(it))
                } ?: throw NullBodyException()
            } else {
                throw FailResponseException(response.errorBody()?.string())
            }
        } else {
            throw NoNetworkAvailableException()
        }
    } catch (e: Exception) {
        error(State.ERROR(e))
    }
}

fun String.checkIPAddressValid(): Boolean {
    return if (this.isNotEmpty()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            InetAddresses.isNumericAddress(this)
        } else {
            Patterns.IP_ADDRESS.matcher(this).matches()
        }
    } else {
        false
    }
}