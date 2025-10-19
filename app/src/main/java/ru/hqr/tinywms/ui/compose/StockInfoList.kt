package ru.hqr.tinywms.ui.compose

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.StockListInfo
import ru.hqr.tinywms.service.getClientId
import ru.hqr.tinywms.ui.component.ActualizeDialog
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInfoList(barcode: String,
                  drawerState: DrawerState,
                  scope: CoroutineScope,
                  navController: NavHostController) {

    val response = remember {
        mutableStateOf(emptyList<StockListInfo>())
    }
    val clientId = remember {
        mutableIntStateOf(0)
    }

    val showDialog = remember {
        mutableStateOf(false)
    }

    val addressId = remember {
        mutableStateOf("")
    }
//    val barcode = remember {
//        mutableStateOf("")
//    }


    ActualizeDialog(showDialog = showDialog, addressId = addressId, barcode = barcode)

    CustomModalNavigationDrawer(
        drawerState = drawerState,
        scope = scope,
        navController = navController,
    ) {
        Scaffold(
            topBar = {
                Column(
                    Modifier
                        .background(color = Color.Green)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                        text = "clientId: ${clientId.intValue}", color = Color.Black
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                        text = barcode, color = Color.Black
                    )
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Stock list",
                            )
                        },
                        navigationIcon = {
                            Row {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Menu"
                                    )
                                }
                                IconButton(onClick = {
                                    navController.popBackStack()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        }
                    )
                }
            }
        ) { padding ->
            val context = LocalContext.current
            val sharedPreferences =
                context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
            val result = TinyWmsRest.retrofitService.findStockInfo(1, barcode)
            result.enqueue(object : Callback<List<StockListInfo>?> {
                override fun onResponse(
                    p0: Call<List<StockListInfo>?>,
                    p1: Response<List<StockListInfo>?>
                ) {
                    Log.i("onResponse", p1.toString())
                    clientId.intValue = getClientId(sharedPreferences)
                    response.value = p1.body()!!
                }

                override fun onFailure(p0: Call<List<StockListInfo>?>, p1: Throwable) {
                    Log.i("onFailure", "onFailure")
//                response.value = "Error found is : " + p1.message
                }

            })
            Column(
                Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                response.value.forEach { stockInfo ->
                    MessageRow(stockInfo, showDialog, addressId)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageRow(
    message: StockListInfo,
    showDialog: MutableState<Boolean>,
    addressId: MutableState<String>,
) {
    val current = LocalHapticFeedback.current
    val expanded = remember {
        mutableStateOf(false)
    }

    Card(modifier = Modifier.padding(8.dp)
        .combinedClickable(
            onClick = {
                Log.i("onClick", "onClick")
            },
            onLongClick = {
                Log.i("onLongClick", "onLongClick")
                current.performHapticFeedback(HapticFeedbackType.LongPress)
                expanded.value = !expanded.value
            }
        )) {
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            DropdownMenuItem(
                text = { Text("Actualize") },
                onClick = {
                    Log.i("DropdownMenuItem", "DropdownMenuItemEdit")
                    expanded.value = false
                    addressId.value = message.addressId
                    showDialog.value = true
                }
            )
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(message.addressId)
            Text(message.barcode)
            Text(message.quantity.toString())
            Text(message.measureUnit)
        }
    }
}