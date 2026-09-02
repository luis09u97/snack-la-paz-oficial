package com.snacklapaz.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.snacklapaz.app.R
import com.snacklapaz.app.ui.theme.OrangePrimary
import kotlinx.coroutines.delay

/**
 * Splash em Compose exibida logo após a splash nativa do sistema.
 * Logo grande, centralizado, com uma leve animação de entrada.
 * Chama [onFinished] após [durationMillis] para navegar adiante.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    durationMillis: Long = 2700
) {
    val logoScale = remember { Animatable(0.4f) }
    val logoAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo entra com fade + leve "bounce" (profissional, não exagerado)
        logoAlpha.animateTo(1f, animationSpec = tween(450))
        logoScale.animateTo(
            targetValue = 1.5f,
            animationSpec = tween(550, easing = EaseOutBack)
        )
        // Aguarda e segue para a próxima tela
        delay(durationMillis)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangePrimary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_full),
            contentDescription = "Snack La Paz",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .scale(logoScale.value)
                .alpha(logoAlpha.value)
        )
    }
}