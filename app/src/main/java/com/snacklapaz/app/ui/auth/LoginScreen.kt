package com.snacklapaz.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.R
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTextField
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onGoToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(rememberScrollState())
            .padding(28.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_icon),
            contentDescription = "Snack La Paz",
            modifier = Modifier.height(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Bem-vindo de volta!",
            style = MaterialTheme.typography.headlineMedium,
            color = GrayDark,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Entre para continuar seu pedido",
            color = GrayMedium,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        SnackTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = "E-mail",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(14.dp))

        SnackTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = "Senha",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            errorMessage = errorMessage
        )

        Spacer(modifier = Modifier.height(28.dp))

        SnackPrimaryButton(
            text = "Entrar",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Preencha e-mail e senha para continuar."
                } else {
                    authViewModel.login(email, password)
                    onLoginSuccess()
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(text = "Não tem conta? ", color = GrayMedium, fontSize = 14.sp)
            Text(
                text = "Cadastre-se",
                color = OrangePrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onGoToSignUp)
            )
        }
    }
}