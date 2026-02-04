package ru.hqr.tinywms.ui.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.hqr.tinywms.dto.client.StockListInfo
import ru.hqr.tinywms.type.NavRoute
import ru.hqr.tinywms.ui.component.BottomNavigationBar
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import ru.hqr.tinywms.ui.component.MessageInventoryRow
import ru.hqr.tinywms.view.StockInfoListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressInfo(
    addressId: String,
    drawerState: DrawerState,
    navController: NavHostController,
    selectedDestination: MutableIntState,
    vm: StockInfoListViewModel,
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var clientId by remember { mutableStateOf(0) }

    LaunchedEffect(Unit, block = {
        clientId = context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
            .getInt("clientId", 0)
        vm.getStockInfoListByAddressId(clientId, addressId)
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
                            "Информация об адресе",
                        )
                    },
                    navigationIcon = {
                        Row {
                            IconButton(onClick = { navController.popBackStack() }) {
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
                commonAddressInfo(addressId)
                Spacer(
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
                infoByAddress2(vm.stockInfoList,
                    navController
                )
            }
        }
    }
}

@Composable
fun commonAddressInfo(addressId: String) {
    Row(
        modifier = Modifier
            .background(color = Color.Red)
            .fillMaxWidth()
            .height(90.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            Text(text = "адрес: $addressId", color = Color.Black)
        }
    }
}

@Composable
fun infoByAddress2(infoByAddressList: List<StockListInfo>, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text("Список товаров")
        infoByAddressList.forEach { info ->
            MessageInventoryRow(
                barcode = info.barcode,
                title = info.title,
                count = info.quantity,
                customOnClick = { navController.navigate("${NavRoute.STOCK_INFO.name}/barcode=${info.barcode}") }
            )
            Spacer(modifier = Modifier.padding(top = 5.dp))
        }
    }
}