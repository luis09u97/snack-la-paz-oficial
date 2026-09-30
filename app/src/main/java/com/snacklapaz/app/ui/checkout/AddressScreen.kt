package com.snacklapaz.app.ui.checkout

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
                .padding(20.dp)
        ) {
            SnackTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    cartViewModel.updateDraftAddress(currentDraft.copy(fullName = it))
                },
                label = "Nome completo",
                leadingIcon = Icons.Filled.Person
            )
            Spacer(modifier = Modifier.height(14.dp))

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
            Spacer(modifier = Modifier.height(14.dp))

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
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            AddressPreviewCard(
                street = street,
                neighborhood = neighborhood,
                city = city,
                state = state
            )

            Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            OrderSummaryCard(
                subtotal = cartViewModel.subtotal,
                deliveryFee = cartViewModel.deliveryFeeFor(currentDraft),
                total = cartViewModel.totalFor(currentDraft)
            )
        }

        Surface(color = White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
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
private fun AddressPreviewCard(
    street: String,
    neighborhood: String,
    city: String,
    state: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = OrangeLight, modifier = Modifier.size(42.dp)) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = if (street.isBlank()) "Digite o CEP para encontrar o endereço" else street,
                    color = GrayDark,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (neighborhood.isBlank()) {
                        "Rua, bairro e cidade aparecerão automaticamente"
                    } else {
                        "$neighborhood, $city/$state"
                    },
                    color = GrayMedium,
                    fontSize = 13.sp
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
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumo do pedido",
                style = MaterialTheme.typography.titleMedium,
                color = GrayDark
            )
            Spacer(modifier = Modifier.height(10.dp))

            SummaryLine(label = "Subtotal", value = subtotal)
            SummaryLine(label = "Taxa de entrega", value = deliveryFee)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = GrayBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GrayDark)
                Text(
                    text = "Bs ${"%.2f".format(total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OrangePrimary
                )
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
        Text(text = label, color = GrayMedium, fontSize = 14.sp)
        Text(text = "Bs ${"%.2f".format(value)}", color = GrayDark, fontSize = 14.sp)
    }
}
