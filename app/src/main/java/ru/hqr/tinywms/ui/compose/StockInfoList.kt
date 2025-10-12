package ru.hqr.tinywms.ui.compose

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.StockInfo
import ru.hqr.tinywms.service.getClientId
import ru.hqr.tinywms.ui.component.createCustomModalNavigationDrawer

@Composable
fun StockInfoList(barcode: String, drawerState: DrawerState, scope: CoroutineScope) {

    val response = remember {
        mutableStateOf(emptyList<StockInfo>())
    }
    val clientId = remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        topBar = {
            Column(Modifier
                .background(color = Color.Green)
                .fillMaxWidth()) {
                Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    text = "clientId: ${clientId.value}", color = Color.Black)
                Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    text = barcode, color = Color.Black)
            }
        }
    ) { padding ->
        val context = LocalContext.current
        val sharedPreferences =
            context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
        val result = TinyWmsRest.retrofitService.findStockInfo(1, barcode)
        result!!.enqueue(object : Callback<List<StockInfo>?> {
            override fun onResponse(p0: Call<List<StockInfo>?>, p1: Response<List<StockInfo>?>) {
                clientId.value = getClientId(sharedPreferences)
                response.value = p1.body()!!
            }

            override fun onFailure(p0: Call<List<StockInfo>?>, p1: Throwable) {
                Log.i("onFailure", "onFailure")
//                response.value = "Error found is : " + p1.message
            }

        })
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())) {
            response.value.forEach {
                    stockInfo -> MessageRow(stockInfo)
            }
        }
        createCustomModalNavigationDrawer(drawerState, scope)
    }
}

@Composable
fun MessageRow(
    message: StockInfo
) {
    Card (modifier = Modifier.padding(8.dp)) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(message.barcode)
            Text(message.quantity.toString())
            Text(message.measureUnit)
        }
    }
}