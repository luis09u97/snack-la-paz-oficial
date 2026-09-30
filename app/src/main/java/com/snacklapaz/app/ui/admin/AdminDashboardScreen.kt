package com.snacklapaz.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snacklapaz.app.ui.admin.model.AdminSection
import com.snacklapaz.app.ui.admin.model.adminSections
import com.snacklapaz.app.ui.common.UiState
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.White

@Composable
fun AdminDashboardScreen(
    onBackClick: () -> Unit,
    onSectionClick: (AdminSection) -> Unit,
    adminViewModel: AdminViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = "Painel administrativo", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            when (val state = adminViewModel.dashboardState) {
                UiState.Loading -> Text(text = "Carregando dados...", color = GrayMedium)
                is UiState.Error -> EmptyState(
                    icon = Icons.Filled.Warning,
                    title = "Dashboard indisponível",
                    description = state.message,
                    actionLabel = "Tentar novamente",
                    onActionClick = { adminViewModel.loadDashboard() }
                )
                is UiState.Success -> StatCardsGrid(stats = state.data)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Gerenciar",
                style = MaterialTheme.typography.titleLarge,
                color = GrayDark
            )
            Spacer(modifier = Modifier.height(12.dp))

            SectionsGrid(sections = adminSections, onSectionClick = onSectionClick)
        }
    }
}

@Composable
private fun StatCardsGrid(stats: AdminDashboardStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                icon = Icons.Filled.AttachMoney,
                iconColor = SuccessGreen,
                label = "Vendas registradas",
                value = "Bs ${"%.2f".format(stats.salesTotal)}",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.ReceiptLong,
                iconColor = OrangePrimary,
                label = "Pedidos",
                value = stats.ordersCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                icon = Icons.Filled.People,
                iconColor = OrangePrimary,
                label = "Clientes",
                value = stats.customersCount.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Filled.Warning,
                iconColor = ErrorRed,
                label = "Estoque baixo",
                value = "${stats.lowStockCount} itens",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GrayDark)
            Text(text = label, fontSize = 12.sp, color = GrayMedium)
        }
    }
}

@Composable
private fun SectionsGrid(
    sections: List<AdminSection>,
    onSectionClick: (AdminSection) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(((sections.size / 3 + 1) * 100).dp)
    ) {
        items(sections) { section ->
            SectionCard(section = section, onClick = { onSectionClick(section) })
        }
    }
}

@Composable
private fun SectionCard(section: AdminSection, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = OrangeLight, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = section.icon, contentDescription = section.title, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = section.title,
                fontSize = 11.sp,
                color = GrayDark,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
