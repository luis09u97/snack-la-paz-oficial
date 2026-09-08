package com.snacklapaz.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriaDto(
    @SerialName("id_categoria") val idCategoria: Int,
    val nome: String,
    val descricao: String? = null,
    val status: String? = null
)

@Serializable
data class ProdutoDto(
    @SerialName("id_produto") val idProduto: Int,
    @SerialName("id_categoria") val idCategoria: Int? = null,
    val nome: String,
    val descricao: String? = null,
    val preco: Double,
    val estoque: Int = 0,
    val imagem: String? = null,
    val ingredientes: String? = null,
    val status: String? = null,
    @SerialName("data_cadastro") val dataCadastro: String? = null
)

@Serializable
data class UsuarioDto(
    @SerialName("id_usuario") val idUsuario: Int,
    val nome: String,
    val email: String,
    @SerialName("senha_hash") val senhaHash: String,
    val status: String? = null,
    @SerialName("data_cadastro") val dataCadastro: String? = null,
    @SerialName("ultimo_acesso") val ultimoAcesso: String? = null
)

@Serializable
data class PerfilDto(
    @SerialName("id_perfil") val idPerfil: Int,
    val nome: String,
    val descricao: String? = null
)

@Serializable
data class UsuarioPerfilDto(
    @SerialName("id_usuario") val idUsuario: Int,
    @SerialName("id_perfil") val idPerfil: Int
)

@Serializable
data class ClienteDto(
    @SerialName("id_cliente") val idCliente: Int,
    @SerialName("id_usuario") val idUsuario: Int,
    val cpf: String? = null,
    val telefone: String? = null,
    @SerialName("data_nascimento") val dataNascimento: String? = null
)

@Serializable
data class EnderecoDto(
    @SerialName("id_endereco") val idEndereco: Int,
    @SerialName("id_cliente") val idCliente: Int,
    val cep: String? = null,
    val logradouro: String,
    val numero: String,
    val complemento: String? = null,
    val bairro: String,
    val cidade: String,
    val estado: String,
    val tipo: String? = null,
    val principal: Boolean = false
)

@Serializable
data class PedidoDto(
    @SerialName("id_pedido") val idPedido: Int,
    @SerialName("id_cliente") val idCliente: Int,
    @SerialName("id_endereco_entrega") val idEnderecoEntrega: Int,
    @SerialName("data_pedido") val dataPedido: String? = null,
    val status: String,
    @SerialName("valor_frete") val valorFrete: Double = 0.0,
    @SerialName("valor_total") val valorTotal: Double
)

@Serializable
data class ItemPedidoDto(
    @SerialName("id_item") val idItem: Int,
    @SerialName("id_pedido") val idPedido: Int,
    @SerialName("id_produto") val idProduto: Int,
    val quantidade: Int,
    @SerialName("preco_unitario") val precoUnitario: Double,
    val subtotal: Double
)

@Serializable
data class PagamentoDto(
    @SerialName("id_pagamento") val idPagamento: Int,
    @SerialName("id_pedido") val idPedido: Int,
    val metodo: String,
    val valor: Double,
    val status: String,
    @SerialName("data_pagamento") val dataPagamento: String? = null,
    @SerialName("codigo_transacao") val codigoTransacao: String? = null
)
