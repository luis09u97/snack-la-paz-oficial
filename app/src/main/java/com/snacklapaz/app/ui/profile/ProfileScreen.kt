package com.snacklapaz.app.ui.profile

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.auth.AuthViewModel
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackSecondaryButton
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
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onAdminPanelClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.headlineMedium,
                color = GrayDark,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        item {
            if (!authViewModel.isLoggedIn) {
                LoggedOutContent(onLoginClick = onLoginClick, onSignUpClick = onSignUpClick)
            } else {
                LoggedInContent(authViewModel = authViewModel, onAdminPanelClick = onAdminPanelClick)
            }
        }
    }
}

@Composable
private fun LoggedOutContent(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(86.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(42.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Entre para finalizar pedidos",
            color = GrayDark,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "Sua conta guarda carrinho, endereços, pedidos e favoritos.",
            color = GrayMedium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 8.dp)
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
    val context = LocalContext.current
    var dialogTitle by remember { mutableStateOf<String?>(null) }
    var dialogText by remember { mutableStateOf("") }

    dialogTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { dialogTitle = null },
            title = { Text(text = title) },
            text = { Text(text = dialogText) },
            confirmButton = {
                TextButton(onClick = { dialogTitle = null }) {
                    Text(text = "Entendi")
                }
            }
        )
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        AccountHeader(authViewModel = authViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickInfoCard(title = "Pedidos", value = "0", modifier = Modifier.weight(1f))
            QuickInfoCard(title = "Favoritos", value = "0", modifier = Modifier.weight(1f))
            QuickInfoCard(title = "Endereços", value = "1", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        ProfileSection(title = "Minha conta") {
            ProfileMenuItem(
                icon = Icons.Filled.Person,
                label = "Dados pessoais",
                subtitle = authViewModel.userEmail,
                onClick = {
                    dialogTitle = "Dados pessoais"
                    dialogText = "Aqui o cliente poderá atualizar nome, telefone e email quando a autenticação real estiver ligada ao Supabase."
                }
            )
            ProfileMenuItem(
                icon = Icons.Filled.LocationOn,
                label = "Endereços",
                subtitle = "Salvar casa, trabalho e outros locais",
                onClick = {
                    dialogTitle = "Endereços"
                    dialogText = "O próximo passo é salvar endereços no banco para o cliente não preencher tudo novamente a cada pedido."
                }
            )
            ProfileMenuItem(
                icon = Icons.Filled.CreditCard,
                label = "Pagamentos",
                subtitle = "Formas de pagamento preferidas",
                onClick = {
                    dialogTitle = "Pagamentos"
                    dialogText = "Esta área ficará pronta para Pix, dinheiro, cartão e histórico de pagamentos."
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        ProfileSection(title = "Compras") {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                label = "Meus pedidos",
                subtitle = "Acompanhar compras e recibos",
                onClick = {
                    dialogTitle = "Meus pedidos"
                    dialogText = "Quando os pedidos forem persistidos no Supabase, esta tela mostrará pedidos anteriores e status de entrega."
                }
            )
            ProfileMenuItem(
                icon = Icons.Filled.Favorite,
                label = "Favoritos",
                subtitle = "Produtos salvos para pedir depois",
                onClick = {
                    dialogTitle = "Favoritos"
                    dialogText = "Os produtos favoritos já podem ser marcados na interface. Depois eles serão salvos por usuário no banco."
                }
            )
            ProfileMenuItem(
                icon = Icons.Filled.ConfirmationNumber,
                label = "Cupons",
                subtitle = "Promoções e descontos disponíveis",
                onClick = {
                    dialogTitle = "Cupons"
                    dialogText = "Esta área vai reunir cupons ativos para incentivar novas compras."
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        ProfileSection(title = "Preferências") {
            ProfileMenuItem(
                icon = Icons.Filled.Notifications,
                label = "Notificações",
                subtitle = "Avisos sobre pedido e promoções",
                onClick = {
                    dialogTitle = "Notificações"
                    dialogText = "Aqui o cliente poderá escolher receber avisos de pedido, novidades e promoções."
                }
            )
            ProfileMenuItem(
                icon = Icons.Filled.Security,
                label = "Segurança",
                subtitle = "Senha e acesso da conta",
                onClick = {
                    dialogTitle = "Segurança"
                    dialogText = "Esta opção será conectada ao Supabase Auth para troca de senha e proteção da conta."
                }
            )
            ProfileMenuItem(
                icon = Icons.Filled.SupportAgent,
                label = "Atendimento",
                subtitle = "Falar com o Snack La Paz",
                onClick = { openSupport(context) }
            )
            ProfileMenuItem(
                icon = Icons.Filled.Settings,
                label = "Configurações",
                subtitle = "Preferências do aplicativo",
                onClick = {
                    dialogTitle = "Configurações"
                    dialogText = "Preferências visuais, idioma e comportamento do app ficarão concentrados aqui."
                }
            )
        }

        if (authViewModel.isAdmin) {
            Spacer(modifier = Modifier.height(14.dp))
            ProfileSection(title = "Administração") {
                ProfileMenuItem(
                    icon = Icons.Filled.Dashboard,
                    label = "Painel administrativo",
                    subtitle = "Gerenciar catálogo e pedidos",
                    onClick = onAdminPanelClick
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        ProfileSection {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Sair da conta",
                subtitle = "Encerrar sessão neste aparelho",
                labelColor = ErrorRed,
                iconColor = ErrorRed,
                onClick = { authViewModel.logout() }
            )
        }
    }
}

@Composable
private fun AccountHeader(authViewModel: AuthViewModel) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(60.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = authViewModel.userName.firstOrNull()?.uppercase() ?: "?",
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = authViewModel.userName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GrayDark)
                Text(text = authViewModel.userEmail, color = GrayMedium, fontSize = 13.sp)
                Text(
                    text = "Cliente Snack La Paz",
                    color = OrangePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.65f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Text(text = value, color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = title, color = GrayMedium, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ProfileSection(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (title != null) {
        Text(
            text = title,
            color = GrayDark,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
        )
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.65f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    labelColor: androidx.compose.ui.graphics.Color = GrayDark,
    iconColor: androidx.compose.ui.graphics.Color = OrangePrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = OrangeLight.copy(alpha = 0.72f), modifier = Modifier.size(38.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(21.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = labelColor, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = GrayMedium, fontSize = 12.sp, lineHeight = 16.sp)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GrayMedium
        )
    }
}

private fun openSupport(context: android.content.Context) {
    val message = Uri.encode("Olá, Snack La Paz. Quero ajuda com meu pedido.")
    val uri = Uri.parse("https://wa.me/?text=$message")
    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
}
