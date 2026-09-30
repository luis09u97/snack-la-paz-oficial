package com.snacklapaz.app.ui.checkout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackSecondaryButton
import com.snacklapaz.app.ui.cart.model.DeliverySecurityCode
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.SuccessGreenLight
import com.snacklapaz.app.ui.theme.White

@Composable
fun OrderConfirmationScreen(
    orderNumber: String,
    total: Double,
    onViewReceiptClick: () -> Unit,
    onTrackOrderClick: () -> Unit
) {
    val checkScale = remember { Animatable(0f) }
    val deliveryCode = remember(orderNumber) { DeliverySecurityCode.fromOrderNumber(orderNumber) }

    LaunchedEffect(Unit) {
        checkScale.animateTo(1f, animationSpec = tween(500, easing = EaseOutBack))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = CircleShape,
                color = SuccessGreenLight,
                modifier = Modifier.size(132.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = SuccessGreen,
                        modifier = Modifier
                            .size(92.dp)
                            .scale(checkScale.value)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Pedido confirmado",
                style = MaterialTheme.typography.headlineMedium,
                color = GrayDark,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pedido nº $orderNumber",
                color = GrayMedium,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Bs ${"%.2f".format(total)}",
                color = OrangePrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(26.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = White,
                border = BorderStroke(1.dp, OrangeLight),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(22.dp)
                ) {
                    Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(48.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Código de segurança da entrega",
                        color = GrayDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = deliveryCode,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        letterSpacing = 4.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Mostre este código somente quando o entregador chegar com seu pedido.",
                        color = GrayMedium,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            SnackPrimaryButton(text = "Acompanhar pedido", onClick = onTrackOrderClick)
            Spacer(modifier = Modifier.height(12.dp))
            SnackSecondaryButton(text = "Ver recibo", onClick = onViewReceiptClick)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
