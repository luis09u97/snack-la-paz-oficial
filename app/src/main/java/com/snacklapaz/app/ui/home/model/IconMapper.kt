package com.snacklapaz.app.ui.home.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * O schema final de categorias não tem coluna de ícone. Então inferimos um
 * ícone visual pelo nome da categoria, sem criar coluna extra no banco.
 */
fun iconFromName(name: String?): ImageVector = when (name) {
    null -> Icons.Filled.Restaurant
    else -> when {
        name.contains("bebida", ignoreCase = true) ||
                name.contains("refresco", ignoreCase = true) -> Icons.Filled.LocalDrink
        name.contains("lanche", ignoreCase = true) ||
                name.contains("salgado", ignoreCase = true) -> Icons.Filled.LunchDining
        name.contains("doce", ignoreCase = true) ||
                name.contains("chocolate", ignoreCase = true) -> Icons.Filled.Cake
        else -> Icons.Filled.Restaurant
    }
}
