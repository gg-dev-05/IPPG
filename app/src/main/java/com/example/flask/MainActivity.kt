package com.example.flask

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val host = "http://192.168.43.37:5000"
    val client = OkHttpClient()
    private var isConnected: MutableLiveData<Boolean> = MutableLiveData()
    var loadingStrings = ""
    var dotsString = ""
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    //TODO  Unable to handle multiple Requests

        
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


     fun connectToServer(view: View) {

        isConnected.postValue(false)
        CoroutineScope(IO).launch {
            val request = Request.Builder()
                    .url(host)
                    .build()


            client.newCall(request).enqueue(object: Callback{
                @SuppressLint("SetTextI18n")
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("My_Error","error from e ${e.toString()}")
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
                        CoroutineScope(IO).launch {
                            val request1 = Request.Builder()
                                .url("${host}/loading")
                                .build()

                            
                            client.newCall(request1).enqueue(object: Callback{
                                override fun onFailure(call: Call, e: IOException) {
                                    isConnected.postValue(false)
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    val loadingStr = response.body()?.string().toString()
                                    loadingStrings = loadingStr
                                    //Log.i("My_Error",loadingStrings)
                                }

                            })
                        }

                        CoroutineScope(IO).launch {
                            val request2 = Request.Builder()
                                .url("${host}/dots")
                                .build()


                            client.newCall(request2).enqueue(object: Callback{
                                override fun onFailure(call: Call, e: IOException) {
                                    isConnected.postValue(false)
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    val dotsStr = response.body()?.string().toString()
                                    dotsString = dotsStr
                                    //Log.i("My_Error",dotsString)
                                }

                            })
                        }


                    }
                    else{
                        Log.i("My_Error","Connection Unsuccessful")
                        isConnected.postValue(false)
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
            intent.putExtra("loadingStrings",loadingStrings)
            intent.putExtra("dotsString",dotsString)
            startActivity(intent)
        }
    }


}