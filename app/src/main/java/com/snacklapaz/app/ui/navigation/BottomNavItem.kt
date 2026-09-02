package com.snacklapaz.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.snacklapaz.app.ui.navigation.Routes

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.HOME,
        label = "Início",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Routes.SEARCH,
        label = "Buscar",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    BottomNavItem(
        route = Routes.CART,
        label = "Carrinho",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    ),
    BottomNavItem(
        route = Routes.ORDERS,
        label = "Pedidos",
        selectedIcon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Outlined.Receipt
    ),
    BottomNavItem(
        route = Routes.PROFILE,
        label = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)