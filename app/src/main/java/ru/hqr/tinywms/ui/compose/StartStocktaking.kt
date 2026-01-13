package ru.hqr.tinywms.ui.compose

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.Barcode
import ru.hqr.tinywms.dto.client.StockInfoRequest
import ru.hqr.tinywms.type.NavRoute
import ru.hqr.tinywms.ui.component.BottomNavigationBar
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
    navController: NavHostController,
    selectedDestination: MutableIntState
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
            },
            bottomBar = {
                BottomNavigationBar(navController, selectedDestination)
            }
        ) { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
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
                Row(
                    modifier = Modifier
//                        .background(color = Color.Green)
                        .fillMaxHeight(0.8f),
                    verticalAlignment = Alignment.Top
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        filteredItems.forEach { elem ->
                            item { MessageInventoryRow(elem.key.title, elem.value) }
                        }
                    }
                }
                Row() {
                    Column() {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 16.dp),
                            onClick = { showDialog.value = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.Red,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color.Yellow,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 30.dp,
                                                bottomEnd = 30.dp
                                            )
                                        )
                                        .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Сканировать", fontSize = 20.sp, color = Color.Yellow)
                            }
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 16.dp),
                            onClick = {
                                val stockInfoListRequest = filteredItems.map {
                                    StockInfoRequest(
                                        it.key.barcode,
                                        BigDecimal(it.value.toInt()),
                                        "шт."
                                    )
                                }
                                rememberCoroutineScope.launch {
                                    TinyWmsRest.retrofitService.actualizeStockInfo(
                                        clientId,
                                        searchQuery,
                                        stockInfoListRequest
                                    ).enqueue(object : Callback<Unit> {
                                        override fun onResponse(
                                            p0: Call<Unit>,
                                            p1: Response<Unit>
                                        ) {
                                            Log.i("onResponse", p1.toString())
                                            navController.navigate(NavRoute.STOCK_LIST.name)
                                        }

                                        override fun onFailure(p0: Call<Unit>, p1: Throwable) {
                                            Log.i("onFailure", "onFailure")
                                        }

                                    })
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.Red,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color.Yellow,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 30.dp,
                                                bottomEnd = 30.dp
                                            )
                                        )
                                        .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Подтвердить", fontSize = 20.sp, color = Color.Yellow)
                            }
                        }
                    }
                }
            }
        }
    }
}