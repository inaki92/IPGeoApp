package com.inaki.ipgeoapp.rest

import com.inaki.ipgeoapp.model.IPLocation
import retrofit2.Response
import javax.inject.Inject

interface RemoteLocationRepository {
    suspend fun retrieveLocation(ipAddress: String): Response<IPLocation>
}

class RemoteLocationRepositoryImpl @Inject constructor(
    private val api: IPService
) : RemoteLocationRepository {
    override suspend fun retrieveLocation(ipAddress: String): Response<IPLocation> =
        api.getLocation(ipAddress)

}