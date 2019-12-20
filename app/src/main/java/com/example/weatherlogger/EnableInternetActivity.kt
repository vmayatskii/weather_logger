package com.example.weatherlogger

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EnableInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.internet_disabled)

        // checks internet connection until internet is enabled
        Thread{
            while(true){
                if(isNetworkEnabled()){
                    this.finish()
                    startActivity(Intent(this,MainActivity::class.java))
                    break
                }
                Thread.sleep(500)
            }}.start()
    }

    private fun isNetworkEnabled():Boolean{
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}
