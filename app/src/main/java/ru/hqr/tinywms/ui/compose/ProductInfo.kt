package ru.hqr.tinywms.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.Barcode

@Composable
fun ProductInfo(name: String) {

    val response = remember {
        mutableStateOf("")
    }

    Scaffold { padding ->
        val result = TinyWmsRest.retrofitService.findBarcode(name)
        result!!.enqueue(object : Callback<Barcode?> {
            override fun onResponse(p0: Call<Barcode?>, p1: Response<Barcode?>) {
                response.value = (p1.body()?.barcode ?: "") + ":" + (p1.body()?.title ?: "")
            }

            override fun onFailure(p0: Call<Barcode?>, p1: Throwable) {
                response.value = "Error found is : " + p1.message
            }

        })
        Column(Modifier.padding(padding)) {
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                text = response.value)
        }
    }
}