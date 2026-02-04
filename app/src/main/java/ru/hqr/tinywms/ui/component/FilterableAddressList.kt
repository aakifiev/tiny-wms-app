package ru.hqr.tinywms.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FilterableAddressList(
    items: List<String>,
    query: String,
    onStockInfoClick: (addressId: String) -> Unit
) {
    val filteredItems = remember(query, items) {
        if (query.isEmpty()) {
            items
        } else {
            items.filter { item ->
                item.contains(query, ignoreCase = true)
            }
        }
    }

    LazyColumn (
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredItems) { addressId ->
            MessageAddressRow2(
                addressId,
                onClick = { onStockInfoClick(addressId) })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageAddressRow2(
    message: String, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(90.dp)
            .fillMaxHeight()
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.8f)
            ,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Адрес: $message", color = Color.Black)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .width(90.dp)
                .clickable(onClick = onClick)
                .background(color = Color.Yellow, shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Search Icon",
                    tint = Color.Black
                )
            }
        }
    }
}