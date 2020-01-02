package com.example.weatherlogger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
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
import android.view.View
import java.util.*
import kotlin.coroutines.*


class MainActivity : AppCompatActivity() {

    lateinit var city : String
    var lastLong : Double = 0.0
    var lastLat : Double = 0.0
    lateinit var currentWeather : Weather

    // variables used for getting location
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

}

    override fun onResume() {
        super.onResume()
        Thread {
            getLastLocation()
        }.start()
    }

    // setting values to fields in main page
    private fun setFields(){
        currentWeather = Weather.getWeather(lastLong,lastLat,city, this)

        findViewById<TextView>(R.id.yourCurrentLocationCity).text = city
        findViewById<TextView>(R.id.currentWeatherTemperature).text = "${currentWeather.temperature.toInt().toString()}°C"
        findViewById<TextView>(R.id.lastCity).text = Weather.getLastSavedCity(this)
        findViewById<TextView>(R.id.lastTemperature).text = "${Weather.getLastSavedTemperature(this).toInt().toString()}°C"
    }

    // Method sets location to mFusedLocationClient and refreshing fields
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
                            setFields()
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

    fun onClick(view : View){
        when(view.id){
            R.id.showAllSavingsButton -> startActivity(Intent(this,SavingsActivity::class.java))
            R.id.saveWeatherButton -> {
                Weather.saveCurrentWeather(currentWeather,context=this)
                setFields()
            }
        }
    }

}
