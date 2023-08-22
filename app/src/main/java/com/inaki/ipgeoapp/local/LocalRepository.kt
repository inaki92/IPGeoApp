package com.inaki.ipgeoapp.local

import javax.inject.Inject

interface LocalRepository {
    suspend fun insertLocation(location: Location)
    suspend fun getAllLocations(): List<Location>
    suspend fun getLocationById(ipAddress: String): Location?
    suspend fun deleteLocation(location: Location)
}

class LocalRepositoryImpl @Inject constructor(
    private val dao: LocationDAO
) : LocalRepository {
    override suspend fun insertLocation(location: Location) {
        dao.insertNewLocation(location)
    }

    override suspend fun getAllLocations(): List<Location> {
        return dao.getLocations()
    }

    override suspend fun getLocationById(ipAddress: String): Location? {
        return dao.getLocationByIpAddress(ipAddress)
    }

    override suspend fun deleteLocation(location: Location) {
        dao.deleteLocation(location)
    }
}