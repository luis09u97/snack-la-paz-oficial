package com.snacklapaz.app.ui.checkout

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun PaymentScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onPixClick: () -> Unit,
    onCashClick: () -> Unit,
    onOrderConfirmed: (orderNumber: String, total: Double) -> Unit
) {
    val address = cartViewModel.draftAddress
    val total = cartViewModel.totalFor(address)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(
            title = "Pagamento",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            CheckoutStepCard()
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Escolha como quer pagar",
                color = GrayDark,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "Cada opção mostra exatamente o que precisa ser feito.",
                color = GrayMedium,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            PaymentOptionList(
                onPixClick = {
                    cartViewModel.updateDraftPaymentMethod("Pix")
                    onPixClick()
                },
                onCashClick = {
                    cartViewModel.updateDraftPaymentMethod("Dinheiro")
                    onCashClick()
                },
                onCardClick = {
                    cartViewModel.updateDraftPaymentMethod("Cartão")
                    cartViewModel.placeOrder(address, "Cartão na entrega") { orderNumber ->
                        onOrderConfirmed(orderNumber, total)
                    }
                },
                onBitcoinClick = {
                    cartViewModel.updateDraftPaymentMethod("Bitcoin")
                    cartViewModel.placeOrder(address, "Bitcoin") { orderNumber ->
                        onOrderConfirmed(orderNumber, total)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            DeliveryResumeCard(
                address = address.formatted(),
                total = total,
                deliveryFee = cartViewModel.deliveryFeeFor(address)
            )

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
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Toque em uma forma de pagamento para continuar.",
                    color = GrayMedium,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun CheckoutStepCard() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            StepDot(number = "1", checked = true)
            StepLine(active = true, modifier = Modifier.weight(1f))
            StepDot(number = "2", checked = false)
            StepLine(active = false, modifier = Modifier.weight(1f))
            StepDot(number = "3", checked = false)
        }
    }
}

@Composable
private fun StepDot(number: String, checked: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (checked) OrangePrimary else OrangeLight,
        modifier = Modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (checked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = number,
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun StepLine(active: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(2.dp)
            .padding(horizontal = 8.dp)
            .background(if (active) OrangePrimary else GrayBorder)
    )
}

@Composable
private fun PaymentOptionList(
    onPixClick: () -> Unit,
    onCashClick: () -> Unit,
    onCardClick: () -> Unit,
    onBitcoinClick: () -> Unit
) {
    val methods = listOf(
        PaymentOption("Pix", "QR Code e código copia e cola", Icons.Filled.Pix, onPixClick),
        PaymentOption("Dinheiro", "Diga se precisa de troco", Icons.Filled.AttachMoney, onCashClick),
        PaymentOption("Cartão", "Débito ou crédito na entrega", Icons.Filled.CreditCard, onCardClick),
        PaymentOption("Bitcoin", "Pagamento cripto para clientes que preferem", Icons.Filled.CurrencyBitcoin, onBitcoinClick)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        methods.forEach { option ->
            PaymentOptionCard(
                option = option,
                onClick = option.onClick
            )
        }
    }
}

@Composable
private fun PaymentOptionCard(
    option: PaymentOption,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = White,
        animationSpec = tween(180),
        label = "paymentScreenBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = GrayBorder,
        animationSpec = tween(180),
        label = "paymentScreenBorder"
    )
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(180),
        label = "paymentScreenScale"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = OrangeLight,
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = option.title,
                    color = GrayDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = option.subtitle,
                    color = GrayMedium,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DeliveryResumeCard(
    address: String,
    total: Double,
    deliveryFee: Double
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocalShipping,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Resumo final",
                    color = GrayDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = address, color = GrayMedium, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = GrayBorder)
            Spacer(modifier = Modifier.height(10.dp))
            ResumeLine(label = "Entrega", value = deliveryFee)
            ResumeLine(label = "Total", value = total, highlight = true)
        }
    }
}

@Composable
private fun ResumeLine(label: String, value: Double, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (highlight) GrayDark else GrayMedium,
            fontSize = if (highlight) 16.sp else 14.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = "Bs ${"%.2f".format(value)}",
            color = if (highlight) OrangePrimary else GrayDark,
            fontSize = if (highlight) 16.sp else 14.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private data class PaymentOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
