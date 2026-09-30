package com.snacklapaz.app.data

import com.snacklapaz.app.data.dto.ClienteDto
import com.snacklapaz.app.data.dto.EnderecoDto
import com.snacklapaz.app.data.dto.ItemPedidoDto
import com.snacklapaz.app.data.dto.PedidoStatusUpdateDto
import com.snacklapaz.app.data.dto.PedidoDto
import com.snacklapaz.app.data.dto.ProdutoDto
import com.snacklapaz.app.ui.cart.model.CartItem
import com.snacklapaz.app.ui.cart.model.DeliveryAddress
import com.snacklapaz.app.ui.cart.model.OrderSummary
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Grava o pedido de verdade no Supabase: endereço -> pedido -> itens.
 */
class OrderRepository {

    private val client = SupabaseClientProvider.client

    suspend fun placeOrder(
        address: DeliveryAddress,
        items: List<CartItem>,
        deliveryFee: Double,
        total: Double,
        paymentMethod: String
    ): Int {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("Usuário não está logado")

        val cliente = client.postgrest["clientes"]
            .select { filter { eq("auth_id", userId) } }
            .decodeSingle<ClienteDto>()

        val endereco = client.postgrest["enderecos"]
            .insert(
                NovoEnderecoDto(
                    idCliente = cliente.idCliente,
                    cep = address.cep.ifBlank { null },
                    logradouro = address.street,
                    numero = address.number,
                    complemento = address.complement.ifBlank { null },
                    bairro = address.neighborhood,
                    cidade = address.city.ifBlank { "São Paulo" },
                    estado = address.state.ifBlank { "SP" }
                )
            ) { select() }
            .decodeSingle<EnderecoDto>()

        val pedido = client.postgrest["pedidos"]
            .insert(
                NovoPedidoDto(
                    idCliente = cliente.idCliente,
                    idEnderecoEntrega = endereco.idEndereco,
                    status = "RECEBIDO",
                    valorFrete = deliveryFee,
                    valorTotal = total
                )
            ) { select() }
            .decodeSingle<PedidoDto>()

        val itensParaInserir = items.map { item ->
            NovoItemPedidoDto(
                idPedido = pedido.idPedido,
                idProduto = item.productId.toInt(),
                quantidade = item.quantity,
                precoUnitario = item.unitPrice,
                subtotal = item.unitPrice * item.quantity
            )
        }
        client.postgrest["itens_pedido"].insert(itensParaInserir)

        client.postgrest["pagamentos"].insert(
            NovoPagamentoDto(
                idPedido = pedido.idPedido,
                metodo = paymentMethod,
                valor = total,
                status = "PENDENTE"
            )
        )

        return pedido.idPedido
    }

    suspend fun getCurrentUserOrders(): List<OrderSummary> {
        val cliente = getCurrentCliente()
        val pedidos = client.postgrest["pedidos"]
            .select { filter { eq("id_cliente", cliente.idCliente) } }
            .decodeList<PedidoDto>()
            .sortedByDescending { it.idPedido }

        return pedidos.map { pedido -> pedido.toOrderSummary() }
    }

    suspend fun getOrderById(orderId: Int): OrderSummary? {
        return client.postgrest["pedidos"]
            .select { filter { eq("id_pedido", orderId) } }
            .decodeSingleOrNull<PedidoDto>()
            ?.toOrderSummary()
    }

    suspend fun getAdminOrders(): List<PedidoDto> {
        return client.postgrest["pedidos"]
            .select()
            .decodeList<PedidoDto>()
            .sortedByDescending { it.idPedido }
    }

    suspend fun updateOrderStatus(orderId: Int, status: String) {
        client.postgrest["pedidos"].update(PedidoStatusUpdateDto(status)) {
            filter { eq("id_pedido", orderId) }
        }
    }

    suspend fun countClientes(): Int {
        return client.postgrest["clientes"].select().decodeList<ClienteDto>().size
    }

    private suspend fun getCurrentCliente(): ClienteDto {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("Usuário não está logado")
        return client.postgrest["clientes"]
            .select { filter { eq("auth_id", userId) } }
            .decodeSingle<ClienteDto>()
    }

    private suspend fun PedidoDto.toOrderSummary(): OrderSummary {
        val itens = client.postgrest["itens_pedido"]
            .select { filter { eq("id_pedido", idPedido) } }
            .decodeList<ItemPedidoDto>()
        val produtos = client.postgrest["produtos"]
            .select()
            .decodeList<ProdutoDto>()
            .associateBy { it.idProduto }
        val endereco = client.postgrest["enderecos"]
            .select { filter { eq("id_endereco", idEnderecoEntrega) } }
            .decodeSingleOrNull<EnderecoDto>()
        val pagamento = client.postgrest["pagamentos"]
            .select { filter { eq("id_pedido", idPedido) } }
            .decodeSingleOrNull<PagamentoPedidoDto>()

        val cartItems = itens.map { item ->
            val produto = produtos[item.idProduto]
            CartItem(
                productId = item.idProduto.toString(),
                name = produto?.nome ?: "Produto ${item.idProduto}",
                imageUrl = produto?.imagem.orEmpty(),
                unitPrice = item.precoUnitario,
                quantity = item.quantidade
            )
        }
        val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }

        return OrderSummary(
            orderNumber = idPedido.toString(),
            items = cartItems,
            subtotal = subtotal,
            deliveryFee = valorFrete,
            discount = 0.0,
            total = valorTotal,
            address = DeliveryAddress(
                street = endereco?.logradouro.orEmpty(),
                number = endereco?.numero.orEmpty(),
                neighborhood = endereco?.bairro.orEmpty(),
                complement = endereco?.complemento.orEmpty(),
                cep = endereco?.cep.orEmpty(),
                city = endereco?.cidade ?: "São Paulo",
                state = endereco?.estado ?: "SP"
            ),
            paymentMethod = pagamento?.metodo ?: "Pagamento não informado",
            dateTimeMillis = 0L,
            status = status
        )
    }
}

@Serializable
data class NovoEnderecoDto(
    @SerialName("id_cliente") val idCliente: Int,
    val cep: String? = null,
    val logradouro: String,
    val numero: String,
    val complemento: String? = null,
    val bairro: String,
    val cidade: String,
    val estado: String
)

@Serializable
data class NovoPedidoDto(
    @SerialName("id_cliente") val idCliente: Int,
    @SerialName("id_endereco_entrega") val idEnderecoEntrega: Int,
    val status: String,
    @SerialName("valor_frete") val valorFrete: Double,
    @SerialName("valor_total") val valorTotal: Double
)

@Serializable
data class NovoPagamentoDto(
    @SerialName("id_pedido") val idPedido: Int,
    val metodo: String,
    val valor: Double,
    val status: String
)

@Serializable
data class PagamentoPedidoDto(
    @SerialName("id_pagamento") val idPagamento: Int,
    @SerialName("id_pedido") val idPedido: Int,
    val metodo: String,
    val valor: Double,
    val status: String
)

@Serializable
data class NovoItemPedidoDto(
    @SerialName("id_pedido") val idPedido: Int,
    @SerialName("id_produto") val idProduto: Int,
    val quantidade: Int,
    @SerialName("preco_unitario") val precoUnitario: Double,
    val subtotal: Double
)
