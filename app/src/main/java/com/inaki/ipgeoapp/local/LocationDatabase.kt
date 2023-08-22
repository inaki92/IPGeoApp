package com.inaki.ipgeoapp.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Location::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun getLocationDao(): LocationDAO
}