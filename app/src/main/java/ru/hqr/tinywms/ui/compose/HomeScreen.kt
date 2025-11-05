package ru.hqr.tinywms.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.hqr.tinywms.ui.component.Camera
import java.util.concurrent.Executor

@Composable
fun HomeScreen(executorHs: Executor,
               popBackStack: () -> Unit,
               toProductInfo: (barcode: String) -> Unit) {

//    lateinit var cameraExecutor: ExecutorService;
    val identifier = "[HomeScreen]"

    Scaffold { padding ->
        Column (Modifier.padding(padding)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Box(modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .height(360.dp)
                .clip(shape = RoundedCornerShape(6.dp))) {
                Camera(
                    executor = executorHs,
                    onError = {
                        Log.i(identifier, "There was as error with the camera: $it")
                    },
                    onCatchBarcode = toProductInfo)
            }
        }
    }
}