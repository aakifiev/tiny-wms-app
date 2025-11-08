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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import ru.hqr.tinywms.dto.client.Barcode
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import ru.hqr.tinywms.ui.component.ScanBarcodeDialog
import ru.hqr.tinywms.view.AddressListViewModel
import ru.hqr.tinywms.view.StockListViewModel
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBarcodeInfo(
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

    var barcode = remember { mutableStateOf("") }

    val title = remember {
        mutableStateOf("")
    }

    val barcodeMenuExpanded = remember {
        mutableStateOf(false)
    }

    val rememberCoroutineScope = rememberCoroutineScope()

    val showDialog = remember {
        mutableStateOf(false)
    }
    ScanBarcodeDialog(showDialog = showDialog, executorHs = executor,
        barcodeResult = barcode)

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

                OutlinedTextField(
                    label = { Text("Выбрать товар") },
                    trailingIcon = {},
                    value = barcode.value,
                    onValueChange = {
                        barcode.value = it
                        barcodeMenuExpanded.value = barcode.value.length > 2
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
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    label = { Text("Укажите наименование товара") },
                    trailingIcon = {},
                    value = title.value,
                    onValueChange = {
                        title.value = it
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
                    TextButton(
                        onClick = {
                            if (barcode.value.isBlank()) {
                                return@TextButton
                            }
                            if (title.value.isBlank()) {
                                return@TextButton
                            }
                            val newBarcode = Barcode(
                                barcode.value,
                                title.value
                            )
                            rememberCoroutineScope.launch {
                                TinyWmsRest.retrofitService.createBarcode(
                                    newBarcode
                                ).enqueue(object : Callback<Unit> {
                                    override fun onResponse(
                                        p0: Call<Unit>,
                                        p1: Response<Unit>
                                    ) {
                                        Log.i("onResponse", p1.toString())
                                        navController.navigate("stockList")
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