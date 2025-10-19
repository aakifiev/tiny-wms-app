package ru.hqr.tinywms.ui.component

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import ru.hqr.tinywms.R
import ru.hqr.tinywms.util.mainactivity.getCameraProvider
import java.util.concurrent.Executor

@Composable
fun Camera(
    executor: Executor,
    onError: (ImageCaptureException) -> Unit,
    onCatchBarcode: (barcode: String) -> Unit
) {
// MARK: VARIABLES
    val context = LocalContext.current
    val identifier = context.getString(R.string.camera_view_identifier)

    // MARK: CAMERA SETUP AND RENDER
    // 1
    val lensFacing = CameraSelector.LENS_FACING_BACK

    val lifecycleOwner = LocalLifecycleOwner.current
    // Setup Basic Camera
    val preview = Preview.Builder()
        .build()
    val previewView = remember { PreviewView(context) }
    // Image Capture Functionality
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    val imageAnalyzer = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(executor, LuminosityAnalyzer(onCatchBarcode))
        }

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    LaunchedEffect (lensFacing) {
//        ProcessCameraProvider.getInstance(context)
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalyzer
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
        previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
    }
    // 3
    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}
