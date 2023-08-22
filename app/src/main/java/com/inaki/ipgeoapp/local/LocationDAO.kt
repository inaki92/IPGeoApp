package com.inaki.ipgeoapp.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewLocation(location: Location)

    @Query("SELECT * FROM location_table")
    suspend fun getLocations(): List<Location>

    @Query("SELECT * FROM location_table WHERE ipAddress LIKE :ipAddress")
    suspend fun getLocationByIpAddress(ipAddress: String): Location?

    @Delete
    suspend fun deleteLocation(location: Location)

}