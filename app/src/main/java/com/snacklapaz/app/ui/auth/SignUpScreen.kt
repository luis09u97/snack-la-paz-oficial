package com.snacklapaz.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTextField
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = "Criar conta", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Crie sua conta",
                style = MaterialTheme.typography.headlineMedium,
                color = GrayDark,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Leva menos de um minuto",
                color = GrayMedium,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            SnackTextField(
                value = fullName,
                onValueChange = { fullName = it; errorMessage = null },
                label = "Nome completo",
                leadingIcon = Icons.Filled.Person
            )
            Spacer(modifier = Modifier.height(14.dp))

            SnackTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = "E-mail",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(14.dp))

            SnackTextField(
                value = phone,
                onValueChange = { phone = it; errorMessage = null },
                label = "Telefone",
                leadingIcon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(14.dp))

            SnackTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = "Senha",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true
            )
            Spacer(modifier = Modifier.height(14.dp))

            SnackTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorMessage = null },
                label = "Confirmar senha",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true,
                errorMessage = errorMessage
            )

            Spacer(modifier = Modifier.height(28.dp))

            SnackPrimaryButton(
                text = "Criar conta",
                onClick = {
                    errorMessage = when {
                        fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() ->
                            "Preencha todos os campos para continuar."
                        password.length < 6 ->
                            "A senha precisa ter pelo menos 6 caracteres."
                        password != confirmPassword ->
                            "As senhas não coincidem."
                        else -> null
                    }

                    if (errorMessage == null) {
                        authViewModel.signUp(fullName, email, password)
                        onSignUpSuccess()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}