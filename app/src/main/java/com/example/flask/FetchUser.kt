package com.example.flask

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_fetch_user.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class FetchUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetch_user)

        val globalclass: GlobalClass = applicationContext as GlobalClass
        val host = globalclass.getHost()
        val user = intent.extras?.get("username").toString()
        val loading = intent.extras?.get("loadingStrings").toString()
        userName.text = "@${user}"


//        Toast.makeText(applicationContext,"${host}/app",Toast.LENGTH_SHORT).show()
        val jsonObj = JSONObject()
        jsonObj.put("username",user)
        val mediatype = MediaType.parse("application/json; charset=utf-8")
        val send = Request.Builder()
            .url("${host}/app")
            .post(RequestBody.create(mediatype,jsonObj.toString()))
            .build()

        val client =OkHttpClient.Builder().readTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).build()
        CoroutineScope(IO).launch {
            client.newCall(send).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("My_Error",e.toString())

                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@FetchUser,"Server Not Responding", Toast.LENGTH_LONG).show()
                    }

                }

                override fun onResponse(call: Call, response: Response) {
                    var imageUrl = response?.body()?.string().toString()
                    if(imageUrl == "Not"){
                        runOnUiThread {

                            Toast.makeText(this@FetchUser,"${userName.text.toString()} does not exist", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@FetchUser, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    else{
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            image.visibility = View.VISIBLE
                            Log.i("My_Error",imageUrl)

                            imageUrl.replace(" ","")
                            val chk = imageUrl[0].toString()
                            Log.i("My_Error", chk)
                            if(chk == "b"){
                                imageUrl = imageUrl.substring(2,imageUrl.length-1)
                            }

                            Log.i("My_Error",imageUrl)
                            val imageBytes = Base64.decode(imageUrl, Base64.DEFAULT)
                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            val resized =
                                Bitmap.createScaledBitmap(decodedImage, 500, 500, true)
                            image.setImageBitmap(resized)
//                        Picasso.get().load(imageUrl).resize(500,500).into(image)
                        }
                    }

                }

            })
        }

    }
}