package com.snacklapaz.app.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.snacklapaz.app.ui.theme.OrangeDark
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White
import kotlin.math.roundToInt

@Composable
fun CartAddFeedback(
    animationKey: Int,
    modifier: Modifier = Modifier
) {
    if (animationKey == 0) return

    val progress = remember { Animatable(1f) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(animationKey) {
        visible = true
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 620, easing = FastOutSlowInEasing)
        )
        visible = false
    }

    if (!visible) return

    val value = progress.value
    val yOffset = (-300 + (238 * value)).roundToInt()
    val xOffset = ((1f - value) * -26).roundToInt()
    val alpha = if (value < 0.78f) 1f else 1f - ((value - 0.78f) / 0.22f)
    val scale = 1.08f - (0.28f * value)

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .offset { IntOffset(xOffset, yOffset) }
            .alpha(alpha.coerceIn(0f, 1f))
    ) {
        Surface(
            shape = CircleShape,
            shadowElevation = 10.dp,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.background(
                    Brush.verticalGradient(listOf(OrangePrimary, OrangeDark))
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier
                        .size(23.dp)
                        .scale(scale)
                )
            }
        }
    }
}
