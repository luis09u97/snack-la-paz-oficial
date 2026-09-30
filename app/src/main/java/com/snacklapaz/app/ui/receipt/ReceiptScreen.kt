package com.snacklapaz.app.ui.receipt

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.model.OrderSummary
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.SuccessGreenLight
import com.snacklapaz.app.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptScreen(
    order: OrderSummary?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = "Recibo", onBackClick = onBackClick)

        if (order == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Nenhum recibo disponível no momento.", color = GrayMedium)
            }
            return
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ReceiptCard(order = order)
        }

        Surface(color = White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                SnackPrimaryButton(
                    text = "Salvar recibo em PDF",
                    onClick = {
                        try {
                            val file = ReceiptPdfGenerator.generate(context, order)
                            ReceiptPdfGenerator.openPdf(context, file)
                            Toast.makeText(context, "Recibo salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Não foi possível salvar o PDF.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ReceiptCard(order: OrderSummary) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, OrangeLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(54.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "Snack La Paz",
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text(text = "Comprovante de pedido", color = GrayMedium, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            StatusAndOrderCard(order = order)
            Spacer(modifier = Modifier.height(14.dp))
            DeliveryCodeCard(code = order.deliverySecurityCode)
            Spacer(modifier = Modifier.height(14.dp))

            SectionCard(title = "Dados da entrega") {
                BigInfoRow(label = "Cliente", value = order.address.fullName)
                BigInfoRow(label = "Telefone", value = order.address.phone)
                BigInfoRow(label = "Endereço", value = order.address.formatted())
                BigInfoRow(label = "Pagamento", value = order.paymentMethod)
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionCard(title = "Itens do pedido") {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                color = GrayDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(text = "${item.quantity} unidade(s)", color = GrayMedium, fontSize = 13.sp)
                        }
                        Text(
                            text = "Bs ${"%.2f".format(item.unitPrice * item.quantity)}",
                            color = GrayDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            TotalCard(order = order)

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Este recibo é apenas um comprovante do pedido e não uma nota fiscal.",
                color = GrayMedium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatusAndOrderCard(order: OrderSummary) {
    Surface(shape = RoundedCornerShape(20.dp), color = SuccessGreenLight, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(30.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = "Pedido confirmado", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(
                    text = "Pedido nº ${order.orderNumber} • ${formatDate(order.dateTimeMillis)}",
                    color = GrayDark,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun DeliveryCodeCard(code: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CreamBackground,
        border = BorderStroke(1.dp, OrangeLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(46.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = "Código da entrega", color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = code,
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = 3.sp
                )
                Text(text = "Mostre somente quando receber o pedido.", color = GrayMedium, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(18.dp), color = CreamBackground, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun BigInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(text = label, color = GrayMedium, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = GrayDark, fontSize = 15.sp, lineHeight = 19.sp)
    }
}

@Composable
private fun TotalCard(order: OrderSummary) {
    Surface(shape = RoundedCornerShape(20.dp), color = OrangeLight, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            ValueLine(label = "Subtotal", value = order.subtotal)
            ValueLine(label = "Entrega", value = order.deliveryFee)
            if (order.discount > 0) {
                ValueLine(label = "Desconto", value = -order.discount)
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = OrangePrimary.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = GrayDark)
                Text(
                    text = "Bs ${"%.2f".format(order.total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = OrangePrimary
                )
            }
        }
    }
}

@Composable
private fun ValueLine(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = GrayDark, fontSize = 15.sp)
        Text(text = "Bs ${"%.2f".format(value)}", color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
    return sdf.format(Date(millis))
}
