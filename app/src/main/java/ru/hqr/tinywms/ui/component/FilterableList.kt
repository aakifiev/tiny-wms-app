package ru.hqr.tinywms.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.hqr.tinywms.dto.client.Stock

@Composable
fun FilterableList(
    items: List<Stock>,
    query: String,
    onStockInfoClick: (barcode: String) -> Unit
) {
    val filteredItems = remember(query, items) {
        if (query.isEmpty()) {
            items
        } else {
            items.filter { item ->
                item.title.contains(query, ignoreCase = true) ||
                        item.barcode.contains(query, ignoreCase = true)
            }
        }
    }

    LazyColumn (
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredItems) { stock ->
            MessageRow(
                stock,
                onClick = { onStockInfoClick(stock.barcode) })
        }
    }
}

@Composable
fun MessageRow(
    message: Stock, onClick: () -> Unit
) {
    Card (modifier = Modifier.padding().fillMaxWidth(),
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