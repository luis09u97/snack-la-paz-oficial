package com.snacklapaz.app.data

import com.snacklapaz.app.data.dto.CategoriaDto
import com.snacklapaz.app.data.dto.ProdutoDto
import com.snacklapaz.app.data.dto.ProdutoWriteDto
import com.snacklapaz.app.ui.home.model.Category
import com.snacklapaz.app.ui.home.model.Product
import com.snacklapaz.app.ui.home.model.iconFromName
import io.github.jan.supabase.postgrest.postgrest

/**
 * Busca categorias e produtos direto do banco Supabase, convertendo os
 * DTOs (formato cru do banco) para os modelos que a interface usa.
 */
class ProductRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getCategories(): List<Category> {
        val dtos = client.postgrest["categorias"]
            .select()
            .decodeList<CategoriaDto>()

        return dtos
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

    private fun ProdutoDto.toProduct(): Product {
        return Product(
            id = idProduto.toString(),
            name = nome,
            price = preco,
            rating = 4.5f,
            imageUrl = imagem.orEmpty(),
            categoryId = idCategoria?.toString().orEmpty(),
            description = descricao.orEmpty(),
            ingredients = ingredientes.orEmpty()
        )
    }
}
