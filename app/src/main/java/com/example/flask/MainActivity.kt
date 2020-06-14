package com.example.flask

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val host = "http://192.168.43.37:5000"

    private var isConnected: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val globalclass: GlobalClass = applicationContext as GlobalClass
        globalclass.setHost(host)

        isConnected.observe(this, Observer {
            newValue ->
            if(!newValue){
                status.text = "Disconnected"
            }
            else{
                status.text = "Connected"
            }
        })


    }

//    private fun setStatusBar() {
//        CoroutineScope(Main).launch {
//            while(true){
//                checkConnection()
//            }
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun checkConnection() {
//        CoroutineScope(Main).launch {
//            if(!isConnected){
//                status.text = "Disconnected"
//            }
//            else{
//                status.text = "Connected"
//            }
//        }
//    }

     fun connectToServer(view: View) {

        CoroutineScope(IO).launch {
            val request = Request.Builder()
                    .url(host)
                    .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("My_Error",e.toString())
                    isConnected.postValue(false)
                    userWelcome.text = "Sorry Server Not Responding"
                }

                override fun onResponse(call: Call, response: Response) {
                    if(response.body()?.string().toString() == "connected"){
                        isConnected.postValue(true)
                        CoroutineScope(Main).launch {
                            userWelcome.visibility = View.INVISIBLE
                            userInput.visibility = View.VISIBLE
                            connectButton.visibility = View.GONE
                            sendRequest.visibility = View.VISIBLE

                        }


                    }
                    else{
                        Log.i("My_Error","Connection Unsuccessful")
                    }
                }

            })
        }







    }

    fun sendDataToServer(view: View) {



        if(userInput.text.toString().trim().isEmpty()){
            Toast.makeText(this,"Enter a username",Toast.LENGTH_SHORT).show()
        }
        else{
            val intent = Intent(this,FetchUser::class.java)
            intent.putExtra("username",userInput.text)
            startActivity(intent)
        }
    }


}