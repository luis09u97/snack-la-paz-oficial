package com.snacklapaz.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.auth.AuthViewModel
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.common.UiState
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackSecondaryButton
import com.snacklapaz.app.ui.orders.OrdersViewModel
import com.snacklapaz.app.ui.profile.ProfileDetailSection.ADDRESSES
import com.snacklapaz.app.ui.profile.ProfileDetailSection.COUPONS
import com.snacklapaz.app.ui.profile.ProfileDetailSection.FAVORITES
import com.snacklapaz.app.ui.profile.ProfileDetailSection.NOTIFICATIONS
import com.snacklapaz.app.ui.profile.ProfileDetailSection.PAYMENTS
import com.snacklapaz.app.ui.profile.ProfileDetailSection.PERSONAL_DATA
import com.snacklapaz.app.ui.profile.ProfileDetailSection.SECURITY
import com.snacklapaz.app.ui.profile.ProfileDetailSection.SETTINGS
import com.snacklapaz.app.ui.profile.ProfileDetailSection.SUPPORT
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    ordersViewModel: OrdersViewModel,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onAdminPanelClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileDetailClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 22.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                text = "Perfil",
                color = GrayDark,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            if (!authViewModel.isLoggedIn) {
                LoggedOutContent(onLoginClick = onLoginClick, onSignUpClick = onSignUpClick)
            } else {
                LoggedInContent(
                    authViewModel = authViewModel,
                    cartViewModel = cartViewModel,
                    ordersViewModel = ordersViewModel,
                    onAdminPanelClick = onAdminPanelClick,
                    onOrdersClick = onOrdersClick,
                    onAddressClick = onAddressClick,
                    onPaymentClick = onPaymentClick,
                    onCartClick = onCartClick,
                    onProfileDetailClick = onProfileDetailClick
                )
            }
        }
    }
}

@Composable
private fun LoggedOutContent(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, OrangeLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(108.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Entre para comprar sem repetir dados",
                color = GrayDark,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Sua conta guarda pedidos, recibos, endereço e preferências em um só lugar.",
                color = GrayMedium,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))
            SnackPrimaryButton(text = "Entrar", onClick = onLoginClick)
            Spacer(modifier = Modifier.height(12.dp))
            SnackSecondaryButton(text = "Criar conta", onClick = onSignUpClick)
        }
    }
}

@Composable
private fun LoggedInContent(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    ordersViewModel: OrdersViewModel,
    onAdminPanelClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileDetailClick: (String) -> Unit
) {
    val orderCount = (ordersViewModel.ordersState as? UiState.Success)?.data?.size ?: 0
    val cartCount = cartViewModel.items.sumOf { it.quantity }
    val addressCount = if (cartViewModel.draftAddress.street.isNotBlank()) 1 else 0
    var askLogout by remember { mutableStateOf(false) }

    if (askLogout) {
        AlertDialog(
            onDismissRequest = { askLogout = false },
            title = { Text(text = "Sair da conta?", fontSize = 21.sp, fontWeight = FontWeight.Bold) },
            text = { Text(text = "Você continuará com o app instalado, mas precisará entrar novamente para comprar.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        askLogout = false
                        authViewModel.logout()
                    }
                ) {
                    Text(text = "Sair", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { askLogout = false }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        AccountHeader(authViewModel = authViewModel)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickInfoCard(title = "Pedidos", value = orderCount.toString(), modifier = Modifier.weight(1f), onClick = onOrdersClick)
            QuickInfoCard(title = "Carrinho", value = cartCount.toString(), modifier = Modifier.weight(1f), onClick = onCartClick)
            QuickInfoCard(title = "Endereços", value = addressCount.toString(), modifier = Modifier.weight(1f), onClick = { onProfileDetailClick(ADDRESSES) })
        }

        ProfileHeroAction(
            cartCount = cartCount,
            onCartClick = onCartClick,
            onPaymentClick = {
                if (cartCount > 0) onPaymentClick() else onProfileDetailClick(PAYMENTS)
            }
        )

        ProfileSection(title = "Minha conta") {
            ProfileMenuItem(
                icon = Icons.Filled.AccountCircle,
                label = "Dados pessoais",
                subtitle = "Nome, email, tipo de conta e sessão salva",
                onClick = { onProfileDetailClick(PERSONAL_DATA) }
            )
            ProfileMenuItem(
                icon = Icons.Filled.EditLocation,
                label = "Endereços",
                subtitle = if (addressCount > 0) cartViewModel.draftAddress.formatted() else "Cadastrar ou revisar endereço de entrega",
                onClick = { onProfileDetailClick(ADDRESSES) }
            )
            ProfileMenuItem(
                icon = Icons.Filled.CreditCard,
                label = "Pagamentos",
                subtitle = "Pix, dinheiro, cartão e Bitcoin",
                onClick = { onProfileDetailClick(PAYMENTS) }
            )
        }

        ProfileSection(title = "Compras") {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                label = "Meus pedidos",
                subtitle = "Acompanhar status, recibos e código de entrega",
                onClick = onOrdersClick
            )
            ProfileMenuItem(
                icon = Icons.Filled.Favorite,
                label = "Favoritos",
                subtitle = "Produtos salvos para pedir depois",
                onClick = { onProfileDetailClick(FAVORITES) }
            )
            ProfileMenuItem(
                icon = Icons.Filled.ConfirmationNumber,
                label = "Cupons",
                subtitle = "Promoções e descontos disponíveis",
                onClick = { onProfileDetailClick(COUPONS) }
            )
        }

        ProfileSection(title = "Preferências") {
            ProfileMenuItem(
                icon = Icons.Filled.Notifications,
                label = "Notificações",
                subtitle = "Avisos do pedido e promoções",
                onClick = { onProfileDetailClick(NOTIFICATIONS) }
            )
            ProfileMenuItem(
                icon = Icons.Filled.Security,
                label = "Segurança",
                subtitle = "Sessão salva e acesso protegido",
                onClick = { onProfileDetailClick(SECURITY) }
            )
            ProfileMenuItem(
                icon = Icons.Filled.SupportAgent,
                label = "Atendimento",
                subtitle = "Falar com o Snack La Paz no WhatsApp",
                onClick = { onProfileDetailClick(SUPPORT) }
            )
            ProfileMenuItem(
                icon = Icons.Filled.Settings,
                label = "Configurações",
                subtitle = "Preferências visuais e do aplicativo",
                onClick = { onProfileDetailClick(SETTINGS) }
            )
        }

        if (authViewModel.isAdmin) {
            ProfileSection(title = "Administração") {
                ProfileMenuItem(
                    icon = Icons.Filled.Dashboard,
                    label = "Painel administrativo",
                    subtitle = "Gerenciar catálogo, estoque e pedidos",
                    onClick = onAdminPanelClick
                )
            }
        }

        ProfileSection {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Sair da conta",
                subtitle = "Encerrar sessão neste aparelho",
                labelColor = ErrorRed,
                iconColor = ErrorRed,
                onClick = { askLogout = true }
            )
        }
    }
}

@Composable
private fun AccountHeader(authViewModel: AuthViewModel) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = OrangePrimary,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = White.copy(alpha = 0.22f), modifier = Modifier.size(86.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = authViewModel.userName.firstOrNull()?.uppercase() ?: "?",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = authViewModel.userName, fontWeight = FontWeight.Bold, fontSize = 25.sp, color = White)
                    Text(text = authViewModel.userEmail, color = White.copy(alpha = 0.86f), fontSize = 15.sp)
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = White.copy(alpha = 0.18f),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Text(
                            text = if (authViewModel.isAdmin) "Administrador Snack La Paz" else "Cliente Snack La Paz",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeroAction(
    cartCount: Int,
    onCartClick: () -> Unit,
    onPaymentClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, OrangeLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(18.dp)
        ) {
            Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(58.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(30.dp))
                }
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(
                    text = if (cartCount > 0) "Você tem $cartCount item(ns) no carrinho" else "Seu carrinho está vazio",
                    color = GrayDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
                Text(
                    text = if (cartCount > 0) "Finalize o pedido em poucos toques." else "Escolha um produto e volte aqui para pagar.",
                    color = GrayMedium,
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
            }
            Text(
                text = if (cartCount > 0) "Pagar" else "Ver",
                color = OrangePrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = if (cartCount > 0) onPaymentClick else onCartClick)
            )
        }
    }
}

@Composable
private fun QuickInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.65f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 18.dp)) {
            Text(text = value, color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Text(text = title, color = GrayMedium, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ProfileSection(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        if (title != null) {
            Text(
                text = title,
                color = GrayDark,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                modifier = Modifier.padding(start = 2.dp, bottom = 10.dp)
            )
        }
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = White,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.65f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    labelColor: Color = GrayDark,
    iconColor: Color = OrangePrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = OrangeLight.copy(alpha = 0.72f), modifier = Modifier.size(52.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = labelColor, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = GrayMedium, fontSize = 14.sp, lineHeight = 18.sp)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = GrayMedium, modifier = Modifier.size(28.dp))
    }
}
