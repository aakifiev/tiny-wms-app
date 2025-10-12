package ru.hqr.tinywms.ui.component

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.nio.ByteBuffer

class LuminosityAnalyzer(onCatchBarcode: (barcode: String) -> Unit) : ImageAnalysis.Analyzer {

    private val action = onCatchBarcode

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val client = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_EAN_13)
                .build()
            val scanner = BarcodeScanning.getClient()
            client.process(image)
                .addOnSuccessListener { visionText ->
                    if (visionText.text.isNotEmpty()) {
                        Log.i("Analyzer", "text:${visionText.text}")
                    }
                }
            scanner.process(image)
                .addOnSuccessListener { barcode ->
                    if (barcode.isNotEmpty()) {
                        val rawValue = barcode.get(0).rawValue
                        Log.i("Analyzer", "quantity:$rawValue")
                        rawValue.let {
                            action.invoke(it as String)
                        }
                    }
                }
        }

//        val buffer = image.planes[0].buffer
//        val data = buffer.toByteArray()
//        val pixels = data.map { it.toInt() and 0xFF }
//        val luma = pixels.average()
//
//        Log.i("Analyzer", "quantity:$luma")
//        Toast.makeText(LocalContext.current, luma, 10)

        imageProxy.close()
    }
}