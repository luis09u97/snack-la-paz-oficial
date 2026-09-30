package com.snacklapaz.app.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
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
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.OrangeSoft
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
                .padding(18.dp)
        ) {
            Text(
                text = "Visão geral da loja",
                color = GrayDark,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp
            )
            Text(
                text = "Dados reais do Supabase para acompanhar vendas, pedidos e operação.",
                color = GrayMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))

            PeriodFilter(
                selectedPeriod = adminViewModel.selectedPeriod,
                onPeriodClick = { adminViewModel.updatePeriod(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = adminViewModel.dashboardState) {
                UiState.Loading -> Text(text = "Carregando dados...", color = GrayMedium)
                is UiState.Error -> EmptyState(
                    icon = Icons.Filled.Warning,
                    title = "Dashboard indisponível",
                    description = state.message,
                    actionLabel = "Tentar novamente",
                    onActionClick = { adminViewModel.loadDashboard() }
                )
                is UiState.Success -> StatCardsGrid(
                    stats = state.data,
                    onSectionClick = onSectionClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Gerenciar",
                color = GrayDark,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "Toque em uma área para administrar a loja.",
                color = GrayMedium,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            SectionsGrid(sections = adminSections, onSectionClick = onSectionClick)
        }
    }
}

@Composable
private fun PeriodFilter(
    selectedPeriod: AdminPeriod,
    onPeriodClick: (AdminPeriod) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        AdminPeriod.entries.forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodClick(period) },
                label = { Text(period.label, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
private fun StatCardsGrid(
    stats: AdminDashboardStats,
    onSectionClick: (AdminSection) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                icon = Icons.Filled.AttachMoney,
                iconColor = SuccessGreen,
                label = "Vendas registradas",
                value = "Bs ${"%.2f".format(stats.salesTotal)}",
                modifier = Modifier.weight(1f),
                onClick = { adminSections.open("reports", onSectionClick) }
            )
            StatCard(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                iconColor = OrangePrimary,
                label = "Pedidos",
                value = stats.ordersCount.toString(),
                modifier = Modifier.weight(1f),
                onClick = { adminSections.open("orders", onSectionClick) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                icon = Icons.Filled.People,
                iconColor = OrangePrimary,
                label = "Clientes",
                value = stats.customersCount.toString(),
                modifier = Modifier.weight(1f),
                onClick = { adminSections.open("customers", onSectionClick) }
            )
            StatCard(
                icon = Icons.Filled.RestaurantMenu,
                iconColor = OrangePrimary,
                label = "Produtos",
                value = stats.productsCount.toString(),
                modifier = Modifier.weight(1f),
                onClick = { adminSections.open("products", onSectionClick) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                icon = Icons.Filled.Warning,
                iconColor = ErrorRed,
                label = "Estoque baixo",
                value = "${stats.lowStockCount} itens",
                modifier = Modifier.weight(1f),
                onClick = { adminSections.open("inventory", onSectionClick) }
            )
            StatCard(
                icon = Icons.Filled.Payments,
                iconColor = OrangePrimary,
                label = "Pagamentos pendentes",
                value = stats.pendingPaymentsCount.toString(),
                modifier = Modifier.weight(1f),
                onClick = { adminSections.open("payments", onSectionClick) }
            )
        }
        AdminAlerts(stats = stats, onSectionClick = onSectionClick)
    }
}

@Composable
private fun AdminAlerts(
    stats: AdminDashboardStats,
    onSectionClick: (AdminSection) -> Unit
) {
    val alerts = buildList {
        if (stats.lowStockCount > 0) add(AdminAlert("${stats.lowStockCount} produto(s) com estoque baixo.", "inventory"))
        if (stats.outOfStockCount > 0) add(AdminAlert("${stats.outOfStockCount} produto(s) sem estoque.", "inventory"))
        if (stats.pendingPaymentsCount > 0) add(AdminAlert("${stats.pendingPaymentsCount} pagamento(s) pendente(s).", "payments"))
        if (stats.preparingOrdersCount > 0) add(AdminAlert("${stats.preparingOrdersCount} pedido(s) em preparação.", "orders"))
    }
    if (alerts.isEmpty()) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = White,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.65f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Nenhum alerta operacional no momento.",
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(18.dp)
            )
        }
    } else {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = White,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.14f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Alertas administrativos", color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                alerts.forEach { alert ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { adminSections.open(alert.sectionId, onSectionClick) }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                        Text(text = alert.message, color = GrayDark, fontSize = 15.sp, modifier = Modifier.weight(1f).padding(start = 9.dp))
                        Text(text = "Abrir", color = OrangePrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.65f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = iconColor.copy(alpha = 0.12f), modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(26.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrayDark)
            Text(text = label, fontSize = 14.sp, color = GrayMedium, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun SectionsGrid(
    sections: List<AdminSection>,
    onSectionClick: (AdminSection) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        sections.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { section ->
                    SectionCard(
                        section = section,
                        onClick = { onSectionClick(section) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    section: AdminSection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.65f)),
        modifier = modifier
            .height(128.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = OrangeSoft, modifier = Modifier.size(56.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = section.icon, contentDescription = section.title, tint = OrangePrimary, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = section.title,
                fontSize = 15.sp,
                color = GrayDark,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = section.subtitle(),
                fontSize = 11.sp,
                color = GrayMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

private data class AdminAlert(
    val message: String,
    val sectionId: String
)

private fun List<AdminSection>.open(sectionId: String, onSectionClick: (AdminSection) -> Unit) {
    firstOrNull { it.id == sectionId }?.let(onSectionClick)
}

private fun AdminSection.subtitle(): String {
    return when (id) {
        "products" -> "Catálogo"
        "inventory" -> "Quantidades"
        "orders" -> "Status"
        "payments" -> "Controle"
        "customers" -> "Cadastro"
        "categories" -> "Cardápio"
        "promotions" -> "Campanhas"
        "reports" -> "Vendas"
        "activities" -> "Histórico"
        "settings" -> "Loja"
        else -> "Abrir"
    }
}
