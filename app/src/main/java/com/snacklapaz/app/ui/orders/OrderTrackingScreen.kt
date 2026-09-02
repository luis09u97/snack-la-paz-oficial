package com.snacklapaz.app.ui.orders

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.model.OrderSummary
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.orders.model.OrderStatus
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.OrderStatusColors
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun OrderTrackingScreen(
    order: OrderSummary?,
    onBackClick: () -> Unit
) {
    // Simulação local do avanço de status. Quando o Supabase entrar,
    // isso vira uma escuta em tempo real da tabela de pedidos — o resto
    // da tela (a timeline) continua igual.
    var currentStatus by remember { mutableStateOf(OrderStatus.RECEIVED) }

    LaunchedEffect(order?.orderNumber) {
        val steps = OrderStatus.entries
        for (index in steps.indices) {
            currentStatus = steps[index]
            if (index < steps.lastIndex) delay(2500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = "Acompanhar pedido", onBackClick = onBackClick)

        if (order == null) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = "Nenhum pedido em andamento.", color = GrayMedium)
            }
            return
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Pedido nº ${order.orderNumber}",
                style = MaterialTheme.typography.titleLarge,
                color = GrayDark,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bs ${"%.2f".format(order.total)}",
                color = OrangePrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            OrderStatus.entries.forEachIndexed { index, status ->
                TimelineStep(
                    status = status,
                    state = when {
                        status.ordinal < currentStatus.ordinal -> StepState.COMPLETED
                        status.ordinal == currentStatus.ordinal -> StepState.CURRENT
                        else -> StepState.PENDING
                    },
                    isLast = index == OrderStatus.entries.lastIndex
                )
            }
        }
    }
}

private enum class StepState { COMPLETED, CURRENT, PENDING }

@Composable
private fun TimelineStep(
    status: OrderStatus,
    state: StepState,
    isLast: Boolean
) {
    val circleColor by animateColorAsState(
        targetValue = when (state) {
            StepState.COMPLETED -> OrderStatusColors.completed
            StepState.CURRENT -> OrderStatusColors.current
            StepState.PENDING -> OrderStatusColors.pending
        },
        animationSpec = tween(400),
        label = "circleColor"
    )
    val lineColor by animateColorAsState(
        targetValue = if (state == StepState.COMPLETED) OrderStatusColors.completed else GrayBorder,
        animationSpec = tween(400),
        label = "lineColor"
    )
    val textColor = when (state) {
        StepState.COMPLETED -> GrayDark
        StepState.CURRENT -> OrangePrimary
        StepState.PENDING -> GrayMedium
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = circleColor,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (state == StepState.COMPLETED) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(40.dp)
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 24.dp)) {
            Text(
                text = status.label,
                color = textColor,
                fontWeight = if (state == StepState.CURRENT) FontWeight.Bold else FontWeight.Medium,
                fontSize = 15.sp
            )
            if (state == StepState.CURRENT) {
                Text(
                    text = "Em andamento...",
                    color = GrayMedium,
                    fontSize = 12.sp
                )
            }
        }
    }
}