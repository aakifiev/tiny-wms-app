package ru.hqr.tinywms.conf

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL =
//    "http://77.66.184.87:10002"
//    "http://192.144.12.65:10001"
    "http://10.0.2.2:10001"

val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()