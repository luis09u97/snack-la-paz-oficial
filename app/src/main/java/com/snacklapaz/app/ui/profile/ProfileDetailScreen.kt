package com.snacklapaz.app.ui.profile

import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.auth.AuthViewModel
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackSecondaryButton
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.orders.OrdersViewModel
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.OrangeSoft
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.SuccessGreenLight
import com.snacklapaz.app.ui.theme.White

object ProfileDetailSection {
    const val PERSONAL_DATA = "personal_data"
    const val ADDRESSES = "addresses"
    const val PAYMENTS = "payments"
    const val FAVORITES = "favorites"
    const val COUPONS = "coupons"
    const val NOTIFICATIONS = "notifications"
    const val SECURITY = "security"
    const val SUPPORT = "support"
    const val SETTINGS = "settings"
}

@Composable
fun ProfileDetailScreen(
    sectionId: String,
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    ordersViewModel: OrdersViewModel,
    onBackClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val context = LocalContext.current
    val page = profilePage(sectionId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = page.title, onBackClick = onBackClick)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                DetailHero(page = page)
            }
            item {
                when (sectionId) {
                    ProfileDetailSection.PERSONAL_DATA -> PersonalDataContent(authViewModel)
                    ProfileDetailSection.ADDRESSES -> AddressContent(cartViewModel, onAddressClick)
                    ProfileDetailSection.PAYMENTS -> PaymentsContent(cartViewModel, onPaymentClick, onCartClick)
                    ProfileDetailSection.FAVORITES -> FavoritesContent(onCartClick)
                    ProfileDetailSection.COUPONS -> CouponsContent(onCartClick)
                    ProfileDetailSection.NOTIFICATIONS -> NotificationsContent()
                    ProfileDetailSection.SECURITY -> SecurityContent(authViewModel)
                    ProfileDetailSection.SUPPORT -> SupportContent(
                        userName = authViewModel.userName,
                        context = context
                    )
                    ProfileDetailSection.SETTINGS -> SettingsContent()
                    else -> OrdersShortcutContent(ordersViewModel, onOrdersClick)
                }
            }
        }
    }
}

@Composable
private fun DetailHero(page: ProfilePage) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = page.color,
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = White.copy(alpha = 0.24f), modifier = Modifier.size(78.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(page.icon, contentDescription = null, tint = White, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = page.title, color = White, fontWeight = FontWeight.Bold, fontSize = 27.sp, lineHeight = 31.sp)
                Text(text = page.subtitle, color = White.copy(alpha = 0.9f), fontSize = 15.sp, lineHeight = 21.sp)
            }
        }
    }
}

@Composable
private fun PersonalDataContent(authViewModel: AuthViewModel) {
    DetailCard {
        BigInfoLine("Nome do cliente", authViewModel.userName.ifBlank { "Não informado" }, Icons.Filled.AccountCircle)
        BigInfoLine("E-mail da conta", authViewModel.userEmail.ifBlank { "Não informado" }, Icons.Filled.CreditCard)
        BigInfoLine("Tipo de acesso", if (authViewModel.isAdmin) "Administrador" else "Cliente", Icons.Filled.Security)
        StatusPill("Sessão salva neste aparelho", "Ao reabrir o app, a conta continua conectada.")
    }
}

@Composable
private fun AddressContent(cartViewModel: CartViewModel, onAddressClick: () -> Unit) {
    val address = cartViewModel.draftAddress
    DetailCard {
        if (address.street.isBlank()) {
            EmptyProfileState(
                icon = Icons.Filled.LocationOn,
                title = "Nenhum endereço salvo",
                subtitle = "Cadastre o CEP e o app completa rua, bairro e cidade para acelerar a compra."
            )
        } else {
            BigInfoLine("Endereço principal", address.formatted(), Icons.Filled.LocationOn)
            BigInfoLine("Contato da entrega", "${address.fullName} - ${address.phone}", Icons.Filled.Phone)
        }
        Spacer(modifier = Modifier.height(18.dp))
        SnackPrimaryButton(text = "Cadastrar ou editar endereço", onClick = onAddressClick)
    }
}

@Composable
private fun PaymentsContent(
    cartViewModel: CartViewModel,
    onPaymentClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val hasCartItems = cartViewModel.items.isNotEmpty()
    DetailCard {
        PaymentOption("Pix", "QR Code e copia e cola com a chave cadastrada.", Icons.Filled.Payment)
        PaymentOption("Dinheiro", "Pergunta de troco com cálculo antes de confirmar.", Icons.Filled.CheckCircle)
        PaymentOption("Cartão", "Débito ou crédito no momento da entrega.", Icons.Filled.CreditCard)
        PaymentOption("Bitcoin", "Opção exibida para clientes que preferem cripto.", Icons.Filled.Security)
        Spacer(modifier = Modifier.height(18.dp))
        if (hasCartItems) {
            SnackPrimaryButton(text = "Escolher forma de pagamento", onClick = onPaymentClick)
        } else {
            SnackSecondaryButton(text = "Adicionar produtos primeiro", onClick = onCartClick)
        }
    }
}

@Composable
private fun FavoritesContent(onCartClick: () -> Unit) {
    DetailCard {
        EmptyProfileState(
            icon = Icons.Filled.Favorite,
            title = "Favoritos ficam no catálogo",
            subtitle = "Use o coração nos produtos para guardar escolhas enquanto monta o pedido."
        )
        Spacer(modifier = Modifier.height(18.dp))
        SnackPrimaryButton(text = "Ver produtos", onClick = onCartClick)
    }
}

@Composable
private fun CouponsContent(onCartClick: () -> Unit) {
    DetailCard {
        CouponCard("BEMVINDO", "Campanha para novos clientes", "Use em ações promocionais do primeiro pedido.")
        CouponCard("COMBO", "Comida + bebida", "Ideal para incentivar compra completa.")
        CouponCard("FRETEGRATIS", "Entrega especial", "Cupom pronto para campanhas por região.")
        Spacer(modifier = Modifier.height(18.dp))
        SnackPrimaryButton(text = "Montar pedido com promoção", onClick = onCartClick)
    }
}

@Composable
private fun NotificationsContent() {
    var orderNotifications by remember { mutableStateOf(true) }
    var promoNotifications by remember { mutableStateOf(true) }
    var deliveryNotifications by remember { mutableStateOf(true) }

    DetailCard {
        PreferenceSwitch("Status do pedido", "Receba avisos quando o pedido mudar de etapa.", orderNotifications) {
            orderNotifications = it
        }
        PreferenceSwitch("Promoções", "Novidades, combos e descontos do Snack La Paz.", promoNotifications) {
            promoNotifications = it
        }
        PreferenceSwitch("Entrega", "Alertas quando o entregador estiver chegando.", deliveryNotifications) {
            deliveryNotifications = it
        }
        StatusPill("Preferência aplicada", "As escolhas ficam ativas durante o uso atual do app.")
    }
}

@Composable
private fun SecurityContent(authViewModel: AuthViewModel) {
    DetailCard {
        BigInfoLine("Conta protegida", "Login validado pelo Supabase Auth", Icons.Filled.Security)
        BigInfoLine("Persistência", "A sessão permanece salva ao fechar e abrir o app", Icons.Filled.CheckCircle)
        BigInfoLine("Usuário atual", authViewModel.userEmail.ifBlank { "Não informado" }, Icons.Filled.AccountCircle)
        StatusPill("Dica de segurança", "Use sair da conta se o aparelho for compartilhado.")
    }
}

@Composable
private fun SupportContent(userName: String, context: Context) {
    DetailCard {
        EmptyProfileState(
            icon = Icons.Filled.SupportAgent,
            title = "Atendimento Snack La Paz",
            subtitle = "Abra o WhatsApp com uma mensagem pronta para pedir ajuda com compra, entrega ou pagamento."
        )
        Spacer(modifier = Modifier.height(18.dp))
        SnackPrimaryButton(
            text = "Falar no WhatsApp",
            onClick = { openSupport(context, userName) }
        )
    }
}

@Composable
private fun SettingsContent() {
    var compactMode by remember { mutableStateOf(false) }
    var highContrast by remember { mutableStateOf(false) }
    var keepSession by remember { mutableStateOf(true) }

    DetailCard {
        PreferenceSwitch("Manter sessão conectada", "Evita login toda vez que abrir o app.", keepSession) {
            keepSession = it
        }
        PreferenceSwitch("Modo compacto", "Reduz textos de apoio em áreas futuras.", compactMode) {
            compactMode = it
        }
        PreferenceSwitch("Destaque visual", "Aumenta contraste em cards importantes.", highContrast) {
            highContrast = it
        }
        StatusPill("Configurações visuais", "Preparadas para evoluir sem confundir o cliente.")
    }
}

@Composable
private fun OrdersShortcutContent(
    ordersViewModel: OrdersViewModel,
    onOrdersClick: () -> Unit
) {
    DetailCard {
        EmptyProfileState(
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            title = "Meus pedidos",
            subtitle = "Acompanhe status, recibos e código de segurança da entrega."
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Abra a lista completa para ver compras anteriores e detalhes de cada pedido.",
            color = GrayMedium,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        SnackPrimaryButton(text = "Abrir meus pedidos", onClick = onOrdersClick)
    }
}

@Composable
private fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.72f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
private fun BigInfoLine(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(54.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = GrayMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = value, color = GrayDark, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 23.sp)
        }
    }
}

@Composable
private fun PaymentOption(title: String, subtitle: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CreamBackground,
        border = BorderStroke(1.dp, OrangeLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Surface(shape = CircleShape, color = White, modifier = Modifier.size(54.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = subtitle, color = GrayMedium, fontSize = 14.sp, lineHeight = 19.sp)
            }
        }
    }
}

@Composable
private fun CouponCard(code: String, title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = OrangeSoft,
        border = BorderStroke(1.dp, OrangeLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Surface(shape = CircleShape, color = OrangePrimary, modifier = Modifier.size(54.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.LocalOffer, contentDescription = null, tint = White, modifier = Modifier.size(27.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = code, color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = title, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, color = GrayMedium, fontSize = 14.sp, lineHeight = 19.sp)
            }
        }
    }
}

@Composable
private fun PreferenceSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = subtitle, color = GrayMedium, fontSize = 14.sp, lineHeight = 19.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
    HorizontalDivider(color = GrayBorder.copy(alpha = 0.55f))
}

@Composable
private fun EmptyProfileState(icon: ImageVector, title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(92.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(46.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 24.sp, textAlign = TextAlign.Center)
        Text(
            text = subtitle,
            color = GrayMedium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun StatusPill(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SuccessGreenLight,
        border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.18f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(text = subtitle, color = GrayMedium, fontSize = 14.sp, lineHeight = 19.sp)
            }
        }
    }
}

private data class ProfilePage(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

private fun profilePage(sectionId: String): ProfilePage {
    return when (sectionId) {
        ProfileDetailSection.PERSONAL_DATA -> ProfilePage("Dados pessoais", "Veja suas informações principais da conta.", Icons.Filled.AccountCircle, OrangePrimary)
        ProfileDetailSection.ADDRESSES -> ProfilePage("Endereços", "Controle o local de entrega antes de pedir.", Icons.Filled.EditLocation, OrangePrimary)
        ProfileDetailSection.PAYMENTS -> ProfilePage("Pagamentos", "Escolha Pix, dinheiro, cartão ou Bitcoin.", Icons.Filled.CreditCard, OrangePrimary)
        ProfileDetailSection.FAVORITES -> ProfilePage("Favoritos", "Produtos salvos para comprar mais rápido.", Icons.Filled.Favorite, OrangePrimary)
        ProfileDetailSection.COUPONS -> ProfilePage("Cupons", "Promoções organizadas para o cliente entender.", Icons.Filled.ConfirmationNumber, OrangePrimary)
        ProfileDetailSection.NOTIFICATIONS -> ProfilePage("Notificações", "Decida quais avisos quer receber.", Icons.Filled.Notifications, OrangePrimary)
        ProfileDetailSection.SECURITY -> ProfilePage("Segurança", "Conta conectada com mais confiança.", Icons.Filled.Security, OrangePrimary)
        ProfileDetailSection.SUPPORT -> ProfilePage("Atendimento", "Fale com o Snack La Paz quando precisar.", Icons.Filled.SupportAgent, OrangePrimary)
        ProfileDetailSection.SETTINGS -> ProfilePage("Configurações", "Ajustes simples para deixar o app do seu jeito.", Icons.Filled.Settings, OrangePrimary)
        else -> ProfilePage("Meus pedidos", "Acompanhe suas compras anteriores.", Icons.AutoMirrored.Filled.ReceiptLong, OrangePrimary)
    }
}

private fun openSupport(context: Context, userName: String) {
    val message = Uri.encode("Olá, Snack La Paz. Sou $userName e quero ajuda com meu pedido.")
    val uri = Uri.parse("https://wa.me/5511963641616?text=$message")
    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
}
