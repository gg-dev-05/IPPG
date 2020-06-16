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
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val host = "http://192.168.43.37:5000"
    val client: OkHttpClient = OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS).connectTimeout(2, TimeUnit.SECONDS).build()
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

        val request = Request.Builder()
                .url(host)
                .build()


        client.newCall(request).enqueue(object: Callback{
            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call, e: IOException) {
                Log.i("My_Error","error from e ${e.toString()}")
                isConnected.postValue(false)
                runOnUiThread {
                    userWelcome.text = "Sorry Server Not Responding"
                    Toast.makeText(this@MainActivity,"Try Again Later", Toast.LENGTH_LONG).show()
                }


            }

            override fun onResponse(call: Call, response: Response) {
                if(!response.isSuccessful){
                    Log.i("My_Error","Connection Unsuccessful")
                    Toast.makeText(this@MainActivity,"Server not responding", Toast.LENGTH_LONG).show()
                    isConnected.postValue(false)
                }
                if(response.body()?.string().toString() == "connected"){
                    isConnected.postValue(true)
                    runOnUiThread {
                        userWelcome.visibility = View.INVISIBLE
                        userInput.visibility = View.VISIBLE
                        connectButton.visibility = View.GONE
                        sendRequest.visibility = View.VISIBLE
                    }



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
                else{
                    Log.i("My_Error","Connection Unsuccessful")
                    Toast.makeText(this@MainActivity,"Server not responding", Toast.LENGTH_LONG).show()
                    isConnected.postValue(false)
                }
            }

        })








    }

    fun sendDataToServer(view: View) {



        if(userInput.text.toString().trim().isEmpty()){
            Toast.makeText(this,"Enter a username",Toast.LENGTH_SHORT).show()
        }
        else{
            val intent = Intent(this,FetchUser::class.java)
            Log.i("My_Error","${userInput.text}, $loadingStrings, $dotsString")
            intent.putExtra("update",false)
            intent.putExtra("username",userInput.text)
            intent.putExtra("loadingStrings",loadingStrings)
            intent.putExtra("dotsString",dotsString)
            startActivity(intent)
        }
    }


}