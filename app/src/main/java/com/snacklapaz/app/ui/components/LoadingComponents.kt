package com.snacklapaz.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.snacklapaz.app.ui.theme.GrayLight
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary

/**
 * Retângulo cinza com "brilho" animado passando por cima — usado para
 * montar skeletons de cards, linhas de texto, etc. enquanto carrega.
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 8
) {
    val transition = rememberInfiniteTransition(label = "skeletonShimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(GrayLight.copy(alpha = alpha))
    )
}

/**
 * Skeleton de um card de produto inteiro (imagem + linhas de texto),
 * pra exibir em grade enquanto os produtos reais ainda não chegaram.
 */
@Composable
fun ProductCardSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            cornerRadius = 16
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
        SkeletonBox(modifier = Modifier.fillMaxWidth(0.8f).height(14.dp))
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
        SkeletonBox(modifier = Modifier.fillMaxWidth(0.4f).height(14.dp))
    }
}

/**
 * Indicador de carregamento central com mensagem (ex: "Carregando produtos...").
 * Usar em telas inteiras enquanto o primeiro carregamento acontece.
 */
@Composable
fun SnackLoadingIndicator(
    message: String = "Carregando...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = OrangePrimary, strokeWidth = 3.dp)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMedium
        )
    }
}