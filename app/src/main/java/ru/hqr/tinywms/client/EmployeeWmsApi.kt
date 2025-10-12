package ru.hqr.tinywms.client

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.Query
import ru.hqr.tinywms.dto.client.Barcode
import ru.hqr.tinywms.dto.client.EmployeeClientId
import ru.hqr.tinywms.dto.client.Stock
import ru.hqr.tinywms.dto.client.StockInfo

interface EmployeeWmsApi {

    @GET("/employees")
    fun findEmployee(@Query("user") user: String, @Query("pwd") pwd: String): Call<EmployeeClientId>
}