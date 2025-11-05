package ru.hqr.tinywms.client

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface EmployeeWmsApi {

    @GET("/employees:login")
    fun findEmployee(@Header("user") user: String, @Header("pwd") pwd: String): Call<Int>
}