package com.snacklapaz.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.snacklapaz.app.data.OrderRepository
import com.snacklapaz.app.data.ProductRepository
import com.snacklapaz.app.data.dto.PedidoDto
import com.snacklapaz.app.data.dto.ProdutoDto
import com.snacklapaz.app.ui.common.UiState
import kotlinx.coroutines.launch

data class AdminDashboardStats(
    val salesTotal: Double = 0.0,
    val ordersCount: Int = 0,
    val customersCount: Int = 0,
    val lowStockCount: Int = 0
)

class AdminViewModel(
    private val productRepository: ProductRepository = ProductRepository(),
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    var productsState by mutableStateOf<UiState<List<ProdutoDto>>>(UiState.Loading)
        private set

    var ordersState by mutableStateOf<UiState<List<PedidoDto>>>(UiState.Loading)
        private set

    var dashboardState by mutableStateOf<UiState<AdminDashboardStats>>(UiState.Loading)
        private set

    init {
        refreshAll()
    }

    fun refreshAll() {
        loadProducts()
        loadOrders()
        loadDashboard()
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

    fun loadDashboard() {
        viewModelScope.launch {
            dashboardState = UiState.Loading
            dashboardState = try {
                val products = productRepository.getAdminProducts()
                val orders = orderRepository.getAdminOrders()
                UiState.Success(
                    AdminDashboardStats(
                        salesTotal = orders.sumOf { it.valorTotal },
                        ordersCount = orders.size,
                        customersCount = orderRepository.countClientes(),
                        lowStockCount = products.count { it.estoque <= 5 }
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar o dashboard.")
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
}
