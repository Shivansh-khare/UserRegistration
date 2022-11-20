package com.example.android.roomwordssample.location


import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.android.roomwordssample.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class LocationForegroundService: Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"
    val PERMISSION_ID = 44
    var notCancled:Boolean = true
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun getLastLocation() : Location? {
        // check if permissions are given
        var loc: Location?=null
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (isLocationEnabled()) {

            // getting last
            // location from
            // FusedLocationClient
            // object
            mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(OnCompleteListener { task ->
                    val location: Location? = task.result
                    loc = location
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Toast.makeText(this,"you are currently at " + location.getLatitude().toString() + " and " + location.getLongitude().toString(),
                            Toast.LENGTH_SHORT).show()
                        try {
                            val fileout: FileOutputStream = openFileOutput("mytextfile.txt", MODE_PRIVATE)
                            val outputWriter = OutputStreamWriter(fileout)
                            outputWriter.append("you are currently at" + location.getLatitude().toString() + " and " + location.getLongitude().toString()+"\n")
                            outputWriter.close()
                            //display file saved message
                            Toast.makeText(
                                baseContext, "File saved successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        Log.d("myToast","you are currently at" + location.getLatitude().toString() + " and " + location.getLongitude().toString())
                    }
                })
            return loc
        } else {
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                .show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        return loc
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(5)
        mLocationRequest.setFastestInterval(0)
        mLocationRequest.setNumUpdates(1)

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
//            try {
//                val fileout: FileOutputStream = openFileOutput("mytextfile.txt", MODE_PRIVATE)
//                val outputWriter = OutputStreamWriter(fileout)
//                outputWriter.append("you are currently at" + mLastLocation.getLatitude().toString() + " and " + mLastLocation.getLongitude().toString()+"\n")
//                outputWriter.close()
//                //display file saved message
//                Toast.makeText(
//                    baseContext, "File saved successfully!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            Log.d("myToast","you are currently at" + mLastLocation.getLatitude().toString() + " and " + mLastLocation.getLongitude().toString()+"")

        }
    }




    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onCreate() {
        super.onCreate()
        notCancled=true
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        notCancled=true;
        val input = intent.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //do heavy work on a background thread
        Toast.makeText(this,"work running", Toast.LENGTH_SHORT).show();
        doInBg()
        //stopSelf();
        return START_NOT_STICKY
    }

    private fun doInBg() {
//        Toast.makeText(this,"work running", Toast.LENGTH_SHORT).show()
        Log.d("myToast","on work Started")
        Thread(Runnable {
            while(notCancled){
                getLastLocation();
                Thread.sleep(30*1000)
            }
        }).start()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        notCancled = false
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}