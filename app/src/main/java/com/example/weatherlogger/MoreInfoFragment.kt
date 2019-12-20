package com.example.weatherlogger

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class MoreInfoFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = this.arguments

        val view = inflater.inflate(R.layout.fragment_more_info, container, false)
        with(view){
            findViewById<TextView>(R.id.moreInfoCity).text = "City: ${bundle?.getString("city")}"
            findViewById<TextView>(R.id.moreInfoDate).text = "Date: ${bundle?.getString("date")}"
            findViewById<TextView>(R.id.moreInfoHumidity).text = "Humidity: ${bundle?.getInt("humidity").toString()}%"
            findViewById<TextView>(R.id.moreInfoPressure).text = "Pressure: ${bundle?.getInt("pressure").toString()}  hPa"
            findViewById<TextView>(R.id.moreInfoTemperature).text = "Temperature: ${bundle?.getDouble("temperature")?.toInt().toString()}°C"
            findViewById<TextView>(R.id.moreInfoWindSpeed).text = "Wind speed: ${bundle?.getDouble("wind_speed").toString()}m/sec"

            findViewById<Button>(R.id.delete_weather_button).setOnClickListener {
                // Confirmation dialog
                val dialog = AlertDialog.Builder(this.context)
                with(dialog){
                    setTitle("Delete")
                    setMessage("Are you sure?")
                    setPositiveButton("Delete"){_,_ ->
                        Weather.deleteWeather(bundle!!.getInt("index"),this.context)
                    activity?.finish()
                    startActivity(Intent(this@MoreInfoFragment.context,SavingsActivity::class.java))}
                    setNegativeButton("Cancel"){_,_ -> }
                    show()
                }

            }
        }

        return view
    }


}
