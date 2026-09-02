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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.White

@Composable
fun OrdersScreen(
    cartViewModel: CartViewModel,
    onTrackOrderClick: () -> Unit
) {
    val order = cartViewModel.lastOrder

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

        if (order == null) {
            EmptyState(
                icon = Icons.Outlined.Receipt,
                title = "Você ainda não fez pedidos",
                description = "Seus pedidos aparecerão aqui assim que você finalizar uma compra."
            )
            return
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = White,
            shadowElevation = 1.dp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clickable(onClick = onTrackOrderClick)
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
                            text = "Acompanhar pedido",
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
}