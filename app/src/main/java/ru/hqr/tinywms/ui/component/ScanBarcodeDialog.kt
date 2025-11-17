package ru.hqr.tinywms.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.concurrent.Executor

@Composable
fun ScanBarcodeDialog(
    showDialog: MutableState<Boolean>,
    executorHs: Executor,
    barcodeResult: MutableState<String>
) {
    if (!showDialog.value) return
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
//            Spacer(modifier = Modifier.height(40.dp))
            Box(modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .height(360.dp)
                .clip(shape = RoundedCornerShape(6.dp))) {
                Camera(
                    executor = executorHs,
                    onError = {
                        Log.i("scanBarcode", "There was as error with the camera: $it")
                    },
                    onCatchBarcode = {
                        barcode -> barcodeResult.value = barcode
                        showDialog.value = false
                    })
            }
        }
    }
}