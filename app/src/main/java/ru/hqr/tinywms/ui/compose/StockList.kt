package ru.hqr.tinywms.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.conf.TinyWmsRest
import ru.hqr.tinywms.dto.client.Stock
import ru.hqr.tinywms.service.getClientId
import ru.hqr.tinywms.ui.component.FilterableList
import ru.hqr.tinywms.ui.component.createCustomModalNavigationDrawer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockList(
    onStockInfoClick: (barcode: String) -> Unit,
    navigateBack: () -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope
) {

    val response = remember {
        mutableStateOf(emptyList<Stock>())
    }

    var searchQuery by remember { mutableStateOf("") }

//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()

    createCustomModalNavigationDrawer(drawerState, scope)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Navigation example",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show drawer") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    Log.i("click", drawerState.toString())
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }
            )
        }
    ) { padding ->

        val result = TinyWmsRest.retrofitService.findStocks(getClientId())
        result!!.enqueue(object : Callback<List<Stock>?> {
            override fun onResponse(p0: Call<List<Stock>?>, p1: Response<List<Stock>?>) {
                Log.i("onResponse", p1.toString())
//                response.value = (p1.body()?.barcode ?: "") + ":" + (p1.body()?.title ?: "")
                response.value = p1.body()!!
            }

            override fun onFailure(p0: Call<List<Stock>?>, p1: Throwable) {
                Log.i("onFailure", "onFailure")
//                response.value = "Error found is : " + p1.message
            }

        })
        Column(
            Modifier
                .padding(padding)) {
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
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )

            FilterableList(
                items = response.value,
                query = searchQuery,
                onStockInfoClick = onStockInfoClick
            )
        }

//        Column(
//            Modifier
//                .padding(padding)
//                .verticalScroll(rememberScrollState())) {
//            response.value.forEach {
//                stock -> MessageRow(stock, onClick = {onStockInfoClick(stock.barcode)})
//            }
//            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
//                text = response.value)
//        }
    }
}

@Composable
fun MessageRow(
    message: Stock, onClick: () -> Unit
) {
    Card (modifier = Modifier.padding(8.dp),
        onClick = onClick) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(message.barcode)
            Text(message.title)
        }
    }
}