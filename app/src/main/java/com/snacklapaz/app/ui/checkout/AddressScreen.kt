package com.snacklapaz.app.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.cart.model.DeliveryAddress
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTextField
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun AddressScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderConfirmed: (orderNumber: String, total: Double) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var complement by remember { mutableStateOf("") }

    val isFormValid = fullName.isNotBlank() && phone.isNotBlank() &&
            street.isNotBlank() && number.isNotBlank() && neighborhood.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = "Endereço de entrega", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            SnackTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Nome completo",
                leadingIcon = Icons.Filled.Person
            )
            Spacer(modifier = Modifier.height(14.dp))

            SnackTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Telefone",
                leadingIcon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(14.dp))

            SnackTextField(
                value = street,
                onValueChange = { street = it },
                label = "Rua / Avenida",
                leadingIcon = Icons.Filled.LocationOn
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SnackTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = "Número",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                SnackTextField(
                    value = neighborhood,
                    onValueChange = { neighborhood = it },
                    label = "Bairro",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            SnackTextField(
                value = complement,
                onValueChange = { complement = it },
                label = "Complemento (opcional)",
                leadingIcon = Icons.Filled.Home
            )

            Spacer(modifier = Modifier.height(24.dp))

            OrderSummaryCard(
                subtotal = cartViewModel.subtotal,
                deliveryFee = cartViewModel.deliveryFee,
                total = cartViewModel.total
            )
        }

        Surface(color = White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                SnackPrimaryButton(
                    text = "Finalizar pedido",
                    enabled = isFormValid,
                    onClick = {
                        val address = DeliveryAddress(
                            fullName = fullName,
                            phone = phone,
                            street = street,
                            number = number,
                            neighborhood = neighborhood,
                            complement = complement
                        )
                        val total = cartViewModel.total
                        val orderNumber = cartViewModel.placeOrder(address)
                        onOrderConfirmed(orderNumber, total)
                    }
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