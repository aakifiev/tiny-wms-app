package ru.hqr.tinywms.ui.compose

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.StockInfoRequest
import ru.hqr.tinywms.dto.client.StockListInfo
import ru.hqr.tinywms.ui.component.BottomNavigationBar
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import ru.hqr.tinywms.view.StockInfoListViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInfo(
    barcode: String,
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    navController: NavHostController,
    selectedDestination: MutableIntState,
    vm: StockInfoListViewModel
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var clientId by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf(barcode) }

    LaunchedEffect(Unit, block = {
        clientId = context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
            .getInt("clientId", 0)
        scope.launch {
            vm.getStockInfoList(clientId, barcode)
            delay(500)
            title = vm.stockInfoList.first().title
        }
    })

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
                            "Информация о товаре",
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
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                commonInfo(
                    barcode,
                    title
                    )
                Spacer(modifier = Modifier
                    .padding(top = 20.dp))
                infoByAddress(vm.stockInfoList)
            }
        }
    }
}

@Composable
fun commonInfo(
    barcode: String,
    title: String
) {
    Row(
        modifier = Modifier
            .background(color = Color.Red)
            .height(90.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(text = "штрихкод: $barcode", color = Color.Black)
            Text(text = "Товар: $title", color = Color.Black)
        }
        Column() {
            Text("<12>", color = Color.Black)
        }
    }
}

@Composable
fun infoByAddress(infoByAddressList: List<StockListInfo>) {
    val clientId = LocalContext.current.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
        .getInt("clientId", 0)

    Column(
        modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxWidth()
    ) {
        Text("Адреса с товаром")
        infoByAddressList.forEach { info ->
            MessageAddressRow(
                info.addressId,
                info.quantity,
                info.barcode,
                clientId
            )
            Spacer(modifier = Modifier.padding(top = 5.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageAddressRow(
    addressId: String,
    count: BigDecimal,
    barcode: String,
    clientId: Int
) {

    val isEdit = remember { mutableStateOf(true) }
    val editedCount = remember {
        mutableStateOf(count)
    }
    val rememberCoroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .height(90.dp)
            .fillMaxHeight()
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
                .fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = addressId, color = Color.Black, fontSize = 15.sp)
            if (isEdit.value) {
                Row() {
                    Text(text = "Товаров на адресе: ${editedCount.value} шт.", color = Color.Black, fontSize = 15.sp)
                    IconButton(
                        onClick = {
                            isEdit.value = false
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "edit",
                            tint = Color.Black
                        )
                    }
                }
            } else {
                Row() {
                    Text(text = "Товаров на адресе: ", color = Color.Black, fontSize = 15.sp)
                    IconButton(
                        onClick = {
                            if (BigDecimal.ZERO != editedCount.value) {
                                editedCount.value = editedCount.value.minus(BigDecimal.ONE)
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "minus",
                            tint = Color.Black
                        )
                    }
                    Text(text = "${editedCount.value}", color = Color.Black, fontSize = 15.sp)
                    IconButton(
                        onClick = {
                            editedCount.value = editedCount.value.plus(BigDecimal.ONE)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "plus",
                            tint = Color.Black
                        )
                    }
                    IconButton(
                        onClick = {
                            isEdit.value = true
                            val stockInfo = StockInfoRequest(
                                barcode = barcode,
                                quantity = editedCount.value,
                                measureUnit = "шт.")
                            rememberCoroutineScope.launch {
                                TinyWmsRest.retrofitService.actualizeStockInfo(
                                    clientId,
                                    addressId,
                                    listOf(stockInfo)).enqueue(object : Callback<Unit> {
                                    override fun onResponse(
                                        p0: Call<Unit>,
                                        p1: Response<Unit>
                                    ) {
                                        Log.i("onResponse", p1.toString())
                                    }

                                    override fun onFailure(p0: Call<Unit>, p1: Throwable) {
                                        Log.i("onFailure", "onFailure")
                                    }

                                })
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "done",
                            tint = Color.Black
                        )
                    }
                    Text(text = " шт.", color = Color.Black, fontSize = 15.sp)
                }
            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .width(90.dp)
//                .clickable(onClick = customOnClick)
                .background(color = Color.Yellow, shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "${editedCount.value} шт.", color = Color.Black)
                    }
            }
        }
    }
}