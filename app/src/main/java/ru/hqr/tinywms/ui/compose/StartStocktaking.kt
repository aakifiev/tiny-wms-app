package ru.hqr.tinywms.ui.compose

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.Barcode
import ru.hqr.tinywms.dto.client.StockInfoRequest
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import ru.hqr.tinywms.ui.component.InventoryDialog
import ru.hqr.tinywms.ui.component.MessageInventoryRow
import ru.hqr.tinywms.view.AddressListViewModel
import java.math.BigDecimal
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartStocktaking(
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    vm: AddressListViewModel,
    executor: Executor,
    navController: NavHostController
) {

    val context = LocalContext.current
    var clientId by remember { mutableStateOf(0) }
    val rememberCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit, block = {
        clientId = context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
            .getInt("clientId", 0)
        vm.getAddressList(clientId)
    })

    val addressMenuExpanded = remember {
        mutableStateOf(false)
    }

    var searchQuery by remember { mutableStateOf("") }

    val showDialog = remember {
        mutableStateOf(false)
    }

    val filteredItems = remember { mutableStateMapOf<Barcode, Short>() }
    InventoryDialog(
        showDialog = showDialog, executorHs = executor,
        filteredItemsResult = filteredItems
    )

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
                            "Старт инвентаризации",
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
                val addressFilteredItems = remember(searchQuery, vm.addresses) {
                    if (searchQuery.length < 3) {
                        vm.addresses
                    } else {
                        vm.addresses.filter { item ->
                            item.contains(searchQuery, ignoreCase = true)
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
                        addressFilteredItems.forEach {
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredItems.forEach { elem ->
                        item { MessageInventoryRow(elem.key.title, elem.value) }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = {
                            showDialog.value = true
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                    ) {
                        Text("Старт")
                    }
                }
                Button(
                    onClick = {
                        val stockInfoListRequest = filteredItems.map {
                            StockInfoRequest(
                                it.key.barcode,
                                BigDecimal(it.value.toInt()),
                                "UNIT"
                            )
                        }
                        rememberCoroutineScope.launch {
                            TinyWmsRest.retrofitService.addStockInfo(
                                clientId,
                                searchQuery,
                                stockInfoListRequest
                            ).enqueue(object : Callback<Unit> {
                                override fun onResponse(
                                    p0: Call<Unit>,
                                    p1: Response<Unit>
                                ) {
                                    Log.i("onResponse", p1.toString())
                                    navController.navigate("stockList")
                                }

                                override fun onFailure(p0: Call<Unit>, p1: Throwable) {
                                    Log.i("onFailure", "onFailure")
                                }

                            })
                        }

                    },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("Подтвердить")
                }
            }
        }
    }
}