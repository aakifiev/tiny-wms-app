package ru.hqr.tinywms.client

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface EmployeeWmsApi {

    @GET("/employees:login")
    fun findEmployee(@Header("user") user: String, @Header("pwd") pwd: String): Call<Int>

    @POST("/employees")
    fun createEmployee(@Header("user") user: String, @Header("pwd") pwd: String): Call<Int>
}