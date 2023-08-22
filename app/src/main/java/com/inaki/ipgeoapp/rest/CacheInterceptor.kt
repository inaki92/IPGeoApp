package com.inaki.ipgeoapp.rest

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class CacheInterceptor @Inject constructor(
    private val networkConnection: NetworkConnection
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val rBuilder = chain.request().newBuilder()
        if (!networkConnection.isNetworkAvailable()) {
            rBuilder.cacheControl(CacheControl.FORCE_CACHE)
        }

        return chain.proceed(rBuilder.build())
    }
}