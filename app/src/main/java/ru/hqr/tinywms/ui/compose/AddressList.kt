package ru.hqr.tinywms.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.hqr.tinywms.dto.client.Stock
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer
import ru.hqr.tinywms.ui.component.FilterableAddressList
import ru.hqr.tinywms.view.AddressListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressList(
    onStockInfoClick: (barcode: String) -> Unit,
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    vm: AddressListViewModel,
    navController: NavHostController
) {

    LaunchedEffect(Unit, block = {
        vm.getAddressList(1)
    })

    var isRefreshing by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        scope.launch {
            vm.getAddressList(1)
            isRefreshing = false
        }
    }

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
                            "Address list",
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newQuery ->
                        searchQuery = newQuery
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = {
                        Text(
                            text = "Search...",
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Icon",
                                    tint = Color.Gray
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Clear Icon",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        focusedBorderColor = MaterialTheme.colorScheme.primary,
//                        unfocusedBorderColor = Color.Gray
//                    )
                )

                val state = rememberPullToRefreshState()

                PullToRefreshBox(
                    state = state,
                    onRefresh = onRefresh,
//                    onRefresh = { vm.getStockList(1) },
                    isRefreshing = isRefreshing,
//                    modifier = Modifier.padding(padding),
//                    indicator = {
//                        PullToRefreshDefaults.LoadingIndicator(
//                            state = state,
//                            isRefreshing = isRefreshing,
//                            modifier = Modifier.align(Alignment.TopCenter),
//                        )
//                    },
                ) {
                    FilterableAddressList(
                        items = vm.addresses,
                        query = searchQuery,
                        onStockInfoClick = onStockInfoClick
                    )
                }
            }
        }
    }
}