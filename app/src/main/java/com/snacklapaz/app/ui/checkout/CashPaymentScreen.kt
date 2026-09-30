package com.snacklapaz.app.ui.checkout

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTextField
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.SuccessGreenLight
import com.snacklapaz.app.ui.theme.White
import kotlin.math.max

@Composable
fun CashPaymentScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderConfirmed: (orderNumber: String, total: Double) -> Unit
) {
    val address = cartViewModel.draftAddress
    val total = cartViewModel.totalFor(address)
    var needsChange by remember { mutableStateOf(false) }
    var cashAmount by remember { mutableStateOf(cartViewModel.draftCashAmount) }
    val paidAmount = cashAmount.replace(",", ".").toDoubleOrNull()
    val change = max((paidAmount ?: 0.0) - total, 0.0)
    val isCashValid = !needsChange || (paidAmount != null && paidAmount >= total)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = "Dinheiro", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = White,
                border = BorderStroke(1.dp, OrangeLight),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(52.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AttachMoney,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Pagamento em dinheiro",
                                color = GrayDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            )
                            Text(
                                text = "Responda em poucos segundos.",
                                color = GrayMedium,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = OrangeLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Text(text = "Total do pedido", color = GrayDark, fontSize = 13.sp)
                            Text(
                                text = "Bs ${"%.2f".format(total)}",
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                            Text(
                                text = "O entregador verá a orientação de troco no pedido.",
                                color = GrayDark,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Vai precisar de troco?",
                        color = GrayDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Escolha uma resposta. Se precisar, informe com quanto vai pagar.",
                        color = GrayMedium,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 3.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CashChoiceCard(
                        title = "Não, vou pagar certinho",
                        subtitle = "O valor entregue será exatamente o total.",
                        icon = Icons.Filled.Check,
                        selected = !needsChange,
                        onClick = { needsChange = false }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CashChoiceCard(
                        title = "Sim, vou precisar de troco",
                        subtitle = "O entregador leva o troco calculado.",
                        icon = Icons.Filled.Calculate,
                        selected = needsChange,
                        onClick = { needsChange = true }
                    )

                    if (needsChange) {
                        Spacer(modifier = Modifier.height(16.dp))
                        SnackTextField(
                            value = cashAmount,
                            onValueChange = {
                                cashAmount = it.filter { char -> char.isDigit() || char == ',' || char == '.' }
                                cartViewModel.updateDraftCashAmount(cashAmount)
                            },
                            label = "Vou pagar com quanto?",
                            leadingIcon = Icons.Filled.AttachMoney,
                            keyboardType = KeyboardType.Decimal
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ChangeResultCard(
                        needsChange = needsChange,
                        isCashValid = isCashValid,
                        change = change
                    )
                }
            }

            if (cartViewModel.placeOrderError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = cartViewModel.placeOrderError.orEmpty(),
                    color = ErrorRed,
                    fontSize = 13.sp
                )
            }
        }

        Surface(color = White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                SnackPrimaryButton(
                    text = if (cartViewModel.isPlacingOrder) "Enviando..." else "Confirmar pedido em dinheiro",
                    enabled = isCashValid && !cartViewModel.isPlacingOrder,
                    onClick = {
                        val paymentDescription = if (needsChange) {
                            "Dinheiro - troco para Bs ${"%.2f".format(paidAmount ?: total)} (levar Bs ${"%.2f".format(change)})"
                        } else {
                            "Dinheiro - valor certinho"
                        }
                        cartViewModel.placeOrder(address, paymentDescription) { orderNumber ->
                            onOrderConfirmed(orderNumber, total)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CashChoiceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected) OrangeLight else White,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) OrangePrimary else GrayBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (selected) OrangePrimary else OrangeLight,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) White else OrangePrimary,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(text = title, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = subtitle, color = GrayMedium, fontSize = 12.sp, lineHeight = 16.sp)
            }
            if (selected) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = OrangePrimary)
            }
        }
    }
}

@Composable
private fun ChangeResultCard(
    needsChange: Boolean,
    isCashValid: Boolean,
    change: Double
) {
    val ready = !needsChange || isCashValid

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (ready) SuccessGreenLight else CreamBackground,
        border = BorderStroke(1.dp, if (ready) SuccessGreenLight else GrayBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(shape = CircleShape, color = White, modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = null,
                        tint = if (ready) SuccessGreen else OrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = when {
                        !needsChange -> "Tudo certo: sem troco"
                        isCashValid -> "Troco calculado"
                        else -> "Falta informar o valor"
                    },
                    color = GrayDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = when {
                        !needsChange -> "O entregador não precisa levar dinheiro extra."
                        isCashValid -> "O entregador deve levar Bs ${"%.2f".format(change)} de troco."
                        else -> "Digite um valor igual ou maior que o total do pedido."
                    },
                    color = GrayDark,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
