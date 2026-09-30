package com.snacklapaz.app.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.snacklapaz.app.data.OrderRepository
import com.snacklapaz.app.ui.cart.model.OrderSummary
import com.snacklapaz.app.ui.common.UiState
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val repository: OrderRepository = OrderRepository()
) : ViewModel() {

    var ordersState by mutableStateOf<UiState<List<OrderSummary>>>(UiState.Loading)
        private set

    var selectedOrder by mutableStateOf<OrderSummary?>(null)
        private set

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            ordersState = UiState.Loading
            ordersState = try {
                UiState.Success(repository.getCurrentUserOrders())
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error("Não foi possível carregar seus pedidos.")
            }
        }
    }

    fun selectOrder(order: OrderSummary) {
        selectedOrder = order
    }
}
