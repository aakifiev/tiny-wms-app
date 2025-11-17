package ru.hqr.tinywms.ui.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.concurrent.Executor

@Composable
fun InventoryDialog(
    showDialog: MutableState<Boolean>,
    executorHs: Executor,
    filteredItemsResult: MutableMap<String, Short>
) {
    val filteredItems = remember { mutableStateMapOf<String, Short>() }
    val isScan = remember { mutableStateOf(false) }
    val buttonScanText = remember { mutableStateOf("Сканировать") }
    if (!showDialog.value) return
    Dialog(
        onDismissRequest = {
            showDialog.value = false
            filteredItems.clear()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
//            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.9f)
                    .height(360.dp)
                    .clip(shape = RoundedCornerShape(6.dp))
            ) {
                Camera(
                    executor = executorHs,
                    onError = {
                        Log.i("scanBarcode", "There was as error with the camera: $it")
                    },
                    onCatchBarcode = { barcode ->
                        if (isScan.value) {
                            filteredItems.merge(barcode, 1)
                            { oldVal, newVal -> (newVal + oldVal).toShort() }
                            isScan.value = false
                            buttonScanText.value = "Сканировать"
                        }
//                        showDialog.value = false
                    })

            }
            Button(
                onClick = {
                    if (buttonScanText.value == "Сканировать") {
                        buttonScanText.value = "stop"
                        isScan.value = true
                    } else {
                        buttonScanText.value = "Сканировать"
                        isScan.value = false
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                Text(buttonScanText.value)
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredItems.forEach { elem ->
                    item { MessageInventoryRow(elem.key, elem.value) }
                }
            }
            Button(
                onClick = {
                    filteredItemsResult.putAll(filteredItems)
                    showDialog.value = false
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                Text("Готово")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageInventoryRow(
    barcode: String, count: Short
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Штрихкод: $barcode: $count шт.")
        HorizontalDivider()
    }
}