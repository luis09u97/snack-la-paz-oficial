package com.snacklapaz.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.White

/**
 * Cabeçalho padrão das telas internas (ex: Detalhes do produto, Endereço,
 * Recibo). Mostra o título e, opcionalmente, uma seta de voltar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnackTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = GrayDark
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = GrayDark
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
        modifier = Modifier.fillMaxWidth()
    )
}