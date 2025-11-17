package ru.hqr.tinywms.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CustomModalNavigationDrawer(
    drawerState: DrawerState, scope: CoroutineScope,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.LightGray,
                drawerContentColor = Color.Black,
            ) {
                Text("Menu", modifier = Modifier.padding(16.dp))
                HorizontalDivider(
                    color = Color.Black
                )
                NavigationDrawerItem(
                    label = { Text(text = "Сканировать товар", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("home")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Список товаров", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("stockList")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Список адресов", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("addressList")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Добавить товар", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("addStock")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Добавить информацию о товаре", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("addBarcodeInfo")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Инвентаризация", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("startStocktaking")
                        scope.launch { drawerState.close() }
                    }
                )
            }
        },
        content = content
    )
}