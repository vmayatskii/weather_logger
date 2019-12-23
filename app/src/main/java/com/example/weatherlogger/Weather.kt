package com.example.weatherlogger

import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class Weather (
    var temperature : Double = 0.0, // temperature
    var pressure : Int = 0, // Atmospheric pressure - hPa
    var humidity : Int = 0, // in percent
    var windSpeed : Double = 0.0, // wind speed in meters per sec
    var date : String = "", // format: "dd.MM.yyyy HH:mm:ss" - date of current weather saving
    var city : String = "" // city of current saving
) {
companion object{

    private const val FILENAME = "weather_savings.json"

    // returns temperature in current location by longitude and latitude
    fun getWeather(long : Double, lat : Double, city : String = "", context : Context ) : Weather{
        val key = "e969637b42055b4ad51e22ecc8b7310c" // Api key from my account from openweathermap.org
        val url = URL("http://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&APPID=${key}&units=metric")
        val urlConnection = url.openConnection() as HttpURLConnection
        var response : String = ""

        try {
            Thread {
                // Responce will be returned as a Json string
                response = urlConnection.inputStream.bufferedReader().readText()
            }.start()

            // Reader from internet connection works in separate thread so
            // I use this loop to synchronize Threads.
            // I know, that`s not the best way to synchronize them, but
            // in this case it works properly
            while(response=="") {}

        }
        catch (e : Exception) {
            context.startActivity(Intent(context,SomethingWrongActivity::class.java))
        }
        finally {
                urlConnection.disconnect()
            }

        return responseToWeather(response,city)
    }

    // parses response from openweathermap to Weather object
    // Passing city as a param is required because of openweathermap does not shows name of the city properly,
    // so we are taking it from the phone, not from the weathermap
    private fun responseToWeather(json: String, city  : String): Weather {
        val jsonObj = JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1))
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        return Weather(city=city,
            temperature = jsonObj.getJSONObject("main").getDouble("temp"),
            pressure = jsonObj.getJSONObject("main").getInt("pressure"),
            humidity = jsonObj.getJSONObject("main").getInt("humidity"),
            windSpeed = jsonObj.getJSONObject("wind").getDouble("speed"),
            date = sdf.format(Date()).toString())
    }

    // saves current weather to overall file
    fun saveCurrentWeather(weather : Weather, context: Context){
        // getting list of weathers or empty list in case it is no saved weathers yet
        val weatherList : MutableList<Weather> = getWeatherSavingsList(context).asReversed()
        weatherList.add(weather)

        val newWeatherSavingsJsonString = listToJsonString(weatherList)

        // writing renewed list to file
        val file = File(context.filesDir, FILENAME)
        file.writeText(newWeatherSavingsJsonString, Charsets.UTF_8)
        Toast.makeText(context,"Saved!",Toast.LENGTH_SHORT).show()
    }

    // parses List<Weather> to a Json string
    fun listToJsonString(list: List<Weather>) : String{
        var jsonString: String = "{\"weathers\":["
        for (i in list) {
            jsonString += "{\"temperature\":${i.temperature}," +
                    "\"pressure\":${i.pressure}," +
                    "\"humidity\":${i.humidity}," +
                    "\"wind_speed\":${i.windSpeed}," +
                    "\"date\":\"${i.date}\"," +
                    "\"city\":\"${i.city}\"" +
                    "},"
        }

        // erasing last ","
        jsonString = jsonString.substring(0, jsonString.length - 1)

        jsonString += "]}"
        return jsonString
    }

    // Returns List with all saved weathers
    fun getWeatherSavingsList(context : Context): MutableList<Weather> {
        val file = File(context.filesDir, FILENAME)

        // if file does not exists, returning empty list
        if(!file.exists()){
            return mutableListOf<Weather>()
        }

        val weatherListAsAJsonString: String = file.readText(Charsets.UTF_8)

        return fromJsonToList(weatherListAsAJsonString).asReversed()
    }

    private fun fromJsonToList(json : String) : MutableList<Weather>{
        val weathersList: MutableList<Weather> = mutableListOf()

        val jsonObj = JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1))
        val weathers = jsonObj.getJSONArray("weathers")
        for (i in 0 until weathers.length()) {
            val tempWeather = Weather()
            with(tempWeather){
                temperature = weathers.getJSONObject(i).getDouble("temperature")
                pressure = weathers.getJSONObject(i).getInt("pressure")
                humidity = weathers.getJSONObject(i).getInt("humidity")
                windSpeed = weathers.getJSONObject(i).getDouble("wind_speed")
                date = weathers.getJSONObject(i).getString("date")
                city = weathers.getJSONObject(i).getString("city")
            }
            weathersList.add(tempWeather)
        }
        return weathersList
    }

    fun deleteWeather(index : Int, context: Context){
        // getting list of weathers or empty list in case it is no saved weathers yet
        var weatherList : MutableList<Weather> = getWeatherSavingsList(context)
        val file = File(context.filesDir, FILENAME)
        if(weatherList.size==1){
            file.delete()
            return
        }
        weatherList.removeAt(index)

        val newWeatherSavingsJsonString = listToJsonString(weatherList.asReversed())

        // writing renewed list to file
        file.writeText(newWeatherSavingsJsonString, Charsets.UTF_8)
    }

    fun getLastSavedCity(context : Context) : String{
        val weatherList = getWeatherSavingsList(context).asReversed()
        return if(weatherList.size==0) {
            "None yet"
        } else {
            weatherList[weatherList.size - 1].city
        }
    }

    fun getLastSavedTemperature(context : Context) : Double{
        val weatherList = getWeatherSavingsList(context).asReversed()
        return if(weatherList.size==0){
            0.0
        } else {
            weatherList[weatherList.size - 1].temperature
        }
    }

}
}
