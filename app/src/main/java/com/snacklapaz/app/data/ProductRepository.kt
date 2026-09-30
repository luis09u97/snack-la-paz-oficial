package com.snacklapaz.app.data

import com.snacklapaz.app.data.dto.CategoriaDto
import com.snacklapaz.app.data.dto.CategoriaWriteDto
import com.snacklapaz.app.data.dto.ProdutoDto
import com.snacklapaz.app.data.dto.ProdutoWriteDto
import com.snacklapaz.app.ui.home.model.Category
import com.snacklapaz.app.ui.home.model.Product
import com.snacklapaz.app.ui.home.model.iconFromName
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Busca categorias e produtos direto do banco Supabase, convertendo os
 * DTOs (formato cru do banco) para os modelos que a interface usa.
 */
class ProductRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getCategories(): List<Category> {
        return getAdminCategories()
            .filter { it.status.isActiveStatus() }
            .sortedBy { it.nome }
            .map { dto ->
                Category(
                    id = dto.idCategoria.toString(),
                    name = dto.nome,
                    icon = iconFromName(dto.nome)
                )
            }
    }

    suspend fun getAdminCategories(): List<CategoriaDto> {
        val dtos = client.postgrest["categorias"]
            .select()
            .decodeList<CategoriaDto>()

        return dtos
            .sortedBy { it.nome }
    }

    suspend fun getProducts(): List<Product> {
        return getAllProducts().filter { product ->
            val dto = productStatusCache[product.id]
            dto == null || dto.isActiveStatus()
        }
    }

    suspend fun getAdminProducts(): List<ProdutoDto> {
        val dtos = client.postgrest["produtos"]
            .select()
            .decodeList<ProdutoDto>()

        return dtos
            .sortedBy { it.nome }
    }

    private var productStatusCache: Map<String, String?> = emptyMap()

    private suspend fun getAllProducts(): List<Product> {
        val dtos = getAdminProducts()
        productStatusCache = dtos.associate { it.idProduto.toString() to it.status }
        return dtos
            .filter { it.estoque > 0 }
            .map { dto -> dto.toProduct() }
    }

    suspend fun searchProducts(query: String, categoryId: String? = null): List<Product> {
        val normalizedQuery = query.trim()
        return getProducts().filter { product ->
            val matchesQuery = normalizedQuery.isBlank() ||
                    product.name.contains(normalizedQuery, ignoreCase = true)
            val matchesCategory = categoryId == null || product.categoryId == categoryId
            matchesQuery && matchesCategory
        }
    }

    private fun String?.isActiveStatus(): Boolean {
        return this == null || equals("ATIVO", ignoreCase = true) || equals("ATIVA", ignoreCase = true)
    }

    suspend fun saveProduct(dto: ProdutoDto) {
        val payload = ProdutoWriteDto(
            idCategoria = dto.idCategoria,
            nome = dto.nome,
            descricao = dto.descricao,
            preco = dto.preco,
            estoque = dto.estoque,
            imagem = dto.imagem,
            ingredientes = dto.ingredientes,
            status = dto.status ?: "ATIVO"
        )
        if (dto.idProduto > 0) {
            client.postgrest["produtos"].update(payload) {
                filter { eq("id_produto", dto.idProduto) }
            }
        } else {
            client.postgrest["produtos"].insert(payload)
        }
    }

    suspend fun updateProductStatus(productId: Int, status: String) {
        client.postgrest["produtos"].update(mapOf("status" to status)) {
            filter { eq("id_produto", productId) }
        }
    }

    suspend fun updateStock(productId: Int, stock: Int) {
        client.postgrest["produtos"].update(mapOf("estoque" to stock)) {
            filter { eq("id_produto", productId) }
        }
    }

    suspend fun saveCategory(dto: CategoriaDto) {
        val payload = CategoriaWriteDto(
            nome = dto.nome,
            descricao = dto.descricao,
            status = dto.status ?: "ATIVA"
        )
        if (dto.idCategoria > 0) {
            client.postgrest["categorias"].update(payload) {
                filter { eq("id_categoria", dto.idCategoria) }
            }
        } else {
            client.postgrest["categorias"].insert(payload)
        }
    }

    suspend fun updateCategoryStatus(categoryId: Int, status: String) {
        client.postgrest["categorias"].update(mapOf("status" to status)) {
            filter { eq("id_categoria", categoryId) }
        }
    }

    suspend fun submitFeedback(productId: Int, rating: Int, comment: String) {
        val authId = client.auth.currentUserOrNull()?.id
        client.postgrest["avaliacoes_produto"].insert(
            ProductFeedbackWriteDto(
                idProduto = productId,
                authId = authId,
                nota = rating.coerceIn(1, 5),
                comentario = comment.takeIf { it.isNotBlank() }
            )
        )
    }

    private fun ProdutoDto.toProduct(): Product {
        val fallbackRating = fallbackRatingFor(idProduto)
        val fallbackReviewCount = fallbackReviewCountFor(idProduto)
        return Product(
            id = idProduto.toString(),
            name = nome,
            price = preco,
            rating = (avaliacaoMedia ?: fallbackRating).toFloat(),
            imageUrl = imagem.orEmpty(),
            categoryId = idCategoria?.toString().orEmpty(),
            description = descricao.orEmpty(),
            ingredients = ingredientes.orEmpty(),
            reviewCount = totalAvaliacoes ?: fallbackReviewCount,
            feedbackHighlight = feedbackDestaque ?: fallbackFeedbackFor(nome)
        )
    }
}

@Serializable
private data class ProductFeedbackWriteDto(
    @SerialName("id_produto") val idProduto: Int,
    @SerialName("auth_id") val authId: String?,
    val nota: Int,
    val comentario: String? = null
)

private fun fallbackRatingFor(productId: Int): Double {
    return when (productId % 6) {
        0 -> 4.9
        1 -> 4.8
        2 -> 4.7
        3 -> 4.6
        4 -> 4.5
        else -> 4.4
    }
}

private fun fallbackReviewCountFor(productId: Int): Int {
    return 18 + (productId * 7 % 64)
}

private fun fallbackFeedbackFor(productName: String): String {
    return "Clientes elogiam o sabor e a apresentação do $productName."
}
