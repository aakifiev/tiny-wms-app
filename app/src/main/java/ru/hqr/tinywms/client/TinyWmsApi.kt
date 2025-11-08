package ru.hqr.tinywms.client

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import ru.hqr.tinywms.dto.client.AddressInfo
import ru.hqr.tinywms.dto.client.Barcode
import ru.hqr.tinywms.dto.client.Stock
import ru.hqr.tinywms.dto.client.StockInfo
import ru.hqr.tinywms.dto.client.StockInfoRequest
import ru.hqr.tinywms.dto.client.StockListInfo

interface TinyWmsApi {

    @GET("/barcodes/{barcode}")
    fun findBarcode(@Path("barcode") barcode: String): Call<Barcode>

    @POST("/barcodes")
    fun createBarcode(@Body barcode: Barcode): Call<Unit>

    @GET("/stocks/clients/{client}/products")
    fun findStocks(@Path("client") client: Int): Call<List<Stock>>

    @GET("/stocks/clients/{client}/barcodes/{barcode}")
    fun findStockInfo(
        @Path("client") client: Int,
        @Path("barcode") barcode: String
    ): Call<List<StockListInfo>>

    @GET("/stocks/clients/{client}/addresses")
    fun findAddressList(
        @Path("client") client: Int
    ): Call<List<AddressInfo>>

    @GET("/stocks/clients/{client}/addresses/{addressId}")
    fun findStockInfoListByAddress(
        @Path("client") client: Int,
        @Path("addressId") addressId: String,
    ): Call<List<StockInfo>>

    @POST("/stocks/clients/{client}/addresses/{addressId}/actualize")
    fun actualizeStockInfo(
        @Path("client") client: Int,
        @Path("addressId") addressId: String,
        @Body stocks: List<StockInfoRequest>
    ): Call<Unit>

    @POST("/stocks/clients/{client}/addresses/{addressId}/add")
    fun addStockInfo(
        @Path("client") client: Int,
        @Path("addressId") addressId: String,
        @Body stocks: List<StockInfoRequest>
    ): Call<Unit>
}