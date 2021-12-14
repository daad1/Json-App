package com.example.jsonapp

import retrofit2.http.GET
import retrofit2.Call
import java.util.*


interface APIInterface {

    @GET("eur.json")
    fun getCurrency(): Call<Currency>?
}