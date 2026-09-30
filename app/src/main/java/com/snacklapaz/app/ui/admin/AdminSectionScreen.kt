package com.snacklapaz.app.ui.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snacklapaz.app.data.dto.PedidoDto
import com.snacklapaz.app.data.dto.ProdutoDto
import com.snacklapaz.app.ui.common.UiState
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.White

@Composable
fun AdminSectionScreen(
    sectionId: String,
    title: String,
    icon: ImageVector,
    onBackClick: () -> Unit,
    adminViewModel: AdminViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        SnackTopBar(title = title, onBackClick = onBackClick)
        when (sectionId) {
            "products" -> ProductsAdmin(adminViewModel = adminViewModel)
            "orders" -> OrdersAdmin(adminViewModel = adminViewModel)
            else -> PlaceholderAdmin(title = title, icon = icon)
        }
    }
}

@Composable
private fun ProductsAdmin(adminViewModel: AdminViewModel) {
    var editing by remember { mutableStateOf<ProdutoDto?>(null) }
    var creating by remember { mutableStateOf(false) }

    if (creating || editing != null) {
        ProductDialog(
            product = editing,
            onDismiss = {
                creating = false
                editing = null
            },
            onSave = {
                adminViewModel.saveProduct(it)
                creating = false
                editing = null
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { creating = true },
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            Text(text = "Novo produto", modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))

        when (val state = adminViewModel.productsState) {
            UiState.Loading -> Text(text = "Carregando produtos...", color = GrayMedium)
            is UiState.Error -> EmptyState(
                icon = Icons.Filled.Remove,
                title = "Produtos indisponíveis",
                description = state.message,
                actionLabel = "Tentar novamente",
                onActionClick = { adminViewModel.loadProducts() }
            )
            is UiState.Success -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.data, key = { it.idProduto }) { product ->
                    ProductAdminCard(
                        product = product,
                        onEdit = { editing = product },
                        onToggle = { adminViewModel.toggleProduct(product) },
                        onStockChange = { adminViewModel.updateStock(product, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductAdminCard(
    product: ProdutoDto,
    onEdit: () -> Unit,
    onToggle: () -> Unit,
    onStockChange: (Int) -> Unit
) {
    Surface(shape = RoundedCornerShape(14.dp), color = White, shadowElevation = 1.dp) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = product.nome, fontWeight = FontWeight.Bold, color = GrayDark)
                    Text(text = "Bs ${"%.2f".format(product.preco)} • Estoque ${product.estoque}", color = GrayMedium)
                    Text(text = "Status: ${product.status ?: "ATIVO"}", color = OrangePrimary)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onToggle) {
                    Text(if (product.status.equals("ATIVO", true)) "Pausar" else "Ativar")
                }
                TextButton(onClick = { onStockChange(product.estoque + 1) }) { Text("+ estoque") }
                TextButton(onClick = { onStockChange(product.estoque - 1) }) { Text("- estoque") }
            }
        }
    }
}

@Composable
private fun ProductDialog(
    product: ProdutoDto?,
    onDismiss: () -> Unit,
    onSave: (ProdutoDto) -> Unit
) {
    var name by remember(product) { mutableStateOf(product?.nome.orEmpty()) }
    var category by remember(product) { mutableStateOf(product?.idCategoria?.toString() ?: "1") }
    var price by remember(product) { mutableStateOf(product?.preco?.toString().orEmpty()) }
    var stock by remember(product) { mutableStateOf(product?.estoque?.toString() ?: "0") }
    var image by remember(product) { mutableStateOf(product?.imagem.orEmpty()) }
    var description by remember(product) { mutableStateOf(product?.descricao.orEmpty()) }
    var ingredients by remember(product) { mutableStateOf(product?.ingredientes.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Novo produto" else "Editar produto") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoria") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Preço") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Estoque") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = image, onValueChange = { image = it }, label = { Text("Imagem") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") })
                OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Ingredientes") })
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        ProdutoDto(
                            idProduto = product?.idProduto ?: 0,
                            idCategoria = category.toIntOrNull(),
                            nome = name,
                            descricao = description,
                            preco = price.replace(",", ".").toDoubleOrNull() ?: 0.0,
                            estoque = stock.toIntOrNull() ?: 0,
                            imagem = image,
                            ingredientes = ingredients,
                            status = product?.status ?: "ATIVO"
                        )
                    )
                },
                enabled = name.isNotBlank()
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun OrdersAdmin(adminViewModel: AdminViewModel) {
    val statuses = listOf("RECEBIDO", "CONFIRMADO", "PREPARANDO", "PRONTO", "SAIU_PARA_ENTREGA", "ENTREGUE")
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (val state = adminViewModel.ordersState) {
            UiState.Loading -> Text(text = "Carregando pedidos...", color = GrayMedium)
            is UiState.Error -> EmptyState(
                icon = Icons.Filled.Remove,
                title = "Pedidos indisponíveis",
                description = state.message,
                actionLabel = "Tentar novamente",
                onActionClick = { adminViewModel.loadOrders() }
            )
            is UiState.Success -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.data, key = { it.idPedido }) { order ->
                    OrderAdminCard(
                        order = order,
                        statuses = statuses,
                        onStatusClick = { status -> adminViewModel.updateOrderStatus(order.idPedido, status) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderAdminCard(
    order: PedidoDto,
    statuses: List<String>,
    onStatusClick: (String) -> Unit
) {
    Surface(shape = RoundedCornerShape(14.dp), color = White, shadowElevation = 1.dp) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Pedido nº ${order.idPedido}", fontWeight = FontWeight.Bold, color = GrayDark)
            Text(text = "Bs ${"%.2f".format(order.valorTotal)} • ${order.status}", color = GrayMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            ) {
                statuses.take(3).forEach { status ->
                    TextButton(onClick = { onStatusClick(status) }) { Text(status.lowercase().replace("_", " ")) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                statuses.drop(3).forEach { status ->
                    TextButton(onClick = { onStatusClick(status) }) { Text(status.lowercase().replace("_", " ")) }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderAdmin(title: String, icon: ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(56.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Gerenciamento de $title", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Essa seção ainda não faz parte da prioridade atual.", color = GrayMedium, textAlign = TextAlign.Center)
    }
}
