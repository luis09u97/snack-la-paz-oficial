package com.snacklapaz.app.data

import com.snacklapaz.app.data.dto.CategoriaDto
import com.snacklapaz.app.data.dto.ProdutoDto
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
        val dtos = client.postgrest["produtos"]
            .select()
            .decodeList<ProdutoDto>()

        return dtos
            .filter { it.status.isActiveStatus() && it.estoque > 0 }
            .map { dto ->
                Product(
                    id = dto.idProduto.toString(),
                    name = dto.nome,
                    price = dto.preco,
                    rating = 4.5f, // avaliação real virá da tabela "avaliacoes" futuramente
                    imageUrl = dto.imagem.orEmpty(),
                    categoryId = dto.idCategoria?.toString().orEmpty(),
                    description = dto.descricao.orEmpty(),
                    ingredients = dto.ingredientes.orEmpty()
                )
            }
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
}
