package ru.hqr.tinywms.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.StockInfo
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import ru.hqr.tinywms.view.AddressListViewModel
import ru.hqr.tinywms.view.StockListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStock(
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    vm: AddressListViewModel,
    productListVM: StockListViewModel,
    navController: NavHostController
) {

    LaunchedEffect(Unit, block = {
        vm.getAddressList(1)
        productListVM.getStockList(1)
    })

    var searchQuery by remember { mutableStateOf("") }
    var barcodeSearchQuery by remember { mutableStateOf("") }

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
                            "Add stock",
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
                            IconButton(onClick = navigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
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
                    if (barcodeSearchQuery.length < 3) {
                        productListVM.stocks
                    } else {
                        productListVM.stocks.filter { item ->
                            item.barcode.contains(barcodeSearchQuery, ignoreCase = true) ||
                                    item.title.contains(barcodeSearchQuery, ignoreCase = true)
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = addressMenuExpanded.value,
                    onExpandedChange = {},
                ) {
                    TextField(
                        label = { Text("Select address") },
                        trailingIcon = {},
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            addressMenuExpanded.value = searchQuery.length > 2
                        },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
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
                    expanded = barcodeMenuExpanded.value,
                    onExpandedChange = {},
                ) {
                    TextField(
                        label = { Text("Select barcode") },
                        trailingIcon = {},
                        value = barcodeSearchQuery,
                        onValueChange = {
                            barcodeSearchQuery = it
                            barcodeMenuExpanded.value = barcodeSearchQuery.length > 2
                        },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
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
                                    barcodeSearchQuery = it.barcode
                                    barcodeMenuExpanded.value = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    label = { Text("Enter quantity") },
                    trailingIcon = {},
                    value = quantity.value,
                    onValueChange = {
                        quantity.value = it
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = {
                            val stockInfo = StockInfo(
                                barcodeSearchQuery,
                                quantity.value.toBigDecimal(),
                                "UNIT"
                            )
                            rememberCoroutineScope.launch {
                                TinyWmsRest.retrofitService.addStockInfo(
                                    1,
                                    searchQuery,
                                    listOf(stockInfo)
                                ).enqueue(object : Callback<Unit> {
                                    override fun onResponse(
                                        p0: Call<Unit>,
                                        p1: Response<Unit>
                                    ) {
                                        Log.i("onResponse", p1.toString())
//                                        clientId.intValue = getClientId(sharedPreferences)
//                                        response.value = p1.body()!!
                                    }

                                    override fun onFailure(p0: Call<Unit>, p1: Throwable) { Log.i("onFailure", "onFailure")
                                    }

                                })
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}