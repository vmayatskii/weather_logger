package com.example.weatherlogger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.*
import android.location.Geocoder
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import kotlinx.coroutines.*
import java.util.*

class LocationClient : AppCompatActivity() {

    var city : String = ""
    var lastLong : Double = 0.0
    var lastLat : Double = 0.0

    // variables used for getting location
    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@LocationClient)
        getLastLocation()
    }

    // Method sets location to mFusedLocationClient
    private fun getLastLocation() : Boolean{
        if (checkPermissions()) {
            if(isNetworkEnabled()) {
                if (isLocationEnabled()) {
                    mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                        var location: Location? = task.result
                        requestNewLocationData()
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            // using geocoder to get a city
                            val geocoder = Geocoder(this, Locale.getDefault())
                            val addresses =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            city = if (addresses[0].locality != null) {
                                addresses[0].locality
                            } else {
                                "Unknown"
                            }
                            lastLat = location.latitude
                            lastLong = location.longitude
                            sendValues()
                        }
                    }
                } else {
                    startActivity(Intent(this,EnableLocationActivity::class.java))
                }
            } else {
                startActivity(Intent(this,EnableInternetActivity::class.java))
            }
        } else {
            requestPermissions()
        }
        return true
    }

    private fun sendValues() {
        val data = Intent()
        data.putExtra("long", lastLong)
        data.putExtra("lat", lastLat)
        data.putExtra("city", city)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    // requesting current location
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
        }
    }

    // checks if some of location services is enabled
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    // checks if internet connection is enabled
    private fun isNetworkEnabled():Boolean{
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // cheks permissions
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // requests permission for using location services
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}
