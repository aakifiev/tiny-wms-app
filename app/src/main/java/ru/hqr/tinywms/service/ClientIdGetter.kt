package ru.hqr.tinywms.service

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

fun getClientId(sharedPreferences: SharedPreferences): Int {
    return sharedPreferences.getInt("clientId", 0)
}

@Composable
fun getClientId(): Int {
    val context = LocalContext.current
    val sharedPreferences =
        context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getInt("clientId", 0)
}