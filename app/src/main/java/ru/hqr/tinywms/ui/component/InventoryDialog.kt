package ru.hqr.tinywms.ui.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.Barcode
import java.math.BigDecimal
import java.util.concurrent.Executor

@Composable
fun InventoryDialog(
    showDialog: MutableState<Boolean>,
    executorHs: Executor,
    filteredItemsResult: MutableMap<Barcode, Short>
) {
    val filteredItems = remember { mutableStateMapOf<Barcode, Short>() }
    if (!showDialog.value) return
    Dialog(
        onDismissRequest = {
            showDialog.value = false
            filteredItems.clear()
        }, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
            ) {
                Camera(executor = executorHs, onError = {
                    Log.i("scanBarcode", "There was as error with the camera: $it")
                }, onCatchBarcode = { barcode ->
                    val result = TinyWmsRest.retrofitService.findBarcode(barcode)
                    result.enqueue(object : Callback<Barcode?> {
                        override fun onResponse(
                            p0: Call<Barcode?>, p1: Response<Barcode?>
                        ) {
                            filteredItems.merge(
                                p1.body()!!,
                                1
                            ) { oldVal, newVal -> (newVal + oldVal).toShort() }
                            filteredItemsResult.putAll(filteredItems)
                            showDialog.value = false
                        }

                        override fun onFailure(p0: Call<Barcode?>, p1: Throwable) {
                            Log.i("", "onFailure: ")
                        }
                    })
                })

            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageInventoryRow(
    barcode: String, title: String, count: BigDecimal, customOnClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .height(90.dp)
            .fillMaxHeight()
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Штрихкод: $barcode", color = Color.Black, fontSize = 10.sp)
            Text(text = "Товар: ${title.take(20)}", color = Color.Black, fontSize = 15.sp)
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(text = "Всего на складе: $count шт.", color = Color.Black, fontSize = 10.sp)
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(90.dp)
                    .clickable(onClick = customOnClick)
                    .background(
                        color = Color.Yellow,
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Search Icon",
                    tint = Color.Black
                )
            }
        }
    }
}