package com.inaki.ipgeoapp.usecases

import android.util.Log
import com.inaki.ipgeoapp.local.LocalRepository
import com.inaki.ipgeoapp.local.Location
import com.inaki.ipgeoapp.local.mapToLocalLocation
import com.inaki.ipgeoapp.rest.NetworkConnection
import com.inaki.ipgeoapp.rest.RemoteLocationRepository
import com.inaki.ipgeoapp.utils.FailResponseException
import com.inaki.ipgeoapp.utils.NoNetworkAvailableException
import com.inaki.ipgeoapp.utils.State
import com.inaki.ipgeoapp.utils.TimeUtil
import com.inaki.ipgeoapp.utils.makeNetworkCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "LocationUseCase"

class LocationUseCase @Inject constructor(
    private val remote: RemoteLocationRepository,
    private val local: LocalRepository,
    private val networkConnection: NetworkConnection
) {

   operator fun invoke(ipAddress: String): Flow<State<Location>> = flow {

       try {
           val localLocation = local.getLocationById(ipAddress)
           if (localLocation != null && !TimeUtil.isLocationOld(localLocation.timeStamp)) {
               Log.d(
                   TAG,
                   "Location in DB: Data coming from database ${localLocation.ipAddress}, ${localLocation.timeStamp}"
               )
               emit(State.SUCCESS(localLocation))
           } else {
               makeNetworkCall(
                   isNetworkAvailable = { networkConnection.isNetworkAvailable() },
                   request = { remote.retrieveLocation(ipAddress) },
                   success = {
                       val localLoc =  it.data.mapToLocalLocation()
                       local.insertLocation(localLoc)
                       emit(State.SUCCESS(localLoc))
                   },
                   error = {
                       Log.e(TAG, "error network: ${it.error.localizedMessage}", it.error)
                       emit(it)
                   }
               )
           }
       } catch (e: Exception) {
           Log.e(TAG, "error exception: ${e.localizedMessage}", e)
           emit(State.ERROR(e))
       }
   }
}