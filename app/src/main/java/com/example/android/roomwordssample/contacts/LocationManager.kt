package com.example.android.roomwordssample.contacts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object LocationManager {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private var userId:Long = -1

    fun saveUserWithLocation(user: User, context: Context, isUpdate:Boolean, userViewModel : UserViewModel){
        //Save record first
        GlobalScope.launch {
            if (isUpdate) {
                userViewModel.update(user)
                user.id?.let {
                    userId = it
                }
            } else {
                userId = userViewModel.insert(user)
            }
        }.invokeOnCompletion {
            UpdateLocation(userId,context,userViewModel)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun UpdateLocation(userId:Long,context: Context,userViewModel : UserViewModel){

        //Fetch the location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.lastLocation?.let {
                    currentLocation = it

                    GlobalScope.launch(Dispatchers.IO) {
                        userViewModel.updateLocation(userId, it.longitude, it.latitude)
                    }

                    val removeTask = mFusedLocationClient.removeLocationUpdates(locationCallback)
                    removeTask.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "Location Callback removed.")
                        } else {
                            Log.d("TAG", "Failed to remove Location Callback.")
                        }
                    }

                } ?: run {
                    Log.d("TAG", "Location information isn't available.")
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
                p0?.let {
                    if (!it.isLocationAvailable){
                        Log.d("TAG", "Failed to update location. Location is turned off")
                        val removeTask = mFusedLocationClient.removeLocationUpdates(locationCallback)
                        removeTask.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("TAG", "Location Callback removed.")
                            } else {
                                Log.d("TAG", "Failed to remove Location Callback.")
                            }
                        }
                    }
                }
            }
        }

        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }
}