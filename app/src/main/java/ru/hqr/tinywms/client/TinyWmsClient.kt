package ru.hqr.tinywms.client

import retrofit2.Call
import ru.hqr.tinywms.dto.client.Barcode
import ru.hqr.tinywms.dto.client.Stock
import ru.hqr.tinywms.dto.client.StockInfo
import ru.hqr.tinywms.dto.client.StockListInfo

class TinyWmsClient : TinyWmsApi {

    override fun findBarcode(barcode: String): Call<Barcode> {
        TODO("Not yet implemented")
    }

    override fun findStocks(client: Int): Call<List<Stock>> {
        TODO("Not yet implemented")
    }

    override fun findStockInfo(
        client: Int,
        barcode: String
    ): Call<List<StockListInfo>> {
        TODO("Not yet implemented")
    }

    override fun actualizeStockInfo(
        client: Int,
        addressId: String,
        stocks: List<StockInfo>
    ): Call<Void> {
        TODO("Not yet implemented")
    }
}