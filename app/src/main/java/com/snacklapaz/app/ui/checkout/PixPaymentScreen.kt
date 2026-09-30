package com.snacklapaz.app.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.components.SnackPrimaryButton
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.SuccessGreenLight
import com.snacklapaz.app.ui.theme.White

@Composable
fun PixPaymentScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onOrderConfirmed: (orderNumber: String, total: Double) -> Unit
) {
    val address = cartViewModel.draftAddress
    val total = cartViewModel.totalFor(address)
    val pixPayload = remember(total) {
        PixPayloadGenerator.create(amount = total, txId = "SNACKLAPAZ")
    }
    val qrBitmap = remember(pixPayload) { QrCodeGenerator.createBitmap(pixPayload) }
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = "Pix", onBackClick = onBackClick)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, OrangeLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(50.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Pix,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Pix seguro do Snack La Paz",
                                color = GrayDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Confira o valor antes de confirmar.",
                                color = GrayMedium,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CreamBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(text = "Valor do Pix", color = GrayMedium, fontSize = 12.sp)
                            Text(
                                text = "Bs ${"%.2f".format(total)}",
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                            Text(
                                text = "Recebedor: Luis Fernando Quispe Mamani",
                                color = GrayDark,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = White,
                        border = BorderStroke(1.dp, GrayBorder),
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(292.dp)
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code Pix",
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TrustBadge(text = "QR Code", modifier = Modifier.weight(1f))
                        TrustBadge(text = "Copia e cola", modifier = Modifier.weight(1f))
                        TrustBadge(text = "Chave Pix", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CreamBackground,
                        border = BorderStroke(1.dp, GrayBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = pixPayload,
                            color = GrayDark,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (copied) SuccessGreenLight else OrangeLight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(pixPayload))
                                copied = true
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 13.dp)
                        ) {
                            Icon(
                                imageVector = if (copied) Icons.Filled.VerifiedUser else Icons.Filled.ContentCopy,
                                contentDescription = null,
                                tint = if (copied) SuccessGreen else OrangePrimary,
                                modifier = Modifier.size(19.dp)
                            )
                            Text(
                                text = if (copied) "Código Pix copiado" else "Copiar código Pix",
                                color = if (copied) SuccessGreen else OrangePrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            if (cartViewModel.placeOrderError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = cartViewModel.placeOrderError.orEmpty(),
                    color = ErrorRed,
                    fontSize = 13.sp
                )
            }
        }

        Surface(color = White, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                SnackPrimaryButton(
                    text = if (cartViewModel.isPlacingOrder) "Enviando..." else "Confirmar pedido com Pix",
                    enabled = !cartViewModel.isPlacingOrder,
                    onClick = {
                        cartViewModel.placeOrder(address, "Pix") { orderNumber ->
                            onOrderConfirmed(orderNumber, total)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TrustBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = OrangeLight,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = OrangePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 7.dp)
        )
    }
}
