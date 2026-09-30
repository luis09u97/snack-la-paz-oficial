package com.snacklapaz.app.ui.checkout

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.cart.model.DeliveryAddress
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTextField
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.OrangeSoft
import com.snacklapaz.app.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

@Composable
fun AddressScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onContinueToPayment: () -> Unit
) {
    val context = LocalContext.current
    val savedAddress = remember { loadLastDeliveryAddress(context) }
    val initialAddress = remember(cartViewModel.draftAddress, savedAddress) {
        if (cartViewModel.draftAddress.hasAnyDeliveryInfo()) cartViewModel.draftAddress else savedAddress
    }

    LaunchedEffect(Unit) {
        if (!cartViewModel.draftAddress.hasAnyDeliveryInfo() && savedAddress.hasAnyDeliveryInfo()) {
            cartViewModel.updateDraftAddress(savedAddress)
        }
    }

    var fullName by remember { mutableStateOf(initialAddress.fullName) }
    var phone by remember { mutableStateOf(formatBrazilianPhone(initialAddress.phone)) }
    var cep by remember { mutableStateOf(initialAddress.cep) }
    var street by remember { mutableStateOf(initialAddress.street) }
    var number by remember { mutableStateOf(initialAddress.number) }
    var neighborhood by remember { mutableStateOf(initialAddress.neighborhood) }
    var complement by remember { mutableStateOf(initialAddress.complement) }
    var city by remember { mutableStateOf(initialAddress.city) }
    var state by remember { mutableStateOf(initialAddress.state) }
    var cepStatus by remember { mutableStateOf<String?>(null) }
    var isCepLoading by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    val cleanCep = cep.filter { it.isDigit() }
    val cleanPhone = phone.filter { it.isDigit() }
    val isFormValid = fullName.isNotBlank() && cleanPhone.length >= 10 && cleanCep.length == 8 &&
            street.isNotBlank() && number.isNotBlank() && neighborhood.isNotBlank()
    val currentDraft = DeliveryAddress(
        fullName = fullName,
        phone = phone,
        street = street,
        number = number,
        neighborhood = neighborhood,
        complement = complement,
        cep = cleanCep,
        city = city,
        state = state
    )
    val hasTypedAddress = listOf(fullName, phone, cep, street, number, neighborhood, complement)
        .any { it.isNotBlank() }

    LaunchedEffect(cleanCep) {
        if (cleanCep.length == 8) {
            delay(450)
            isCepLoading = true
            cepStatus = "Buscando endereço..."
            val result = lookupCep(cleanCep)
            isCepLoading = false
            if (result == null) {
                cepStatus = "CEP não encontrado. Confira os números."
            } else {
                street = result.logradouro
                neighborhood = result.bairro
                city = result.localidade
                state = result.uf
                cepStatus = "Endereço encontrado"
                cartViewModel.updateDraftAddress(
                    currentDraft.copy(
                        street = result.logradouro,
                        neighborhood = result.bairro,
                        city = result.localidade,
                        state = result.uf
                    )
                )
            }
        } else {
            cepStatus = null
        }
    }

    fun leaveKeepingDraft() {
        saveLastDeliveryAddress(context, currentDraft)
        cartViewModel.updateDraftAddress(currentDraft)
        onBackClick()
    }

    fun leaveDiscardingDraft() {
        cartViewModel.clearDraftAddress()
        onBackClick()
    }

    BackHandler {
        if (hasTypedAddress) {
            showExitDialog = true
        } else {
            onBackClick()
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Salvar endereço preenchido?") },
            text = {
                Text(
                    text = "Você pode voltar agora e continuar o pedido depois sem digitar tudo novamente."
                )
            },
            confirmButton = {
                TextButton(onClick = { leaveKeepingDraft() }) {
                    Text(text = "Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { leaveDiscardingDraft() }) {
                    Text(text = "Descartar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(
            title = "Endereço de entrega",
            onBackClick = {
                if (hasTypedAddress) {
                    showExitDialog = true
                } else {
                    onBackClick()
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            CheckoutAddressHero(hasSavedAddress = initialAddress.hasAnyDeliveryInfo())
            Spacer(modifier = Modifier.height(18.dp))

            SnackTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    cartViewModel.updateDraftAddress(currentDraft.copy(fullName = it))
                },
                label = "Nome completo",
                leadingIcon = Icons.Filled.Person
            )
            Spacer(modifier = Modifier.height(16.dp))

            SnackTextField(
                value = phone,
                onValueChange = {
                    val maskedPhone = formatBrazilianPhone(it)
                    phone = maskedPhone
                    cartViewModel.updateDraftAddress(currentDraft.copy(phone = maskedPhone))
                },
                label = "Telefone com DDD",
                leadingIcon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(16.dp))

            SnackTextField(
                value = cep,
                onValueChange = {
                    val newCep = it.filter { char -> char.isDigit() }.take(8)
                    cep = newCep
                    cartViewModel.updateDraftAddress(currentDraft.copy(cep = newCep))
                },
                label = "CEP",
                leadingIcon = Icons.Filled.Search,
                keyboardType = KeyboardType.Number
            )

            if (cepStatus != null) {
                Text(
                    text = cepStatus.orEmpty(),
                    color = if (isCepLoading) GrayMedium else OrangePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AddressPreviewCard(
                street = street,
                neighborhood = neighborhood,
                city = city,
                state = state
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SnackTextField(
                    value = number,
                    onValueChange = {
                        number = it
                        cartViewModel.updateDraftAddress(currentDraft.copy(number = it))
                    },
                    label = "Número",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                SnackTextField(
                    value = complement,
                    onValueChange = {
                        complement = it
                        cartViewModel.updateDraftAddress(currentDraft.copy(complement = it))
                    },
                    label = "Compl. (opcional)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            OrderSummaryCard(
                subtotal = cartViewModel.subtotal,
                deliveryFee = cartViewModel.deliveryFeeFor(currentDraft),
                total = cartViewModel.totalFor(currentDraft)
            )
        }

        Surface(color = White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                SnackPrimaryButton(
                    text = "Continuar para pagamento",
                    enabled = isFormValid,
                    onClick = {
                        val address = DeliveryAddress(
                            fullName = fullName,
                            phone = phone,
                            street = street,
                            number = number,
                            neighborhood = neighborhood,
                            complement = complement,
                            cep = cleanCep,
                            city = city,
                            state = state
                        )
                        saveLastDeliveryAddress(context, address)
                        cartViewModel.updateDraftAddress(address)
                        onContinueToPayment()
                    }
                )
            }
        }
    }
}

@Composable
private fun CheckoutAddressHero(hasSavedAddress: Boolean) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, OrangeLight.copy(alpha = 0.85f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(18.dp)
        ) {
            Surface(shape = RoundedCornerShape(18.dp), color = OrangePrimary, modifier = Modifier.size(62.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (hasSavedAddress) Icons.Filled.CheckCircle else Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(33.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = if (hasSavedAddress) "Dados da última compra carregados" else "Informe onde vamos entregar",
                    color = GrayDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
                Text(
                    text = if (hasSavedAddress) {
                        "Confira os dados e altere apenas o que mudou."
                    } else {
                        "Digite o CEP para completar rua, bairro e cidade automaticamente."
                    },
                    color = GrayMedium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AddressPreviewCard(
    street: String,
    neighborhood: String,
    city: String,
    state: String
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, OrangeLight.copy(alpha = 0.75f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(18.dp)
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = OrangeLight, modifier = Modifier.size(58.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = if (street.isBlank()) "Digite o CEP para encontrar o endereço" else street,
                    color = GrayDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 23.sp
                )
                Text(
                    text = if (neighborhood.isBlank()) {
                        "Rua, bairro e cidade aparecerão automaticamente"
                    } else {
                        "$neighborhood, $city/$state"
                    },
                    color = GrayMedium,
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    deliveryFee: Double,
    total: Double
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = White,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Resumo do pedido",
                color = GrayDark,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            SummaryLine(label = "Subtotal", value = subtotal)
            SummaryLine(label = "Taxa de entrega", value = deliveryFee)

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = GrayBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 21.sp, color = GrayDark)
                Surface(shape = RoundedCornerShape(999.dp), color = OrangeSoft) {
                    Text(
                        text = "Bs ${"%.2f".format(total)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = OrangePrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Serializable
private data class ViaCepResponse(
    val cep: String? = null,
    val logradouro: String = "",
    val bairro: String = "",
    val localidade: String = "",
    val uf: String = "",
    @SerialName("erro") val erro: Boolean = false
)

private suspend fun lookupCep(cep: String): ViaCepResponse? {
    return withContext(Dispatchers.IO) {
        runCatching {
            val response = URL("https://viacep.com.br/ws/$cep/json/").readText()
            viaCepJson.decodeFromString<ViaCepResponse>(response)
        }.getOrNull()?.takeUnless { it.erro }
    }
}

private val viaCepJson = Json { ignoreUnknownKeys = true }

private const val LAST_ADDRESS_PREFS = "snack_la_paz_last_address"

private fun DeliveryAddress.hasAnyDeliveryInfo(): Boolean {
    return listOf(fullName, phone, street, number, neighborhood, complement, cep)
        .any { it.isNotBlank() }
}

private fun loadLastDeliveryAddress(context: Context): DeliveryAddress {
    val prefs = context.getSharedPreferences(LAST_ADDRESS_PREFS, Context.MODE_PRIVATE)
    return DeliveryAddress(
        fullName = prefs.getString("fullName", "").orEmpty(),
        phone = prefs.getString("phone", "").orEmpty(),
        street = prefs.getString("street", "").orEmpty(),
        number = prefs.getString("number", "").orEmpty(),
        neighborhood = prefs.getString("neighborhood", "").orEmpty(),
        complement = prefs.getString("complement", "").orEmpty(),
        cep = prefs.getString("cep", "").orEmpty(),
        city = prefs.getString("city", "São Paulo").orEmpty(),
        state = prefs.getString("state", "SP").orEmpty()
    )
}

private fun saveLastDeliveryAddress(context: Context, address: DeliveryAddress) {
    context.getSharedPreferences(LAST_ADDRESS_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString("fullName", address.fullName)
        .putString("phone", address.phone)
        .putString("street", address.street)
        .putString("number", address.number)
        .putString("neighborhood", address.neighborhood)
        .putString("complement", address.complement)
        .putString("cep", address.cep)
        .putString("city", address.city)
        .putString("state", address.state)
        .apply()
}

private fun formatBrazilianPhone(raw: String): String {
    val digits = raw.filter { it.isDigit() }.take(11)
    if (digits.isEmpty()) return ""

    return when {
        digits.length <= 2 -> "(${digits}"
        digits.length <= 6 -> "(${digits.take(2)}) ${digits.drop(2)}"
        digits.length <= 10 -> {
            val area = digits.take(2)
            val prefix = digits.drop(2).take(4)
            val suffix = digits.drop(6)
            "($area) $prefix-$suffix"
        }
        else -> {
            val area = digits.take(2)
            val prefix = digits.drop(2).take(5)
            val suffix = digits.drop(7)
            "($area) $prefix-$suffix"
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = GrayMedium, fontSize = 15.sp)
        Text(text = "Bs ${"%.2f".format(value)}", color = GrayDark, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}
