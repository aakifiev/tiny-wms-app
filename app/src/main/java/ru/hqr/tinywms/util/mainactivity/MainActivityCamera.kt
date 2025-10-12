package ru.hqr.tinywms.util.mainactivity

import android.content.Context
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import ru.hqr.tinywms.MainActivity
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalGetImage
fun MainActivity.startCamera() {
    cameraExecutor = Executors.newSingleThreadExecutor()
}

fun MainActivity.stopCamera() {
    cameraExecutor.shutdown()
}

// MARK: SUSPEND

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}