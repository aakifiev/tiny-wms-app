package ru.hqr.tinywms.client

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import ru.hqr.tinywms.dto.client.Barcode
import ru.hqr.tinywms.dto.client.Stock
import ru.hqr.tinywms.dto.client.StockInfo

interface TinyWmsApi {

    @GET("/barcodes/{barcode}")
    fun findBarcode(@Path("barcode") barcode: String): Call<Barcode>

    @GET("/stocks/clients/{client}/products")
    fun findStocks(@Path("client") client: Int): Call<List<Stock>>

    @GET("/stocks/clients/{client}/barcodes/{barcode}")
    fun findStockInfo(
        @Path("client") client: Int,
        @Path("barcode") barcode: String
    ): Call<List<StockInfo>>
}