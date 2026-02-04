package ru.hqr.tinywms.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import ru.hqr.tinywms.dto.client.Stock
import java.math.BigDecimal

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

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredItems) { stock ->
            MessageInventoryRow(
                stock.barcode, stock.title, BigDecimal.ZERO,
                customOnClick = {onStockInfoClick(stock.barcode)}
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageRow(
    message: Stock, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = {}
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(0.dp)
                .width(width = 300.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Штрихкод: ${message.barcode}")
            Text("Наименование: ${message.title}")
        }
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Search Icon",
                tint = Color.White
            )
        }
    }
    Spacer(modifier = Modifier
        .padding(5.dp))
}