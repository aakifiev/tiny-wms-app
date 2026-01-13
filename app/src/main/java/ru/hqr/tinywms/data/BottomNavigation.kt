package ru.hqr.tinywms.data

import androidx.compose.ui.graphics.vector.ImageVector
import ru.hqr.tinywms.type.NavRoute

data class BottomNavigation(

    val title: String,
    val icon: ImageVector,
    val page: NavRoute
)
