package com.snacklapaz.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun SnackBottomBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    Surface(
        color = White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                BottomNavItemView(
                    item = item,
                    selected = selected,
                    onClick = { onItemClick(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) OrangePrimary else GrayMedium,
        label = "iconColor"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "iconScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                interactionSource = remember_ripple_free(),
                indication = null
            )
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier
                .scale(iconScale)
        )
        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = iconColor
        )
    }
}

// Interaction source simples sem efeito de ripple, pra manter a animação
// customizada (escala + cor) como único feedback visual do toque.
@Composable
private fun remember_ripple_free(): MutableInteractionSource {
    return androidx.compose.runtime.remember { MutableInteractionSource() }
}