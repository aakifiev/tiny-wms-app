package ru.hqr.tinywms.ui.compose

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.StockInfoRequest
import ru.hqr.tinywms.type.NavRoute
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import ru.hqr.tinywms.ui.component.ScanBarcodeDialog
import ru.hqr.tinywms.view.AddressListViewModel
import ru.hqr.tinywms.view.StockListViewModel
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStock(
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    vm: AddressListViewModel,
    productListVM: StockListViewModel,
    executor: Executor,
    navController: NavHostController
) {

    val context = LocalContext.current
    var clientId by remember { mutableStateOf(0) }
    LaunchedEffect(Unit, block = {
        clientId = context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
            .getInt("clientId", 0)
        vm.getAddressList(clientId)
        productListVM.getStockList(clientId)
    })

    var searchQuery by remember { mutableStateOf("") }
    var barcodeSearchQuery = remember { mutableStateOf("") }

    val quantity = remember {
        mutableStateOf("")
    }

    val addressMenuExpanded = remember {
        mutableStateOf(false)
    }

    val barcodeMenuExpanded = remember {
        mutableStateOf(false)
    }

    val rememberCoroutineScope = rememberCoroutineScope()

    val showDialog = remember {
        mutableStateOf(false)
    }
    ScanBarcodeDialog(showDialog = showDialog, executorHs = executor,
        barcodeResult = barcodeSearchQuery)

    CustomModalNavigationDrawer(
        drawerState = drawerState,
        scope = scope,
        navController = navController
    )
    {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Добавить товар",
                        )
                    },
                    navigationIcon = {
                        Row {
                            IconButton(onClick = navigateBack) {
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
        ) { padding ->
            Column(
                Modifier
                    .padding(padding)
            ) {

                val filteredItems = remember(searchQuery, vm.addresses) {
                    if (searchQuery.length < 3) {
                        vm.addresses
                    } else {
                        vm.addresses.filter { item ->
                            item.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }
                val productFilteredItems = remember(barcodeSearchQuery, productListVM.stocks) {
                    if (barcodeSearchQuery.value.length < 3) {
                        productListVM.stocks
                    } else {
                        productListVM.stocks.filter { item ->
                            item.barcode.contains(barcodeSearchQuery.value, ignoreCase = true) ||
                                    item.title.contains(barcodeSearchQuery.value, ignoreCase = true)
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    modifier = Modifier.padding(8.dp),
                    expanded = addressMenuExpanded.value,
                    onExpandedChange = {},
                ) {
                    OutlinedTextField(
                        label = { Text("Выбор адреса") },
                        trailingIcon = {},
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            addressMenuExpanded.value = searchQuery.length > 2
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = addressMenuExpanded.value,
                        onDismissRequest = { addressMenuExpanded.value = false },
                    ) {
                        filteredItems.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    Log.i("DropdownMenuItem", "DropdownMenuItemEdit")
                                    searchQuery = it
                                    addressMenuExpanded.value = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    modifier = Modifier.padding(8.dp),
                    expanded = barcodeMenuExpanded.value,
                    onExpandedChange = {},
                ) {
                    OutlinedTextField(
                        label = { Text("Выбрать товар") },
                        trailingIcon = {},
                        value = barcodeSearchQuery.value,
                        onValueChange = {
                            barcodeSearchQuery.value = it
                            barcodeMenuExpanded.value = barcodeSearchQuery.value.length > 2
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            IconButton(
                                onClick = {
                                    showDialog.value = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Clear Icon",
                                    tint = Color.Gray
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = barcodeMenuExpanded.value,
                        onDismissRequest = { barcodeMenuExpanded.value = false },
                    ) {
                        productFilteredItems.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.barcode} : ${it.title}") },
                                onClick = {
                                    Log.i("DropdownMenuItem", "DropdownMenuItemEdit")
                                    barcodeSearchQuery.value = it.barcode
                                    barcodeMenuExpanded.value = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    label = { Text("Укажите количество") },
                    trailingIcon = {},
                    value = quantity.value,
                    onValueChange = {
                        quantity.value = it
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = {
                            if (searchQuery.isBlank()) {
                                return@Button
                            }
                            if (barcodeSearchQuery.value.isBlank()) {
                                return@Button
                            }
                            if (quantity.value.isBlank()) {
                                return@Button
                            }
                            val stockInfo = StockInfoRequest(
                                barcodeSearchQuery.value,
                                quantity.value.toBigDecimal(),
                                "UNIT"
                            )
                            rememberCoroutineScope.launch {
                                TinyWmsRest.retrofitService.addStockInfo(
                                    clientId,
                                    searchQuery,
                                    listOf(stockInfo)
                                ).enqueue(object : Callback<Unit> {
                                    override fun onResponse(
                                        p0: Call<Unit>,
                                        p1: Response<Unit>
                                    ) {
                                        Log.i("onResponse", p1.toString())
                                        navController.navigate(NavRoute.STOCK_LIST.name)
                                    }

                                    override fun onFailure(p0: Call<Unit>, p: Throwable) {
                                        Log.i("onFailure", "onFailure")
                                    }

                                })
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                    ) {
                        Text("Сохранить")
                    }
                }
            }
        }
    }
}