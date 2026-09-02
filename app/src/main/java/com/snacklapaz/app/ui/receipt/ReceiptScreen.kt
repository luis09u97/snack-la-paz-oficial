package com.snacklapaz.app.ui.receipt

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.model.OrderSummary
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
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
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Nenhum recibo disponível no momento.",
                    color = GrayMedium
                )
            }
            return
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            ReceiptCard(order = order)
        }

        Surface(color = White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
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
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Cabeçalho
            Text(
                text = "Snack La Paz",
                color = OrangePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "Comprovante de pedido",
                color = GrayMedium,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = GrayBorder)
            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(label = "Pedido nº", value = order.orderNumber)
            InfoRow(label = "Data", value = formatDate(order.dateTimeMillis))
            InfoRow(label = "Cliente", value = order.address.fullName)
            InfoRow(label = "Telefone", value = order.address.phone)
            InfoRow(label = "Endereço", value = order.address.formatted())
            InfoRow(label = "Pagamento", value = order.paymentMethod)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = GrayBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Itens",
                fontWeight = FontWeight.SemiBold,
                color = GrayDark,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity}x ${item.name}",
                        color = GrayDark,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Bs ${"%.2f".format(item.unitPrice * item.quantity)}",
                        color = GrayDark,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = GrayBorder)
            Spacer(modifier = Modifier.height(12.dp))

            InfoRowValueOnly(label = "Subtotal", value = order.subtotal)
            InfoRowValueOnly(label = "Entrega", value = order.deliveryFee)
            if (order.discount > 0) {
                InfoRowValueOnly(label = "Desconto", value = -order.discount, color = OrangePrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = GrayDark)
                Text(
                    text = "Bs ${"%.2f".format(order.total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = OrangePrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = GrayBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SuccessGreen.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Confirmado",
                        color = SuccessGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Este recibo é apenas um comprovante do pedido e não uma nota fiscal.",
                color = GrayMedium,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
    ) {
        Text(
            text = "$label: ",
            color = GrayMedium,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(text = value, color = GrayDark, fontSize = 13.sp)
    }
}

@Composable
private fun InfoRowValueOnly(label: String, value: Double, color: androidx.compose.ui.graphics.Color = GrayDark) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = GrayMedium, fontSize = 13.sp)
        Text(text = "Bs ${"%.2f".format(value)}", color = color, fontSize = 13.sp)
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
    return sdf.format(Date(millis))
}