package com.example.weatherlogger

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun jsonCreatingTest(){
        val weatherList = listOf<Weather>(Weather(2.5,1000,36,10.3,"15.02.2005","London"), Weather(-8.1,1004,10,30.5,"13.1.2019","Riga"))
        val json = Weather.listToJsonString(weatherList)
        val expected = "{\"weathers\":[{\"temperature\":2.5,\"pressure\":1000,\"humidity\":36,\"wind_speed\":10.3,\"date\":\"15.02.2005\",\"city\":\"London\"},{\"temperature\":-8.1,\"pressure\":1004,\"humidity\":10,\"wind_speed\":30.5,\"date\":\"13.1.2019\",\"city\":\"Riga\"}]}"
        assertEquals(expected,json)
    }
}
