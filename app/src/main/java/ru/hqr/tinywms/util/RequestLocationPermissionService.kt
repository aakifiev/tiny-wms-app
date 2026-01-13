package ru.hqr.tinywms.util

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.hqr.tinywms.MainActivity

fun MainActivity.requestLocationPermission() {
    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
//            this.shouldShowCamera.value = true;
            return
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) -> Log.i("identifier", "Show location permissions dialog")

        else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}