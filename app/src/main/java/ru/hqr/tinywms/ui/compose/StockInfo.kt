package ru.hqr.tinywms.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.hqr.tinywms.ui.component.BottomNavigationBar
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import java.math.BigDecimal
import java.nio.file.WatchEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInfo(
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    navController: NavHostController,
    selectedDestination: MutableIntState
) {

    val scope = rememberCoroutineScope()

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
                    .padding(
                        20.dp
                    )
                    .fillMaxWidth()
            ) {
                commonInfo()
                Spacer(modifier = Modifier
                    .padding(top = 20.dp))
                infoByAddress(listOf(
                    InfoByAddress(
                    "1-1-1",
                        BigDecimal.valueOf(15)),
                    InfoByAddress(
                        "1-1-2",
                        BigDecimal.valueOf(11))
                ))
            }
        }
    }
}

@Composable
fun commonInfo() {
    Row(
        modifier = Modifier
//        .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text("штрихкод: ")
            Text("Наименование: ")
        }
        Column() {
            Text("<12>")
        }
    }
}

@Composable
fun infoByAddress(infoByAddressList: List<InfoByAddress>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("Список адресов")
        infoByAddressList.forEach { info ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Text(info.address)
                }
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "minus"
                            )
                        }
                        Text("${info.quantity}")
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "plus"
                            )
                        }
                    }
                }
            }
        }
    }
}

data class InfoByAddress(
    val address: String,
    val quantity: BigDecimal
)