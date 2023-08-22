package com.inaki.ipgeoapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.inaki.ipgeoapp.model.IPLocation

@Entity(tableName = "location_table")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ipAddress: String,
    val country: String,
    val city: String,
    val regionName: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val zipCode: String,
    val timeZone: String,
    val timeStamp: Long
)

fun IPLocation.mapToLocalLocation(): Location {
    return Location(
        ipAddress = this.query ?: "99999",
        country = this.country ?: "",
        city = this.city ?: "",
        regionName = this.regionName ?: "",
        countryCode = this.countryCode ?: "",
        latitude = this.lat ?: 9999999.9,
        longitude = this.lon ?: 9999999.9,
        zipCode = this.zip ?: "",
        timeZone = this.timezone ?: "",
        timeStamp = System.currentTimeMillis()
    )
}
