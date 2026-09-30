package com.snacklapaz.app.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snacklapaz.app.data.dto.CategoriaDto
import com.snacklapaz.app.data.dto.ClienteDto
import com.snacklapaz.app.data.dto.PagamentoDto
import com.snacklapaz.app.data.dto.PedidoDto
import com.snacklapaz.app.data.dto.ProdutoDto
import com.snacklapaz.app.ui.common.UiState
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.components.SnackAsyncImage
import com.snacklapaz.app.ui.components.SnackTopBar
import com.snacklapaz.app.ui.theme.CreamBackground
import com.snacklapaz.app.ui.theme.ErrorRed
import com.snacklapaz.app.ui.theme.GrayBorder
import com.snacklapaz.app.ui.theme.GrayDark
import com.snacklapaz.app.ui.theme.GrayLight
import com.snacklapaz.app.ui.theme.GrayMedium
import com.snacklapaz.app.ui.theme.OrangeLight
import com.snacklapaz.app.ui.theme.OrangePrimary
import com.snacklapaz.app.ui.theme.SuccessGreen
import com.snacklapaz.app.ui.theme.WarningAmber
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
            "products" -> ProductsAdmin(adminViewModel)
            "inventory" -> InventoryAdmin(adminViewModel)
            "orders" -> OrdersAdmin(adminViewModel)
            "payments" -> PaymentsAdmin(adminViewModel)
            "customers" -> CustomersAdmin(adminViewModel)
            "categories" -> CategoriesAdmin(adminViewModel)
            "reports" -> ReportsAdmin(adminViewModel)
            "promotions", "activities", "settings" -> MissingSchemaAdmin(title, icon)
            else -> MissingSchemaAdmin(title, icon)
        }
    }
}

@Composable
private fun ProductsAdmin(adminViewModel: AdminViewModel) {
    var editing by remember { mutableStateOf<ProdutoDto?>(null) }
    var creating by remember { mutableStateOf(false) }
    var pendingToggle by remember { mutableStateOf<ProdutoDto?>(null) }

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

    pendingToggle?.let { product ->
        ConfirmDialog(
            title = if (product.isActive()) "Pausar produto?" else "Ativar produto?",
            text = "Deseja alterar o status de ${product.nome}?",
            onConfirm = {
                adminViewModel.toggleProduct(product)
                pendingToggle = null
            },
            onDismiss = { pendingToggle = null }
        )
    }

    AdminListScaffold(
        title = "Produtos do catálogo",
        subtitle = "Crie, edite, pause e mantenha os itens que aparecem na loja.",
        actionLabel = "Novo produto",
        onActionClick = { creating = true }
    ) {
        when (val state = adminViewModel.productsState) {
            UiState.Loading -> item { LoadingText("Carregando produtos...") }
            is UiState.Error -> adminErrorItem(state.message) { adminViewModel.loadProducts() }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    adminEmptyItem("Nenhum produto cadastrado.")
                } else {
                    items(state.data, key = { it.idProduto }) { product ->
                        ProductAdminCard(
                            product = product,
                            onEdit = { editing = product },
                            onToggle = { pendingToggle = product },
                            onStockChange = { adminViewModel.updateStock(product, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryAdmin(adminViewModel: AdminViewModel) {
    AdminListScaffold(
        title = "Controle de estoque",
        subtitle = "Produtos zerados somem do catálogo do cliente. Estoque nunca fica negativo."
    ) {
        when (val state = adminViewModel.productsState) {
            UiState.Loading -> item { LoadingText("Carregando estoque...") }
            is UiState.Error -> adminErrorItem(state.message) { adminViewModel.loadProducts() }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    adminEmptyItem("Nenhum produto para controlar.")
                } else {
                    items(state.data.sortedBy { it.estoque }, key = { it.idProduto }) { product ->
                        InventoryCard(
                            product = product,
                            onStockChange = { adminViewModel.updateStock(product, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersAdmin(adminViewModel: AdminViewModel) {
    val statuses = listOf("RECEBIDO", "CONFIRMADO", "PREPARANDO", "SAIU_PARA_ENTREGA", "ENTREGUE", "CANCELADO")
    var pendingChange by remember { mutableStateOf<Pair<PedidoDto, String>?>(null) }

    pendingChange?.let { (order, status) ->
        ConfirmDialog(
            title = "Alterar status?",
            text = "Deseja alterar o pedido nº ${order.idPedido} para ${status.humanStatus()}?",
            onConfirm = {
                adminViewModel.updateOrderStatus(order.idPedido, status)
                pendingChange = null
            },
            onDismiss = { pendingChange = null }
        )
    }

    AdminListScaffold(
        title = "Pedidos da loja",
        subtitle = "Acompanhe pedidos e avance o status da operação."
    ) {
        when (val state = adminViewModel.ordersState) {
            UiState.Loading -> item { LoadingText("Carregando pedidos...") }
            is UiState.Error -> adminErrorItem(state.message) { adminViewModel.loadOrders() }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    adminEmptyItem("Nenhum pedido encontrado.")
                } else {
                    items(state.data, key = { it.idPedido }) { order ->
                        OrderAdminCard(
                            order = order,
                            statuses = statuses,
                            onStatusClick = { status -> pendingChange = order to status }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentsAdmin(adminViewModel: AdminViewModel) {
    val statuses = listOf("PENDENTE", "APROVADO", "RECUSADO", "CANCELADO", "REEMBOLSADO")
    var pendingChange by remember { mutableStateOf<Pair<PagamentoDto, String>?>(null) }

    pendingChange?.let { (payment, status) ->
        ConfirmDialog(
            title = "Alterar pagamento?",
            text = "Deseja marcar o pagamento nº ${payment.idPagamento} como ${status.humanStatus()}?",
            onConfirm = {
                adminViewModel.updatePaymentStatus(payment.idPagamento, status)
                pendingChange = null
            },
            onDismiss = { pendingChange = null }
        )
    }

    AdminListScaffold(
        title = "Pagamentos",
        subtitle = "Gerencie somente status e valores. Nenhum dado sensível de cartão é armazenado."
    ) {
        when (val state = adminViewModel.paymentsState) {
            UiState.Loading -> item { LoadingText("Carregando pagamentos...") }
            is UiState.Error -> adminErrorItem(state.message) { adminViewModel.loadPayments() }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    adminEmptyItem("Nenhum pagamento encontrado.")
                } else {
                    items(state.data, key = { it.idPagamento }) { payment ->
                        PaymentAdminCard(payment = payment, statuses = statuses, onStatusClick = { pendingChange = payment to it })
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomersAdmin(adminViewModel: AdminViewModel) {
    AdminListScaffold(
        title = "Clientes",
        subtitle = "Consulta administrativa básica, sem senhas ou dados sensíveis."
    ) {
        when (val state = adminViewModel.customersState) {
            UiState.Loading -> item { LoadingText("Carregando clientes...") }
            is UiState.Error -> adminErrorItem(state.message) { adminViewModel.loadCustomers() }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    adminEmptyItem("Nenhum cliente encontrado.")
                } else {
                    items(state.data, key = { it.idCliente }) { customer ->
                        CustomerAdminCard(customer)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriesAdmin(adminViewModel: AdminViewModel) {
    var editing by remember { mutableStateOf<CategoriaDto?>(null) }
    var creating by remember { mutableStateOf(false) }

    if (creating || editing != null) {
        CategoryDialog(
            category = editing,
            onDismiss = {
                creating = false
                editing = null
            },
            onSave = {
                adminViewModel.saveCategory(it)
                creating = false
                editing = null
            }
        )
    }

    AdminListScaffold(
        title = "Categorias",
        subtitle = "Organize as famílias do cardápio: comidas, bebidas, lanches e doces.",
        actionLabel = "Nova categoria",
        onActionClick = { creating = true }
    ) {
        when (val state = adminViewModel.categoriesState) {
            UiState.Loading -> item { LoadingText("Carregando categorias...") }
            is UiState.Error -> adminErrorItem(state.message) { adminViewModel.loadCategories() }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    adminEmptyItem("Nenhuma categoria cadastrada.")
                } else {
                    items(state.data, key = { it.idCategoria }) { category ->
                        CategoryAdminCard(
                            category = category,
                            onEdit = { editing = category },
                            onToggle = { adminViewModel.toggleCategory(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportsAdmin(adminViewModel: AdminViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Relatórios", color = GrayDark, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            AdminPeriod.entries.forEach { period ->
                FilterChip(
                    selected = adminViewModel.selectedPeriod == period,
                    onClick = { adminViewModel.updatePeriod(period) },
                    label = { Text(period.label, fontSize = 12.sp) }
                )
            }
        }
        when (val state = adminViewModel.reportsState) {
            UiState.Loading -> LoadingText("Carregando relatórios...")
            is UiState.Error -> AdminErrorBlock(state.message) { adminViewModel.loadReports() }
            is UiState.Success -> {
                val report = state.data
                if (report.ordersCount == 0 && report.lowStockProducts.isEmpty()) {
                    AdminEmptyBlock("Nenhum dado disponível para este período.")
                }
                ReportMetric("Total vendido", "Bs ${"%.2f".format(report.totalSold)}", Icons.Filled.CreditCard)
                ReportMetric("Pedidos", report.ordersCount.toString(), Icons.Filled.ReceiptLong)
                ReportMetric("Ticket médio", "Bs ${"%.2f".format(report.averageTicket)}", Icons.Filled.CheckCircle)
                AdminCard {
                    Text("Mais vendidos", color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (report.bestSellers.isEmpty()) {
                        Text("Nenhum item vendido no período.", color = GrayMedium, modifier = Modifier.padding(top = 8.dp))
                    } else {
                        report.bestSellers.forEachIndexed { index, item ->
                            Text("${index + 1}. Produto #${item.first} - ${item.second} unidade(s)", color = GrayDark, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
                AdminCard {
                    Text("Pedidos por status", color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (report.ordersByStatus.isEmpty()) {
                        Text("Nenhum pedido no período.", color = GrayMedium, modifier = Modifier.padding(top = 8.dp))
                    } else {
                        report.ordersByStatus.forEach { (status, count) ->
                            Text("${status.humanStatus()}: $count", color = GrayDark, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
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
    AdminCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SnackAsyncImage(
                model = product.imagem,
                contentDescription = product.nome,
                modifier = Modifier
                    .size(74.dp)
                    .background(GrayLight, RoundedCornerShape(16.dp))
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(product.nome, fontWeight = FontWeight.Bold, color = GrayDark, fontSize = 18.sp, lineHeight = 22.sp)
                Text("Bs ${"%.2f".format(product.preco)}", color = OrangePrimary, fontWeight = FontWeight.Bold)
                Text("Estoque: ${product.estoque} • ${product.status ?: "ATIVO"}", color = stockColor(product.estoque), fontSize = 13.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
            AdminSmallButton("Editar", onEdit)
            AdminSmallButton(if (product.isActive()) "Pausar" else "Ativar", onToggle)
            AdminSmallButton("+", { onStockChange(product.estoque + 1) })
            AdminSmallButton("-", { onStockChange((product.estoque - 1).coerceAtLeast(0)) })
        }
    }
}

@Composable
private fun InventoryCard(product: ProdutoDto, onStockChange: (Int) -> Unit) {
    AdminCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = stockColor(product.estoque).copy(alpha = 0.13f), modifier = Modifier.size(54.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Inventory2, contentDescription = null, tint = stockColor(product.estoque), modifier = Modifier.size(28.dp))
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(product.nome, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(stockLabel(product.estoque), color = stockColor(product.estoque), fontWeight = FontWeight.Bold)
            }
            AdminSmallButton("-", { onStockChange((product.estoque - 1).coerceAtLeast(0)) })
            AdminSmallButton("+", { onStockChange(product.estoque + 1) })
        }
    }
}

@Composable
private fun OrderAdminCard(order: PedidoDto, statuses: List<String>, onStatusClick: (String) -> Unit) {
    AdminCard {
        Text("Pedido nº ${order.idPedido}", fontWeight = FontWeight.Bold, color = GrayDark, fontSize = 18.sp)
        Text("Cliente #${order.idCliente} • Bs ${"%.2f".format(order.valorTotal)}", color = GrayMedium)
        StatusBadge(order.status)
        FlowButtons(statuses) { status ->
            AdminSmallButton(status.humanStatus(), { onStatusClick(status) })
        }
    }
}

@Composable
private fun PaymentAdminCard(payment: PagamentoDto, statuses: List<String>, onStatusClick: (String) -> Unit) {
    AdminCard {
        Text("Pagamento nº ${payment.idPagamento}", color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Pedido nº ${payment.idPedido} • ${payment.metodo} • Bs ${"%.2f".format(payment.valor)}", color = GrayMedium)
        StatusBadge(payment.status)
        FlowButtons(statuses) { status ->
            AdminSmallButton(status.humanStatus(), { onStatusClick(status) })
        }
    }
}

@Composable
private fun CustomerAdminCard(customer: ClienteDto) {
    AdminCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = OrangeLight, modifier = Modifier.size(54.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.People, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(28.dp))
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("Cliente #${customer.idCliente}", color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Telefone: ${customer.telefone ?: "não informado"}", color = GrayMedium)
                Text(if (customer.isAdmin) "Administrador" else "Cliente", color = if (customer.isAdmin) OrangePrimary else GrayMedium)
            }
        }
    }
}

@Composable
private fun CategoryAdminCard(category: CategoriaDto, onEdit: () -> Unit, onToggle: () -> Unit) {
    AdminCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Category, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(32.dp))
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(category.nome, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(category.descricao ?: "Sem descrição", color = GrayMedium)
                Text("Status: ${category.status ?: "ATIVA"}", color = OrangePrimary)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
            AdminSmallButton("Editar", onEdit)
            AdminSmallButton(if (category.status.equals("ATIVA", true)) "Desativar" else "Ativar", onToggle)
        }
    }
}

@Composable
private fun ProductDialog(product: ProdutoDto?, onDismiss: () -> Unit, onSave: (ProdutoDto) -> Unit) {
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
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminTextField(name, { name = it }, "Nome")
                AdminTextField(category, { category = it.filter(Char::isDigit) }, "Categoria", KeyboardType.Number)
                AdminTextField(price, { price = it }, "Preço", KeyboardType.Decimal)
                AdminTextField(stock, { stock = it.filter(Char::isDigit) }, "Estoque", KeyboardType.Number)
                AdminTextField(image, { image = it }, "Imagem")
                AdminTextField(description, { description = it }, "Descrição")
                AdminTextField(ingredients, { ingredients = it }, "Ingredientes")
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
private fun CategoryDialog(category: CategoriaDto?, onDismiss: () -> Unit, onSave: (CategoriaDto) -> Unit) {
    var name by remember(category) { mutableStateOf(category?.nome.orEmpty()) }
    var description by remember(category) { mutableStateOf(category?.descricao.orEmpty()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Nova categoria" else "Editar categoria") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminTextField(name, { name = it }, "Nome")
                AdminTextField(description, { description = it }, "Descrição")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        CategoriaDto(
                            idCategoria = category?.idCategoria ?: 0,
                            nome = name,
                            descricao = description,
                            status = category?.status ?: "ATIVA"
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
private fun AdminListScaffold(
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(title, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 25.sp, lineHeight = 30.sp)
            Text(subtitle, color = GrayMedium, fontSize = 14.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 4.dp))
            if (actionLabel != null && onActionClick != null) {
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Text(actionLabel, modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
                }
            }
        }
        content()
    }
}

@Composable
private fun AdminCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, GrayBorder.copy(alpha = 0.65f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun AdminSmallButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text, color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun FlowButtons(values: List<String>, item: @Composable (String) -> Unit) {
    Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        values.chunked(3).forEach { rowValues ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                rowValues.forEach { item(it) }
            }
        }
    }
}

@Composable
private fun AdminTextField(value: String, onValueChange: (String) -> Unit, label: String, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StatusBadge(status: String) {
    val color = when {
        status.equals("ENTREGUE", true) || status.equals("APROVADO", true) -> SuccessGreen
        status.equals("CANCELADO", true) || status.equals("RECUSADO", true) -> ErrorRed
        status.equals("PENDENTE", true) -> WarningAmber
        else -> OrangePrimary
    }
    Surface(shape = RoundedCornerShape(999.dp), color = color.copy(alpha = 0.13f), modifier = Modifier.padding(top = 8.dp)) {
        Text(status.humanStatus(), color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
private fun ReportMetric(label: String, value: String, icon: ImageVector) {
    AdminCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(32.dp))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(value, color = GrayDark, fontWeight = FontWeight.Bold, fontSize = 23.sp)
                Text(label, color = GrayMedium, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun MissingSchemaAdmin(title: String, icon: ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, color = GrayDark)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Essa área precisa de tabela própria no Supabase para funcionar com dados reais. Não coloquei valores fictícios aqui.",
            color = GrayMedium,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ConfirmDialog(title: String, text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Confirmar", color = OrangePrimary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun LoadingText(text: String) {
    Text(text = text, color = GrayMedium, modifier = Modifier.padding(18.dp))
}

private fun LazyListScope.adminErrorItem(message: String, onRetry: () -> Unit) {
    item {
        AdminErrorBlock(message, onRetry)
    }
}

private fun LazyListScope.adminEmptyItem(message: String) {
    item {
        AdminEmptyBlock(message)
    }
}

@Composable
private fun AdminErrorBlock(message: String, onRetry: () -> Unit) {
    EmptyState(
        icon = Icons.Filled.Warning,
        title = "Não foi possível carregar",
        description = message,
        actionLabel = "Tentar novamente",
        onActionClick = onRetry
    )
}

@Composable
private fun AdminEmptyBlock(message: String) {
    EmptyState(
        icon = Icons.Filled.Remove,
        title = "Nenhum dado disponível",
        description = message
    )
}

private fun ProdutoDto.isActive(): Boolean {
    return status == null || status.equals("ATIVO", ignoreCase = true)
}

private fun stockLabel(stock: Int): String {
    return when {
        stock <= 0 -> "Sem estoque"
        stock <= 5 -> "Estoque baixo: $stock"
        else -> "Estoque normal: $stock"
    }
}

private fun stockColor(stock: Int): Color {
    return when {
        stock <= 0 -> ErrorRed
        stock <= 5 -> WarningAmber
        else -> SuccessGreen
    }
}

private fun String.humanStatus(): String {
    return lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
}
