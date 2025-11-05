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
import ru.hqr.tinywms.dto.client.StockInfo
import ru.hqr.tinywms.dto.client.StockListInfo

class StockInfoListViewModel : ViewModel() {

    private val _stockInfoList = mutableStateListOf<StockListInfo>()
    var errorMessage: String by mutableStateOf("")
    val stockInfoList: List<StockListInfo>
        get() = _stockInfoList


    fun getStockInfoListByAddressId(clientId: Int, addressId: String) {
        viewModelScope.launch {
            try {
                _stockInfoList.clear()
                val result =
                    TinyWmsRest.retrofitService.findStockInfoListByAddress(clientId, addressId)
                result.enqueue(object : Callback<List<StockInfo>?> {
                    override fun onResponse(
                        p0: Call<List<StockInfo>?>,
                        p1: Response<List<StockInfo>?>
                    ) {
                        p1.body()!!
                            .map {
                                StockListInfo(
                                    it.barcode,
                                    it.title,
                                    addressId,
                                    it.quantity,
                                    it.measureUnit
                                )
                            }
                            .forEach { _stockInfoList.add(it) }
                    }

                    override fun onFailure(
                        p0: Call<List<StockInfo>?>,
                        p1: Throwable
                    ) {
                        TODO("Not yet implemented")
                    }

                })
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    fun getStockInfoList(clientId: Int, barcode: String) {
        viewModelScope.launch {
            try {
                _stockInfoList.clear()
                val result = TinyWmsRest.retrofitService.findStockInfo(clientId, barcode)
                result.enqueue(object : Callback<List<StockListInfo>?> {
                    override fun onResponse(
                        p0: Call<List<StockListInfo>?>,
                        p1: Response<List<StockListInfo>?>
                    ) {
                        Log.i("onResponse", p1.toString())
//                response.value = (p1.body()?.barcode ?: "") + ":" + (p1.body()?.title ?: "")
                        _stockInfoList.addAll(p1.body()!!)
//                        response.value = p1.body()!!
                    }

                    override fun onFailure(p0: Call<List<StockListInfo>?>, p1: Throwable) {
                        Log.i("onFailure", "onFailure")
//                response.value = "Error found is : " + p1.message
                    }

                })
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

}