package ru.hqr.tinywms.ui.component

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.StockInfoRequest

@Composable
fun ActualizeDialog(
    showDialog: MutableState<Boolean>,
    addressId: MutableState<String> = mutableStateOf(""),
    barcode: MutableState<String> = mutableStateOf("")
) {
    val context = LocalContext.current
    var clientId by remember { mutableStateOf(0) }
    clientId = context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
        .getInt("clientId", 0)
    val rememberCoroutineScope = rememberCoroutineScope()
    if (!showDialog.value) return
    val quantity = remember {
        mutableStateOf("")
    }
    Dialog(
        onDismissRequest = {
            showDialog.value = false
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Актуализировать количество для  ${barcode.value} на адресе ${addressId.value}",
                    modifier = Modifier.padding(16.dp),
                )
                TextField(
                    value = quantity.value,
                    onValueChange = { newText ->
                        quantity.value = newText
                    },
                    label = { Text("Количество")}
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { showDialog.value = false },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Отменить")
                    }
                    TextButton(
                        onClick = {
                            val stockInfo = StockInfoRequest(
                                barcode = barcode.value,
                                quantity = quantity.value.toBigDecimal(),
                                measureUnit = "UNIT")
                            rememberCoroutineScope.launch {
                                TinyWmsRest.retrofitService.actualizeStockInfo(
                                    clientId,
                                    addressId.value,
                                    listOf(stockInfo)).enqueue(object : Callback<Unit> {
                                    override fun onResponse(
                                        p0: Call<Unit>,
                                        p1: Response<Unit>
                                    ) {
                                        Log.i("onResponse", p1.toString())
//                                        clientId.intValue = getClientId(sharedPreferences)
//                                        response.value = p1.body()!!
                                    }

                                    override fun onFailure(p0: Call<Unit>, p1: Throwable) {
                                        Log.i("onFailure", "onFailure")
                                    }

                                })
                            }

                            showDialog.value = false
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Подтвердить")
                    }
                }
            }
        }
    }
}