package com.snacklapaz.app.ui.cart

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.snacklapaz.app.ui.cart.model.CartItem
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayLight
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onGoToHomeClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    val items = cartViewModel.items

    if (items.isEmpty()) {
        EmptyState(
            icon = Icons.Outlined.ShoppingCart,
            title = "Seu carrinho está vazio",
            description = "Explore nossos produtos e monte seu pedido.",
            actionLabel = "Ver produtos",
            onActionClick = onGoToHomeClick
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Text(
            text = "Carrinho",
            style = MaterialTheme.typography.headlineMedium,
            color = GrayDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
        ) {
            items(items, key = { it.productId }) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { cartViewModel.increaseQuantity(item.productId) },
                    onDecrease = { cartViewModel.decreaseQuantity(item.productId) },
                    onRemove = { cartViewModel.removeItem(item.productId) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        CartSummary(
            subtotal = cartViewModel.subtotal,
            deliveryFee = cartViewModel.deliveryFee,
            discount = cartViewModel.discount,
            total = cartViewModel.total,
            onContinueClick = onContinueClick
        )
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GrayLight)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = GrayDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bs ${"%.2f".format(item.unitPrice)}",
                    color = OrangePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                QuantityStepper(
                    quantity = item.quantity,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )
            }

            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Remover ${item.name}",
                tint = ErrorRed,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onRemove)
            )
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GrayLight)
    ) {
        StepperButton(icon = Icons.Filled.Remove, contentDescription = "Diminuir quantidade", onClick = onDecrease)
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 12.dp),
            fontWeight = FontWeight.SemiBold,
            color = GrayDark
        )
        StepperButton(icon = Icons.Filled.Add, contentDescription = "Aumentar quantidade", onClick = onIncrease)
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = OrangePrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun CartSummary(
    subtotal: Double,
    deliveryFee: Double,
    discount: Double,
    total: Double,
    onContinueClick: () -> Unit
) {
    Surface(
        color = White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SummaryRow(label = "Subtotal", value = subtotal)
            SummaryRow(label = "Taxa de entrega", value = deliveryFee)
            if (discount > 0) {
                SummaryRow(label = "Desconto", value = -discount, valueColor = OrangePrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.HorizontalDivider(color = GrayBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GrayDark)
                Text(
                    text = "Bs ${"%.2f".format(total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = OrangePrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SnackPrimaryButton(
                text = "Continuar pedido",
                onClick = onContinueClick
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: Double,
    valueColor: androidx.compose.ui.graphics.Color = GrayDark
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = GrayMedium, fontSize = 14.sp)
        Text(text = "Bs ${"%.2f".format(value)}", color = valueColor, fontSize = 14.sp)
    }
}