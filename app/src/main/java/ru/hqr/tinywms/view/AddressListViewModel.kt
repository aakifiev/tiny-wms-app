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
import ru.hqr.tinywms.dto.client.AddressInfo

class AddressListViewModel: ViewModel() {

    private val _addresses = mutableStateListOf<String>()
    var errorMessage: String by mutableStateOf("")
    val addresses: List<String>
        get() = _addresses

    fun getAddressList(clientId: Int) {
        viewModelScope.launch {
            try {
                _addresses.clear()
                val result = TinyWmsRest.retrofitService.findAddressList(clientId)
                result.enqueue(object : Callback<List<AddressInfo>?> {
                    override fun onResponse(p0: Call<List<AddressInfo>?>, p1: Response<List<AddressInfo>?>) {
                        Log.i("onResponse", p1.toString())
//                response.value = (p1.body()?.barcode ?: "") + ":" + (p1.body()?.title ?: "")
                        p1.body()!!.map { it.addressId }.forEach { _addresses.add(it) }
//                        response.value = p1.body()!!
                    }

                    override fun onFailure(p0: Call<List<AddressInfo>?>, p1: Throwable) {
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