package com.snacklapaz.app.ui.admin.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.graphics.vector.ImageVector

data class AdminSection(
    val id: String,
    val title: String,
    val icon: ImageVector
)

val adminSections = listOf(
    AdminSection("products", "Produtos", Icons.Filled.ShoppingBag),
    AdminSection("categories", "Categorias", Icons.Filled.Category),
    AdminSection("orders", "Pedidos", Icons.Filled.ListAlt),
    AdminSection("customers", "Clientes", Icons.Filled.People),
    AdminSection("inventory", "Estoque", Icons.Filled.Warehouse),
    AdminSection("promotions", "Promoções", Icons.Filled.LocalOffer),
    AdminSection("reviews", "Avaliações", Icons.Filled.Star),
    AdminSection("reports", "Relatórios", Icons.Filled.Assessment),
    AdminSection("users", "Usuários", Icons.Filled.ManageAccounts),
    AdminSection("settings", "Configurações", Icons.Filled.Settings)
)