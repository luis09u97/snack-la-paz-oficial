package com.snacklapaz.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.snacklapaz.app.data.OrderRepository
import com.snacklapaz.app.data.ProductRepository
import com.snacklapaz.app.data.dto.CategoriaDto
import com.snacklapaz.app.data.dto.ClienteDto
import com.snacklapaz.app.data.dto.ItemPedidoDto
import com.snacklapaz.app.data.dto.PagamentoDto
import com.snacklapaz.app.data.dto.PedidoDto
import com.snacklapaz.app.data.dto.ProdutoDto
import com.snacklapaz.app.ui.common.UiState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class AdminDashboardStats(
    val salesTotal: Double = 0.0,
    val ordersCount: Int = 0,
    val customersCount: Int = 0,
    val productsCount: Int = 0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val pendingPaymentsCount: Int = 0,
    val preparingOrdersCount: Int = 0,
    val averageTicket: Double = 0.0
)

enum class AdminPeriod(val label: String) {
    TODAY("Hoje"),
    LAST_7_DAYS("Últimos 7 dias"),
    THIS_MONTH("Este mês"),
    LAST_30_DAYS("Últimos 30 dias")
}

data class AdminReportStats(
    val totalSold: Double = 0.0,
    val ordersCount: Int = 0,
    val averageTicket: Double = 0.0,
    val bestSellers: List<AdminBestSeller> = emptyList(),
    val lowStockProducts: List<ProdutoDto> = emptyList(),
    val ordersByStatus: Map<String, Int> = emptyMap()
)

data class AdminBestSeller(
    val productId: Int,
    val productName: String,
    val quantity: Int
)

class AdminViewModel(
    private val productRepository: ProductRepository = ProductRepository(),
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    var productsState by mutableStateOf<UiState<List<ProdutoDto>>>(UiState.Loading)
        private set

    var ordersState by mutableStateOf<UiState<List<PedidoDto>>>(UiState.Loading)
        private set

    var categoriesState by mutableStateOf<UiState<List<CategoriaDto>>>(UiState.Loading)
        private set

    var customersState by mutableStateOf<UiState<List<ClienteDto>>>(UiState.Loading)
        private set

    var paymentsState by mutableStateOf<UiState<List<PagamentoDto>>>(UiState.Loading)
        private set

    var reportsState by mutableStateOf<UiState<AdminReportStats>>(UiState.Loading)
        private set

    var dashboardState by mutableStateOf<UiState<AdminDashboardStats>>(UiState.Loading)
        private set

    var selectedPeriod by mutableStateOf(AdminPeriod.LAST_30_DAYS)
        private set

    init {
        refreshAll()
    }

    fun refreshAll() {
        loadProducts()
        loadOrders()
        loadCategories()
        loadCustomers()
        loadPayments()
        loadReports()
        loadDashboard()
    }

    fun updatePeriod(period: AdminPeriod) {
        selectedPeriod = period
        loadDashboard()
        loadReports()
    }

    fun loadProducts() {
        viewModelScope.launch {
            productsState = UiState.Loading
            productsState = try {
                UiState.Success(productRepository.getAdminProducts())
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar produtos.")
            }
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            ordersState = UiState.Loading
            ordersState = try {
                UiState.Success(orderRepository.getAdminOrders())
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar pedidos.")
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            categoriesState = UiState.Loading
            categoriesState = try {
                UiState.Success(productRepository.getAdminCategories())
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar categorias.")
            }
        }
    }

    fun loadCustomers() {
        viewModelScope.launch {
            customersState = UiState.Loading
            customersState = try {
                UiState.Success(orderRepository.getAdminCustomers())
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar clientes.")
            }
        }
    }

    fun loadPayments() {
        viewModelScope.launch {
            paymentsState = UiState.Loading
            paymentsState = try {
                UiState.Success(orderRepository.getAdminPayments())
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar pagamentos.")
            }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            dashboardState = UiState.Loading
            dashboardState = try {
                val products = productRepository.getAdminProducts()
                val orders = orderRepository.getAdminOrders()
                val payments = orderRepository.getAdminPayments()
                val filteredOrders = orders.filterByPeriod(selectedPeriod)
                UiState.Success(
                    AdminDashboardStats(
                        salesTotal = filteredOrders.sumOf { it.valorTotal },
                        ordersCount = filteredOrders.size,
                        customersCount = orderRepository.countClientes(),
                        productsCount = products.size,
                        lowStockCount = products.count { it.estoque in 1..5 },
                        outOfStockCount = products.count { it.estoque <= 0 },
                        pendingPaymentsCount = payments.count { it.status.equals("PENDENTE", ignoreCase = true) },
                        preparingOrdersCount = orders.count { it.status in setOf("CONFIRMADO", "PREPARANDO", "EM_PREPARACAO") },
                        averageTicket = if (filteredOrders.isEmpty()) 0.0 else filteredOrders.sumOf { it.valorTotal } / filteredOrders.size
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar o dashboard.")
            }
        }
    }

    fun loadReports() {
        viewModelScope.launch {
            reportsState = UiState.Loading
            reportsState = try {
                val products = productRepository.getAdminProducts()
                val orders = orderRepository.getAdminOrders()
                val items = orderRepository.getAdminOrderItems()
                val productsById = products.associateBy { it.idProduto }
                val filteredOrders = orders.filterByPeriod(selectedPeriod)
                val filteredOrderIds = filteredOrders.map { it.idPedido }.toSet()
                val filteredItems = items.filter { it.idPedido in filteredOrderIds }
                UiState.Success(
                    AdminReportStats(
                        totalSold = filteredOrders.sumOf { it.valorTotal },
                        ordersCount = filteredOrders.size,
                        averageTicket = if (filteredOrders.isEmpty()) 0.0 else filteredOrders.sumOf { it.valorTotal } / filteredOrders.size,
                        bestSellers = filteredItems
                            .groupBy { it.idProduto }
                            .mapValues { entry -> entry.value.sumOf { it.quantidade } }
                            .toList()
                            .sortedByDescending { it.second }
                            .take(5)
                            .map { (productId, quantity) ->
                                AdminBestSeller(
                                    productId = productId,
                                    productName = productsById[productId]?.nome ?: "Produto sem nome",
                                    quantity = quantity
                                )
                            },
                        lowStockProducts = products.filter { it.estoque <= 5 }.sortedBy { it.estoque },
                        ordersByStatus = filteredOrders.groupingBy { it.status }.eachCount()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar relatórios.")
            }
        }
    }

    fun saveProduct(product: ProdutoDto) {
        viewModelScope.launch {
            try {
                productRepository.saveProduct(product)
                loadProducts()
                loadDashboard()
            } catch (e: Exception) {
                e.printStackTrace()
                productsState = UiState.Error("Não foi possível salvar o produto.")
            }
        }
    }

    fun toggleProduct(product: ProdutoDto) {
        viewModelScope.launch {
            try {
                val newStatus = if (product.status.equals("ATIVO", ignoreCase = true)) "PAUSADO" else "ATIVO"
                productRepository.updateProductStatus(product.idProduto, newStatus)
                loadProducts()
                loadDashboard()
            } catch (e: Exception) {
                e.printStackTrace()
                productsState = UiState.Error("Não foi possível atualizar o produto.")
            }
        }
    }

    fun updateStock(product: ProdutoDto, stock: Int) {
        viewModelScope.launch {
            try {
                productRepository.updateStock(product.idProduto, stock.coerceAtLeast(0))
                loadProducts()
                loadDashboard()
            } catch (e: Exception) {
                e.printStackTrace()
                productsState = UiState.Error("Não foi possível atualizar o estoque.")
            }
        }
    }

    fun saveCategory(category: CategoriaDto) {
        viewModelScope.launch {
            try {
                productRepository.saveCategory(category)
                loadCategories()
            } catch (e: Exception) {
                e.printStackTrace()
                categoriesState = UiState.Error("Não foi possível salvar a categoria.")
            }
        }
    }

    fun toggleCategory(category: CategoriaDto) {
        viewModelScope.launch {
            try {
                val newStatus = if (category.status.equals("ATIVA", ignoreCase = true)) "INATIVA" else "ATIVA"
                productRepository.updateCategoryStatus(category.idCategoria, newStatus)
                loadCategories()
            } catch (e: Exception) {
                e.printStackTrace()
                categoriesState = UiState.Error("Não foi possível atualizar a categoria.")
            }
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            try {
                orderRepository.updateOrderStatus(orderId, status)
                loadOrders()
                loadDashboard()
            } catch (e: Exception) {
                e.printStackTrace()
                ordersState = UiState.Error("Não foi possível atualizar o status.")
            }
        }
    }

    fun updatePaymentStatus(paymentId: Int, status: String) {
        viewModelScope.launch {
            try {
                orderRepository.updatePaymentStatus(paymentId, status)
                loadPayments()
                loadDashboard()
            } catch (e: Exception) {
                e.printStackTrace()
                paymentsState = UiState.Error("Não foi possível atualizar o pagamento.")
            }
        }
    }

    private fun List<PedidoDto>.filterByPeriod(period: AdminPeriod): List<PedidoDto> {
        val today = LocalDate.now()
        val startDate = when (period) {
            AdminPeriod.TODAY -> today
            AdminPeriod.LAST_7_DAYS -> today.minusDays(6)
            AdminPeriod.THIS_MONTH -> today.withDayOfMonth(1)
            AdminPeriod.LAST_30_DAYS -> today.minusDays(29)
        }
        return filter { pedido ->
            val date = pedido.dataPedido?.toLocalDateOrNull()
            date == null || !date.isBefore(startDate)
        }
    }

    private fun String.toLocalDateOrNull(): LocalDate? {
        return runCatching {
            LocalDateTime.parse(substringBefore("+"), DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate()
        }.getOrNull() ?: runCatching {
            LocalDate.parse(take(10))
        }.getOrNull()
    }
}
