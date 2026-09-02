package com.snacklapaz.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.theme.GrayLight
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OnOrange
import com.snacklapaz.app.ui.theme.OrangePrimary

/**
 * Botão laranja principal do app (ex: "Continuar pedido", "Finalizar pedido").
 * Encolhe levemente ao ser pressionado, dando feedback tátil visual.
 */
@Composable
fun SnackPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "buttonScale"
    )

    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OrangePrimary,
            contentColor = OnOrange,
            disabledContainerColor = GrayLight,
            disabledContentColor = GrayMedium
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .scale(scale)
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = OnOrange,
                strokeWidth = 2.dp,
                modifier = Modifier.height(22.dp)
            )
        } else {
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Botão secundário (contorno laranja, fundo transparente).
 * Usado para ações menos prioritárias (ex: "Ver recibo").
 */
@Composable
fun SnackSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "buttonScale"
    )

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .scale(scale)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}