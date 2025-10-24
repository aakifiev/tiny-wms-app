package ru.hqr.tinywms.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.Stock

class StockListViewModel: ViewModel() {

    private val _stocks = mutableStateListOf<Stock>()
    var errorMessage: String by mutableStateOf("")
    val stocks: List<Stock>
        get() = _stocks

    fun getStockList(clientId: Int) {
        viewModelScope.launch {
            try {
                _stocks.clear()
                val result = TinyWmsRest.retrofitService.findStocks(clientId)
                result.enqueue(object : Callback<List<Stock>?> {
                    override fun onResponse(p0: Call<List<Stock>?>, p1: Response<List<Stock>?>) {
                        Log.i("onResponse", p1.toString())
//                response.value = (p1.body()?.barcode ?: "") + ":" + (p1.body()?.title ?: "")
                        _stocks.addAll(p1.body()!!)
//                        response.value = p1.body()!!
                    }

                    override fun onFailure(p0: Call<List<Stock>?>, p1: Throwable) {
                        Log.i("onFailure", "onFailure")
//                response.value = "Error found is : " + p1.message
                    }

                })
//                _stocks.addAll(TinyWmsRest.retrofitService.findStocks(getClientId()))
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

}