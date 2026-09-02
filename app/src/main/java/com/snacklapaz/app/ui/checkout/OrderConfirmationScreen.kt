package com.snacklapaz.app.ui.checkout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.White

@Composable
fun OrderConfirmationScreen(
    orderNumber: String,
    total: Double,
    onViewReceiptClick: () -> Unit,
    onTrackOrderClick: () -> Unit
) {
    val checkScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        checkScale.animateTo(1f, animationSpec = tween(500, easing = EaseOutBack))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = SuccessGreen,
            modifier = Modifier
                .size(96.dp)
                .scale(checkScale.value)
        ) {
            androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Pedido realizado com sucesso!",
            style = MaterialTheme.typography.headlineMedium,
            color = GrayDark,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Pedido nº $orderNumber",
            color = GrayMedium,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Bs ${"%.2f".format(total)}",
            color = OrangePrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        SnackPrimaryButton(text = "Acompanhar pedido", onClick = onTrackOrderClick)
        Spacer(modifier = Modifier.height(12.dp))
        SnackSecondaryButton(text = "Ver recibo", onClick = onViewReceiptClick)
    }
}