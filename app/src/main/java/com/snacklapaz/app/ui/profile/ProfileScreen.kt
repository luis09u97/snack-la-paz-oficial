package com.snacklapaz.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.auth.AuthViewModel
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackSecondaryButton
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onAdminPanelClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = GrayDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (!authViewModel.isLoggedIn) {
            LoggedOutContent(onLoginClick = onLoginClick, onSignUpClick = onSignUpClick)
        } else {
            LoggedInContent(authViewModel = authViewModel, onAdminPanelClick = onAdminPanelClick)
        }
    }
}

@Composable
private fun LoggedOutContent(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(80.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Você ainda não entrou na sua conta",
            color = GrayDark,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        Text(
            text = "Entre para ver seus pedidos e favoritos",
            color = GrayMedium,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        SnackPrimaryButton(text = "Entrar", onClick = onLoginClick)
        Spacer(modifier = Modifier.height(10.dp))
        SnackSecondaryButton(text = "Criar conta", onClick = onSignUpClick)
    }
}

@Composable
private fun LoggedInContent(
    authViewModel: AuthViewModel,
    onAdminPanelClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(56.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = authViewModel.userName.firstOrNull()?.uppercase() ?: "?",
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = authViewModel.userName, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = GrayDark)
                Text(text = authViewModel.userEmail, color = GrayMedium, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(shape = RoundedCornerShape(14.dp), color = White, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
            Column {
                ProfileMenuItem(icon = Icons.Filled.LocationOn, label = "Meus endereços")
                ProfileMenuItem(icon = Icons.Filled.Favorite, label = "Favoritos")
                ProfileMenuItem(icon = Icons.Filled.Settings, label = "Configurações")
                if (authViewModel.isAdmin) {
                    ProfileMenuItem(
                        icon = Icons.Filled.Dashboard,
                        label = "Painel administrativo",
                        onClick = onAdminPanelClick
                    )
                }
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = "Sair",
                    labelColor = ErrorRed,
                    iconColor = ErrorRed,
                    onClick = { authViewModel.logout() }
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    labelColor: androidx.compose.ui.graphics.Color = GrayDark,
    iconColor: androidx.compose.ui.graphics.Color = OrangePrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = label, color = labelColor, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GrayMedium
        )
    }
}