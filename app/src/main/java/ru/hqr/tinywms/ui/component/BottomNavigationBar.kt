package ru.hqr.tinywms.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ru.hqr.tinywms.data.BottomNavigation
import ru.hqr.tinywms.type.NavRoute

val bottomItems = listOf(
    BottomNavigation(
        title = "Home",
        icon = Icons.Rounded.Home,
        page = NavRoute.HOME
    ),

    BottomNavigation(
        title = "Stocks",
        icon = Icons.Rounded.ShoppingCart,
        page = NavRoute.STOCK_LIST
    ),

    BottomNavigation(
        title = "Address",
        icon = Icons.Rounded.ThumbUp,
        page = NavRoute.ADDRESS_LIST
    ),

    BottomNavigation(
        title = "Account",
        icon = Icons.Rounded.AccountCircle,
        page = NavRoute.ACCOUNT
    )
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    selectedDestination: MutableIntState
) {
    NavigationBar {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            bottomItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedDestination.value,
                    onClick = {
                        navController.navigate(item.page.name)
                        selectedDestination.value = index
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }
        }
    }
}