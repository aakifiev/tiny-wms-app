package ru.hqr.tinywms.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.hqr.tinywms.biometric.BiometricPreferences
import ru.hqr.tinywms.type.NavRoute
import ru.hqr.tinywms.ui.signin.SignInScreenViewModel

@Composable
fun CustomModalNavigationDrawer(
    drawerState: DrawerState, scope: CoroutineScope,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current as FragmentActivity
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
                        navController.navigate(NavRoute.STOCK_LIST.name)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Список адресов", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate(NavRoute.ADDRESS_LIST.name)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Добавить товар", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate(NavRoute.ADD_STOCK.name)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Добавить информацию о товаре", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate(NavRoute.ADD_BARCODE_INFO.name)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Инвентаризация", color = Color.Black) },
                    selected = false,
                    onClick = {
                        navController.navigate(NavRoute.START_STOCKTAKING.name)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Выход", color = Color.Black) },
                    selected = false,
                    onClick = {
                        val biometricPreferences = BiometricPreferences(context)
                        scope.launch {
                            biometricPreferences.resetPassword()
                            biometricPreferences.resetUserName()
                            biometricPreferences.setBiometricEnabled(false)
                            drawerState.close()
                            navController.navigate(NavRoute.SIGN_IN.name)
                        }
                    }
                )
            }
        },
        content = content
    )
}