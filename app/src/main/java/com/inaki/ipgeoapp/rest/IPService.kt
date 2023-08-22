package com.inaki.ipgeoapp.rest

import com.inaki.ipgeoapp.model.IPLocation
import com.inaki.ipgeoapp.utils.IP_PATH
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IPService {

    @GET(IP_PATH)
    suspend fun getLocation(
        @Path("ipAddress") ipAddress: String
    ): Response<IPLocation>
}