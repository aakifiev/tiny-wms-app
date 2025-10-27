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
                    label = { Text(text = "stock list", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("stockList")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Address list", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("addressList")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Add stock", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate("addStock")
                        scope.launch { drawerState.close() }
                    }
                )
            }
        },
        content = content
    )
}