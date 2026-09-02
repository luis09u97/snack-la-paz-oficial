package com.snacklapaz.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.snacklapaz.app.ui.theme.GrayMedium

/**
 * Tela amigável para estados vazios (carrinho vazio, sem pedidos, sem
 * favoritos, sem resultados de busca, erro de carregamento, etc).
 * Sempre com ícone + mensagem + ação sugerida.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GrayMedium,
            modifier = Modifier.size(72.dp)
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMedium,
            textAlign = TextAlign.Center
        )

        if (actionLabel != null && onActionClick != null) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
            SnackPrimaryButton(
                text = actionLabel,
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}