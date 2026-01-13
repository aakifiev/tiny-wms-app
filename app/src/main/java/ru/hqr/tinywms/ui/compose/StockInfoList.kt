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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.StockInfoRequest
import ru.hqr.tinywms.dto.client.StockListInfo
import ru.hqr.tinywms.ui.component.ActualizeDialog
import ru.hqr.tinywms.ui.component.BottomNavigationBar
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import ru.hqr.tinywms.view.StockInfoListViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInfoList(
    barcode: String,
    byBarcode: Boolean,
    drawerState: DrawerState,
    scope: CoroutineScope,
    vm: StockInfoListViewModel,
    navController: NavHostController
) {

    val context = LocalContext.current
    var clientId by remember { mutableStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        isRefreshing = true
        scope.launch {
            if (byBarcode) {
                vm.getStockInfoList(clientId, barcode)
            } else {
                vm.getStockInfoListByAddressId(clientId, barcode)
            }
            isRefreshing = false
        }
    }

    val showDialog = remember {
        mutableStateOf(false)
    }

    val addressId = remember {
        mutableStateOf("")
    }

    val barcodeForActualize = remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit, block = {
        clientId = context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
            .getInt("clientId", 0)
        if (byBarcode) {
            vm.getStockInfoList(clientId, barcode)
        } else {
            vm.getStockInfoListByAddressId(clientId, barcode)
        }
    })


    ActualizeDialog(showDialog = showDialog, addressId = addressId, barcode = barcodeForActualize)

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
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Информация",
                            )
                        },
                        navigationIcon = {
                            Row {
                                IconButton(onClick = {
                                    navController.popBackStack()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                }
            },
//            bottomBar = {
//                BottomNavigationBar(navController, selectedDestination)
//            }
        ) { padding ->
            val state = rememberPullToRefreshState()

            PullToRefreshBox(
                state = state,
                onRefresh = onRefresh,
                isRefreshing = isRefreshing,
            ) {
                Column(
                    Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    vm.stockInfoList.forEach { stockInfo ->
                        MessageRow(context,stockInfo, showDialog, addressId, barcodeForActualize)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageRow(
    context: Context,
    message: StockListInfo,
    showDialog: MutableState<Boolean>,
    addressId: MutableState<String>,
    barcodeForActualize: MutableState<String>,
) {
    val current = LocalHapticFeedback.current
    val expanded = remember {
        mutableStateOf(false)
    }
    val newQuantity = remember {
        mutableStateOf(message.quantity)
    }
    val rememberCoroutineScope = rememberCoroutineScope()
    DropdownMenu(
        modifier = Modifier.padding(8.dp),
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            text = { Text("Актуализировать кол-во") },
            onClick = {
                Log.i("DropdownMenuItem", "DropdownMenuItemEdit")
                expanded.value = false
                addressId.value = message.addressId
                barcodeForActualize.value = message.barcode
                showDialog.value = true
            }
        )
        DropdownMenuItem(
            text = { Text("Добавить кол-во") },
            onClick = {
                Log.i("DropdownMenuItem", "DropdownMenuItemEdit")
                expanded.value = false
                addressId.value = message.addressId
                barcodeForActualize.value = message.barcode
                showDialog.value = true
            }
        )
        DropdownMenuItem(
            text = { Text("Убавить кол-во") },
            onClick = {
                Log.i("DropdownMenuItem", "DropdownMenuItemEdit")
                expanded.value = false
                addressId.value = message.addressId
                barcodeForActualize.value = message.barcode
                showDialog.value = true
            }
        )
    }
    Column(
        modifier = Modifier
            .padding(24.dp)
            .combinedClickable(
                onClick = {
                    Log.i("onClick", "onClick")
                },
                onLongClick = {
                    Log.i("onLongClick", "onLongClick")
                    current.performHapticFeedback(HapticFeedbackType.LongPress)
                    expanded.value = !expanded.value
                }
            )
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Адрес: ${message.addressId}")
        Text("Наименование: ${message.title}")
        Row {
            Text("Количество:")
            IconButton(onClick = {
                if (BigDecimal.ZERO != newQuantity.value) {
                    newQuantity.value = newQuantity.value.minus(BigDecimal.ONE)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Clear Icon",
                    tint = Color.Gray
                )
            }
            Text("${newQuantity.value}")
            IconButton(onClick = {
                newQuantity.value = newQuantity.value.plus(BigDecimal.ONE)
            }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Clear Icon",
                    tint = Color.Gray
                )
            }
            IconButton(onClick = {
                val clientId =
                    context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
                        .getInt("clientId", 0)

                val stockInfo = StockInfoRequest(
                    barcode = message.barcode,
                    quantity = newQuantity.value,
                    measureUnit = "шт.")
                rememberCoroutineScope.launch {
                    TinyWmsRest.retrofitService.actualizeStockInfo(
                        clientId,
                        message.addressId, listOf(stockInfo)
                    ).enqueue(object : Callback<Unit> {
                        override fun onResponse(
                            p0: Call<Unit>,
                            p1: Response<Unit>
                        ) {
                            Log.i("onResponse", p1.toString())
//                                        clientId.intValue = getClientId(sharedPreferences)
//                                        response.value = p1.body()!!
                        }

                        override fun onFailure(p0: Call<Unit>, p1: Throwable) {
                            Log.i("onFailure", "onFailure")
                        }

                    })
                }
            }) {
                Icon(
                    imageVector = Icons.Outlined.Done,
                    contentDescription = "Clear Icon",
                    tint = Color.Gray
                )
            }
        }

        HorizontalDivider()
    }
}