package com.snacklapaz.app.ui.home.model

private val recommendationIdsByProductId = mapOf(
    "1" to listOf("8", "13", "18"),
    "2" to listOf("9", "11", "19"),
    "3" to listOf("12", "10", "17"),
    "4" to listOf("13", "10", "18"),
    "5" to listOf("14", "13", "17"),
    "6" to listOf("8", "12", "18"),
    "7" to listOf("9", "13", "14"),
    "8" to listOf("1", "6", "14"),
    "9" to listOf("2", "15", "20"),
    "10" to listOf("3", "4", "17"),
    "11" to listOf("2", "1", "19"),
    "12" to listOf("6", "3", "18"),
    "13" to listOf("14", "5", "7"),
    "14" to listOf("13", "8", "18"),
    "15" to listOf("9", "13", "20"),
    "16" to listOf("8", "11", "21"),
    "17" to listOf("10", "13", "18"),
    "18" to listOf("8", "13", "14"),
    "19" to listOf("11", "12", "16"),
    "20" to listOf("10", "9", "15"),
    "21" to listOf("13", "8", "17")
)

private val recommendationNamesByProductName = mapOf(
    "pollo a la broaster" to listOf("Coca Quina", "Mocochinche", "Turrón de Maní"),
    "salchipapa" to listOf("Simba", "Coca-Cola", "Chicles"),
    "pollo al espiedo" to listOf("Pepsi", "Pura Vida", "Humintas"),
    "pescado frito" to listOf("Mocochinche", "Pura Vida", "Turrón de Maní"),
    "sopa de maní" to listOf("Salteña", "Mocochinche", "Humintas"),
    "pique a lo macho" to listOf("Coca Quina", "Pepsi", "Turrón de Maní"),
    "silpancho" to listOf("Simba", "Mocochinche", "Salteña"),
    "coca quina" to listOf("Pollo a la Broaster", "Pique a lo Macho", "Salteña"),
    "simba" to listOf("Salchipapa", "Tucumana", "Gomas"),
    "pura vida" to listOf("Pollo al Espiedo", "Pescado Frito", "Humintas"),
    "coca-cola" to listOf("Salchipapa", "Pollo a la Broaster", "Chicles"),
    "pepsi" to listOf("Pique a lo Macho", "Pollo al Espiedo", "Turrón de Maní"),
    "mocochinche" to listOf("Salteña", "Sopa de Maní", "Silpancho"),
    "salteña" to listOf("Mocochinche", "Coca Quina", "Turrón de Maní"),
    "tucumana" to listOf("Simba", "Mocochinche", "Gomas"),
    "bolinho de arroz" to listOf("Coca Quina", "Coca-Cola", "Chocolates"),
    "humintas" to listOf("Pura Vida", "Mocochinche", "Turrón de Maní"),
    "turrón de maní" to listOf("Coca Quina", "Mocochinche", "Salteña"),
    "chicles" to listOf("Coca-Cola", "Pepsi", "Bolinho de Arroz"),
    "gomas" to listOf("Pura Vida", "Simba", "Tucumana"),
    "chocolates" to listOf("Mocochinche", "Coca Quina", "Humintas")
)

fun List<Product>.recommendationsFor(product: Product): List<Product> {
    val byId = recommendationIdsByProductId[product.id]
        ?.mapNotNull { recommendedId -> firstOrNull { it.id == recommendedId } }
        .orEmpty()

    val byName = recommendationNamesByProductName[product.name.lowercase()]
        ?.mapNotNull { recommendedName ->
            firstOrNull { it.name.equals(recommendedName, ignoreCase = true) }
        }
        .orEmpty()

    val curated = (byId + byName)
        .distinctBy { it.id }
        .filterNot { it.id == product.id }

    if (curated.isNotEmpty()) return curated.take(3)

    return fallbackRecommendationsFor(product)
}

private fun List<Product>.fallbackRecommendationsFor(product: Product): List<Product> {
    val preferredCategories = when (product.categoryId) {
        "1" -> listOf("2", "4", "3")
        "2" -> listOf("1", "3", "4")
        "3" -> listOf("2", "4", "1")
        "4" -> listOf("2", "3", "1")
        else -> listOf("2", "3", "4", "1")
    }

    return preferredCategories
        .flatMap { categoryId -> filter { it.categoryId == categoryId && it.id != product.id } }
        .distinctBy { it.id }
        .take(3)
}
