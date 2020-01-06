package com.example.weatherlogger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.widget.TextView
import android.view.View


class MainActivity : AppCompatActivity() {

    lateinit var city : String
    var lastLong : Double = 0.0
    var lastLat : Double = 0.0
    lateinit var currentWeather : Weather

    val requestCode = 322

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this,LocationClient::class.java)
        startActivityForResult(intent,requestCode)
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == requestCode){
            if(resultCode == Activity.RESULT_OK){
                lastLong = data!!.getDoubleExtra("long",0.0)
                lastLat = data!!.getDoubleExtra("lat",0.0)
                city = data.getStringExtra("city")?:"unknown"
                setFields()
            }
        }
    }

    // setting values to fields in main page
    private fun setFields(){
        currentWeather = Weather.getWeather(lastLong,lastLat,city, this)

        findViewById<TextView>(R.id.yourCurrentLocationCity).text = city
        findViewById<TextView>(R.id.currentWeatherTemperature).text = "${currentWeather.temperature.toInt().toString()}°C"
        findViewById<TextView>(R.id.lastCity).text = Weather.getLastSavedCity(this)
        findViewById<TextView>(R.id.lastTemperature).text = "${Weather.getLastSavedTemperature(this).toInt().toString()}°C"
    }

    fun onClick(view : View){
        when(view.id){
            R.id.showAllSavingsButton -> startActivity(Intent(this,SavingsActivity::class.java))
            R.id.saveWeatherButton -> {
                Weather.saveCurrentWeather(currentWeather,context=this)
                setFields()}
            }
        }
    }

