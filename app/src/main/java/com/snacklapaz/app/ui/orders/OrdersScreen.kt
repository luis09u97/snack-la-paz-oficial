package com.snacklapaz.app.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.model.OrderSummary
import com.snacklapaz.app.ui.common.UiState
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun OrdersScreen(
    ordersViewModel: OrdersViewModel,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onTrackOrderClick: (OrderSummary) -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) ordersViewModel.loadOrders()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Text(
            text = "Pedidos",
            style = MaterialTheme.typography.headlineMedium,
            color = GrayDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (!isLoggedIn) {
            EmptyState(
                icon = Icons.Outlined.Receipt,
                title = "Entre para ver seus pedidos",
                description = "Após o login, seus pedidos salvos no banco aparecerão aqui.",
                actionLabel = "Entrar",
                onActionClick = onLoginClick
            )
            return
        }

        when (val state = ordersViewModel.ordersState) {
            UiState.Loading -> Text(
                text = "Carregando pedidos...",
                color = GrayMedium,
                modifier = Modifier.padding(20.dp)
            )
            is UiState.Error -> EmptyState(
                icon = Icons.Filled.ErrorOutline,
                title = "Não foi possível carregar",
                description = state.message,
                actionLabel = "Tentar novamente",
                onActionClick = { ordersViewModel.loadOrders() }
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(
                        icon = Icons.Outlined.Receipt,
                        title = "Você ainda não fez pedidos",
                        description = "Seus pedidos aparecerão aqui assim que você finalizar uma compra."
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.data, key = { it.orderNumber }) { order ->
                            OrderCard(order = order, onClick = { onTrackOrderClick(order) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderSummary, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pedido nº ${order.orderNumber}",
                    fontWeight = FontWeight.Bold,
                    color = GrayDark,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bs ${"%.2f".format(order.total)} • ${order.items.sumOf { it.quantity }} itens",
                    color = GrayMedium,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OrangePrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = order.status.lowercase().replace("_", " "),
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalhes",
                tint = GrayMedium
            )
        }
    }
}
