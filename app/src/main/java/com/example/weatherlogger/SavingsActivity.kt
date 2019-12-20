package com.example.weatherlogger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class SavingsActivity : AppCompatActivity() {

   private val listView: ListView by lazy { findViewById<ListView>(R.id.weatherList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings)

        val savingsList = Weather.getWeatherSavingsList(this)

        // Creating more user-friendly list to show on ListView
        val userFriendlyList =
            savingsList.map { "Date : ${it.date}\nCity : ${it.city}\nTemperature : ${it.temperature.toInt()}°C" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userFriendlyList)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position: Int, _ ->
            showMoreInfo(savingsList[position],position)
        }
    }

    private fun showMoreInfo(weather : Weather, indexOfElement : Int){
        val dialog = MoreInfoFragment()
        val bundle = Bundle()
        with(bundle) {
            // I use element`s index in array to remove weather if it`s needed
            putInt("index", indexOfElement)

            putDouble("temperature",weather.temperature)
            putInt("pressure",weather.pressure)
            putInt("humidity",weather.humidity)
            putDouble("wind_speed",weather.windSpeed)
            putString("date",weather.date)
            putString("city", weather.city)
        }
        dialog.arguments = bundle
        dialog.show(supportFragmentManager ,"MoreInfoFragment")
    }
}

