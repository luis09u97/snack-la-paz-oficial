package com.snacklapaz.app.ui.home.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector
)

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val rating: Float,
    val imageUrl: String,
    val categoryId: String,
    val description: String,
    val ingredients: String,
    var isFavorite: Boolean = false
)

val sampleCategories = listOf(
    Category("1", "Comidas", Icons.Filled.Restaurant),
    Category("2", "Bebidas", Icons.Filled.LocalDrink),
    Category("3", "Lanches", Icons.Filled.LunchDining),
    Category("4", "Doces", Icons.Filled.Cake)
)

val allSampleProducts = listOf(
    Product(
        id = "1",
        name = "Pollo a la Broaster",
        price = 28.0,
        rating = 4.8f,
        imageUrl = "android.resource://com.snacklapaz.app/drawable/pollo_broaster",
        categoryId = "1",
        description = "Frango crocante servido com batatas fritas, bem dourado e suculento.",
        ingredients = "Frango, batata, farinha temperada, alho, cominho, pimenta suave e sal."
    ),
    Product(
        id = "2",
        name = "Salchipapa",
        price = 18.0,
        rating = 4.7f,
        imageUrl = "https://img0.didiglobal.com/static/soda_public/img_69e68a65b600cf47968efa5009fa7f5a.png",
        categoryId = "1",
        description = "Porção generosa de batatas, salsicha grelhada, queijo e molhos.",
        ingredients = "Batata frita, salsicha, queijo, alface, ketchup, maionese e molho especial."
    ),
    Product(
        id = "3",
        name = "Pollo al Espiedo",
        price = 32.0,
        rating = 4.9f,
        imageUrl = "https://cdn.store.link/products/store2705/pd5vbp-chatgpt%20image%203%20ene%202026%2C%2002_00_23%20p.m..png?versionId=eyvTUrCQPohu_ZoGY.4OwEdv9sgxa.Zm",
        categoryId = "1",
        description = "Frango assado inteiro no espeto, servido com batatas e limão.",
        ingredients = "Frango, batata, limão, alho, ervas, páprica, cominho e sal."
    ),
    Product(
        id = "4",
        name = "Pescado Frito",
        price = 30.0,
        rating = 4.6f,
        imageUrl = "https://d1w7312wesee68.cloudfront.net/36l-UnIZMrwc4kY_IRZO9IaGN3QzFdOi-eTGXMHYBUA/resize%3Afit%3A720%3A720/plain/s3%3A/toasttab/restaurants/restaurant-267554000000000000/menu/items/0/item-200000053504977730_1749076470.jpg",
        categoryId = "1",
        description = "Peixe frito crocante servido com batatas e salada fresca.",
        ingredients = "Peixe, batata, alface, tomate, limão, farinha, alho e sal."
    ),
    Product(
        id = "5",
        name = "Sopa de Maní",
        price = 22.0,
        rating = 4.8f,
        imageUrl = "https://jacaranda.com.bo/wp-content/uploads/2025/01/IMG_Jacaranda_Sopa_Mani.webp",
        categoryId = "1",
        description = "Sopa boliviana cremosa de amendoim com batata palha e ervas.",
        ingredients = "Amendoim, carne, batata, arroz ou macarrão, salsa, cebola, alho e especiarias."
    ),
    Product(
        id = "6",
        name = "Pique a lo Macho",
        price = 34.0,
        rating = 4.9f,
        imageUrl = "https://static.wixstatic.com/media/929756_cff315b5f72b4f9c810215e96ba1fb37~mv2.jpg/v1/fill/w_626%2Ch_648%2Cal_c%2Cq_85%2Cusm_0.66_1.00_0.01%2Cenc_avif%2Cquality_auto/929756_cff315b5f72b4f9c810215e96ba1fb37~mv2.jpg",
        categoryId = "1",
        description = "Prato boliviano farto com carne, salsicha, batatas, ovo e molhos.",
        ingredients = "Carne bovina, salsicha, batata, ovo, tomate, cebola, pimentão e molho picante."
    ),
    Product(
        id = "7",
        name = "Silpancho",
        price = 29.0,
        rating = 4.8f,
        imageUrl = "https://images.mrcook.app/recipe-image/019d4647-d948-7024-b7c7-0472a5e479ea/019d4647-dfb5-7c0c-9013-1a4791d2635d?cacheKey=V2VkLCAwMSBBcHIgMjAyNiAwMjoyMTo0NyBHTVQ%3D",
        categoryId = "1",
        description = "Carne empanada fina sobre arroz, batatas, salada e ovo frito.",
        ingredients = "Carne bovina, arroz, batata, ovo, tomate, cebola roxa, farinha de rosca e temperos."
    ),
    Product(
        id = "8",
        name = "Coca Quina",
        price = 6.0,
        rating = 4.5f,
        imageUrl = "https://compro.bo/cdn/shop/files/COKAQUINA330ML_1200x1200.jpg?v=1700956276",
        categoryId = "2",
        description = "Refrigerante boliviano gelado com sabor marcante e tradicional.",
        ingredients = "Água gaseificada, açúcar, extratos vegetais, acidulante e aroma."
    ),
    Product(
        id = "9",
        name = "Simba",
        price = 8.0,
        rating = 4.5f,
        imageUrl = "https://farmacorp.com/cdn/shop/files/909714_1200x1200.jpg?v=1773894260",
        categoryId = "2",
        description = "Refrigerante Simba gelado, ideal para acompanhar lanches bolivianos.",
        ingredients = "Água gaseificada, açúcar, aroma de fruta, acidulante e conservante."
    ),
    Product(
        id = "10",
        name = "Pura Vida",
        price = 8.0,
        rating = 4.4f,
        imageUrl = "https://cdn.shopify.com/s/files/1/0517/5495/9018/files/7771259756798.jpg?v=1769662420",
        categoryId = "2",
        description = "Néctar de fruta Pura Vida, refrescante e bem colorido.",
        ingredients = "Água, polpa de fruta, açúcar, vitamina C, acidulante e aroma."
    ),
    Product(
        id = "11",
        name = "Coca-Cola",
        price = 7.0,
        rating = 4.7f,
        imageUrl = "https://mir-s3-cdn-cf.behance.net/project_modules/fs/c1dd12131268523.61920dae18e81.jpg",
        categoryId = "2",
        description = "Coca-Cola gelada para acompanhar qualquer pedido.",
        ingredients = "Água gaseificada, açúcar, extrato de noz de cola, cafeína, corante caramelo e aroma."
    ),
    Product(
        id = "12",
        name = "Pepsi",
        price = 7.0,
        rating = 4.6f,
        imageUrl = "https://knjaz.rs/wp-content/uploads/2023/02/pepsi-Blue-500mL-Avgust24.png",
        categoryId = "2",
        description = "Pepsi gelada com apresentação clássica de garrafa.",
        ingredients = "Água gaseificada, açúcar, extrato de cola, cafeína, corante caramelo e aroma."
    ),
    Product(
        id = "13",
        name = "Mocochinche",
        price = 6.5,
        rating = 4.7f,
        imageUrl = "https://fsa.bo/productos/22285-01.jpg",
        categoryId = "2",
        description = "Bebida tradicional boliviana feita com pêssego desidratado.",
        ingredients = "Pêssego desidratado, água, açúcar, canela e cravo."
    ),
    Product(
        id = "14",
        name = "Salteña",
        price = 12.0,
        rating = 4.8f,
        imageUrl = "android.resource://com.snacklapaz.app/drawable/saltena",
        categoryId = "3",
        description = "Lanche boliviano assado com massa dourada e recheio suculento.",
        ingredients = "Massa, frango ou carne, batata, ervilha, ovo, azeitona, caldo e especiarias."
    ),
    Product(
        id = "15",
        name = "Tucumana",
        price = 10.0,
        rating = 4.7f,
        imageUrl = "https://d3s8tbcesxr4jm.cloudfront.net/recipe-images/v3/bolivian-tucumanas-meat-filled-pastries/2_medium.jpg",
        categoryId = "3",
        description = "Pastel boliviano frito, crocante e recheado.",
        ingredients = "Massa de trigo, carne moída, ervilha, batata, cebola e especiarias."
    ),
    Product(
        id = "16",
        name = "Bolinho de Arroz",
        price = 7.5,
        rating = 4.4f,
        imageUrl = "https://s2-receitas.glbimg.com/AXCxxU8HC8gwSEYdFf1XfmML5KE%3D/1200x0/filters%3Aformat%28jpeg%29/https%3A/i.s3.glbimg.com/v1/AUTH_1f540e0b94d8437dbbc39d567a1dee68/internal_photos/bs/2024/0/b/OYWhRVSv6fSvLgW0Kr4w/bolinho-arroz-eduardo-sterblitch.jpg",
        categoryId = "3",
        description = "Bolinho frito dourado, crocante por fora e macio por dentro.",
        ingredients = "Arroz, queijo, ovo, farinha, cheiro-verde, alho e sal."
    ),
    Product(
        id = "17",
        name = "Humintas",
        price = 9.0,
        rating = 4.6f,
        imageUrl = "https://www.gastronomiadealtura.com/images/gastronomy/es/7_85F436FF3178_huminta-3.webp",
        categoryId = "3",
        description = "Lanche tradicional de milho envolto na palha.",
        ingredients = "Milho, queijo, leite, manteiga, erva-doce, açúcar e sal."
    ),
    Product(
        id = "18",
        name = "Turrón de Maní",
        price = 6.0,
        rating = 4.5f,
        imageUrl = "android.resource://com.snacklapaz.app/drawable/turron_mani",
        categoryId = "4",
        description = "Doce de amendoim firme, cortado em porções individuais.",
        ingredients = "Amendoim, açúcar, mel, clara de ovo e baunilha."
    ),
    Product(
        id = "19",
        name = "Chicles",
        price = 4.0,
        rating = 4.3f,
        imageUrl = "android.resource://com.snacklapaz.app/drawable/gomas_mascar",
        categoryId = "4",
        description = "Chicles coloridos com visual chamativo para crianças e adultos.",
        ingredients = "Açúcar, goma base, glicose, corantes e aroma tutti-frutti."
    ),
    Product(
        id = "20",
        name = "Gomas",
        price = 5.0,
        rating = 4.3f,
        imageUrl = "https://comoencasahn.com/cdn/shop/files/Screenshot2024-08-09at10.19.01PM.png?v=1723263642",
        categoryId = "4",
        description = "Gomas doces coloridas, macias e com sabor de frutas.",
        ingredients = "Açúcar, xarope de glicose, gelatina, acidulante, corantes e aromas."
    ),
    Product(
        id = "21",
        name = "Chocolates",
        price = 9.0,
        rating = 4.6f,
        imageUrl = "https://img07.shop-pro.jp/PA01361/612/product/140121355_o1.jpg?cmsp_timestamp=20200301093250",
        categoryId = "4",
        description = "Chocolate boliviano com apresentação premium.",
        ingredients = "Cacau, açúcar, manteiga de cacau, leite em pó e baunilha."
    )
)

val sampleFeaturedProducts: List<Product>
    get() = allSampleProducts.take(7)

val samplePopularProducts: List<Product>
    get() = allSampleProducts.drop(7)
