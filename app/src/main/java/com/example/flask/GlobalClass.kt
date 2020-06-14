package com.example.flask

import android.app.Application

class GlobalClass : Application() {

    private var host: String? = null
    fun getHost(): String? {
        return host
    }

    fun setHost(name: String?) {
        this.host = name
    }



}