package com.snacklapaz.app.ui.home.model

private const val MinGalleryImages = 3

private val blockedGalleryImages = setOf(
    "https://boliviaesturismo.com/wp-content/uploads/2020/04/humintas.jpg",
    "https://boliviaesturismo.com/wp-content/uploads/2020/04/sopa-de-mani.jpg",
    "https://boliviaesturismo.com/wp-content/uploads/2020/04/tucumanas.jpg",
    "https://cdn.pixabay.com/photo/2017/08/06/04/00/gummy-bears-2589581_1280.jpg",
    "https://compro.bo/cdn/shop/products/COKAQUINA2L_1200x1200.jpg",
    "https://i.ytimg.com/vi/du7EkhuDk7k/maxresdefault.jpg",
    "https://i.ytimg.com/vi/SiFRfqh0imc/maxresdefault.jpg",
    "https://i.ytimg.com/vi/XfJXwf5JV4g/maxresdefault.jpg",
    "https://recetasdebolivia.com/wp-content/uploads/2022/02/Receta-de-Empanadas-Fritas-768x576.jpg",
    "https://rostisserialetentazioni.es/wp-content/uploads/2021/03/Pollo-a-last.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a0/Refresco_de_mocochinchi.jpg/1200px-Refresco_de_mocochinchi.jpg",
    "https://www.arcor.com/ar/landings/turron-mani/img/turron.png",
    "https://www.chocolateselceibo.com/wp-content/uploads/2020/04/chocolate-amargo.jpg",
    "https://www.coca-cola.com/content/dam/onexp/us/en/brands/coca-cola-original/coca-cola-original-12oz.png",
    "https://www.comedera.com/wp-content/uploads/2022/05/pescado-frito-con-tostones.jpg",
    "https://www.farmacorp.com/cdn/shop/files/7771205000158_1200x1200.jpg",
    "https://www.lostiempos.com/sites/default/files/styles/noticia_detalle/public/media_imagen/2019/9/15/pique_macho.jpg",
    "https://www.receiteria.com.br/wp-content/uploads/bolinho-de-arroz-facil-730x480.jpg",
    "https://www.recetasnestle.com.bo/sites/default/files/srh_recipes/01717ffad53cce0a705f62768ee4f2b9.jpg",
    "https://www.recetasnestle.com.bo/sites/default/files/srh_recipes/fcbd230f6b00313d6b1a8416f1f630b8.jpg"
)

private val galleryImagesByProductId = mapOf(
    "1" to listOf(
        "android.resource://com.snacklapaz.app/drawable/pollo_broaster",
        "https://images.rappi.com/restaurants_background/po-1772731990154.png",
        "https://static.wixstatic.com/media/6b11ee_eac29b71d9b2405d82685601661faa47~mv2.png/v1/fill/w_777%2Ch_432%2Cal_c%2Cq_85%2Cenc_avif%2Cquality_auto/6b11ee_eac29b71d9b2405d82685601661faa47~mv2.png",
        "https://i.ytimg.com/vi/du7EkhuDk7k/maxresdefault.jpg"
    ),
    "2" to listOf(
        "https://img0.didiglobal.com/static/soda_public/img_69e68a65b600cf47968efa5009fa7f5a.png",
        "https://cafeteriaplayablanca.es/public/media/menu/papas/salchipapa3.jpg",
        "https://www.arise-app.com/images/dishes/es/salchipapa-especial-con-tocineta-y-queso-1cwrsz.webp",
        "https://www.recetasnestle.com.bo/sites/default/files/srh_recipes/01717ffad53cce0a705f62768ee4f2b9.jpg"
    ),
    "3" to listOf(
        "https://cdn.store.link/products/store2705/pd5vbp-chatgpt%20image%203%20ene%202026%2C%2002_00_23%20p.m..png?versionId=eyvTUrCQPohu_ZoGY.4OwEdv9sgxa.Zm",
        "https://elcomercio.pe/resizer/v2/ZJ5LQRJARRAO3DIBR4EXJQXIAU.jpg?auth=739d4fc499b4fd241f1d51e311c8fc6d471d9ab1487469629a22674be94b0ad9&height=408&quality=75&smart=true&width=612",
        "https://rostisserialetentazioni.es/wp-content/uploads/2021/03/Pollo-a-last.jpg",
        "https://statics.productodeaqui.com/images/bonplat/pollastre-a-l-ast.jpg"
    ),
    "4" to listOf(
        "https://d1w7312wesee68.cloudfront.net/36l-UnIZMrwc4kY_IRZO9IaGN3QzFdOi-eTGXMHYBUA/resize%3Afit%3A720%3A720/plain/s3%3A/toasttab/restaurants/restaurant-267554000000000000/menu/items/0/item-200000053504977730_1749076470.jpg",
        "https://tb-static.uber.com/prod/image-proc/processed_images/6700623308b4a566c0344dfb50e5f47e/bc9c318a9c96996e2d990faf2b0c65f6.jpeg",
        "https://tofuu.getjusto.com/orioneat-local/resized2/6SRAbYkF68tevXtnF-2400-x.webp",
        "https://www.comedera.com/wp-content/uploads/2022/05/pescado-frito-con-tostones.jpg"
    ),
    "5" to listOf(
        "https://jacaranda.com.bo/wp-content/uploads/2025/01/IMG_Jacaranda_Sopa_Mani.webp",
        "https://estaticos.unitel.bo/binrepository/1186x791/-7c13/1200d630/none/246276540/KFPR/imagen-aiease-1754521696675_101-12698095_20250806231329.jpg",
        "https://easyhotel.com.bo/wp-content/uploads/2024/03/Sopa-de-Mani.jpg",
        "https://boliviaesturismo.com/wp-content/uploads/2020/04/sopa-de-mani.jpg"
    ),
    "6" to listOf(
        "https://static.wixstatic.com/media/929756_cff315b5f72b4f9c810215e96ba1fb37~mv2.jpg/v1/fill/w_626%2Ch_648%2Cal_c%2Cq_85%2Cusm_0.66_1.00_0.01%2Cenc_avif%2Cquality_auto/929756_cff315b5f72b4f9c810215e96ba1fb37~mv2.jpg",
        "https://www.assai.com.br/sites/default/files/viagem-gastronomica-assai-tradicao-bolivia-pique-a-lo-macho.jpg",
        "https://4.bp.blogspot.com/-zY7ZbEkBygY/T_gn9EmfEcI/AAAAAAAAC7w/QGqBO_y53zI/s1600/Pique-a-lo-Macho.jpg",
        "https://www.lostiempos.com/sites/default/files/styles/noticia_detalle/public/media_imagen/2019/9/15/pique_macho.jpg"
    ),
    "7" to listOf(
        "https://images.mrcook.app/recipe-image/019d4647-d948-7024-b7c7-0472a5e479ea/019d4647-dfb5-7c0c-9013-1a4791d2635d?cacheKey=V2VkLCAwMSBBcHIgMjAyNiAwMjoyMTo0NyBHTVQ%3D",
        "https://storyteller.travel/wp-content/uploads/2021/02/Bolivian-dishes-Silpancho.jpg",
        "https://hqdavida.com.br/storage/2025/07/silpancho-cochabambino-receita-boliviana.png"
    ),
    "8" to listOf(
        "https://compro.bo/cdn/shop/files/COKAQUINA330ML_1200x1200.jpg?v=1700956276",
        "https://compro.bo/cdn/shop/files/COKAQUINA750ML_grande.jpg?v=1725999661",
        "https://compro.bo/cdn/shop/files/COKAQUINA3L_1200x1200.jpg?v=1700699466",
        "https://compro.bo/cdn/shop/products/COKAQUINA2L_1200x1200.jpg",
        "https://www.farmacorp.com/cdn/shop/files/7771205000158_1200x1200.jpg"
    ),
    "9" to listOf(
        "https://farmacorp.com/cdn/shop/files/909714_1200x1200.jpg?v=1773894260",
        "https://www.fidalga.com/cdn/shop/products/7771605000483.jpg?v=1746460318",
        "https://cdn.shopify.com/s/files/1/0480/9424/9119/products/7771605000063.jpg?v=1656732919"
    ),
    "10" to listOf(
        "https://cdn.shopify.com/s/files/1/0517/5495/9018/files/7771259756798.jpg?v=1769662420",
        "https://www.fsa.bo/productos/16598-01.jpg",
        "https://cdn.shopify.com/s/files/1/0480/9424/9119/products/7771259751069_grande.jpg?v=1656732645"
    ),
    "11" to listOf(
        "https://mir-s3-cdn-cf.behance.net/project_modules/fs/c1dd12131268523.61920dae18e81.jpg",
        "https://cdn.gosteisalvei.com/coca-cola-mini-pet-200ml-png-preview.png",
        "https://acdn-us.mitiendanube.com/stores/861/458/products/6-b11e414a12f15174d017561499897214-1024-1024.webp",
        "https://www.superdia.pe/cdn/shop/files/COCA_COLA_600ML_super_dia.jpg?v=1764435319&width=1000",
        "https://www.coca-cola.com/content/dam/onexp/us/en/brands/coca-cola-original/coca-cola-original-12oz.png"
    ),
    "12" to listOf(
        "https://knjaz.rs/wp-content/uploads/2023/02/pepsi-Blue-500mL-Avgust24.png",
        "https://eurosuper.vtexassets.com/arquivos/ids/185891/7702192741749.jpg?v=638585728692030000",
        "https://www.pepsi.com/s3fs-public/2023-01/pepsi-zero-sugar.png"
    ),
    "13" to listOf(
        "https://fsa.bo/productos/22285-01.jpg",
        "https://www.recetas.com.bo/sites/default/files/2024-09/mocochinchi.jpg",
        "https://www.willflyforfood.net/wp-content/uploads/2021/07/bolivian-food-mocochinchi.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a0/Refresco_de_mocochinchi.jpg/1200px-Refresco_de_mocochinchi.jpg",
        "https://4.bp.blogspot.com/-6ui7nEZOPzM/UDd3QgjxFzI/AAAAAAAAX44/FBEMO_tsj58/s1600/rollies-002edited.jpg"
    ),
    "14" to listOf(
        "android.resource://com.snacklapaz.app/drawable/saltena",
        "https://server-bucket-2022.s3.amazonaws.com/travelapp/9fda255e1e7b771c5815090fcefdc802_1716290442895.jpeg",
        "https://media.mdzol.com/adjuntos/373/migration/u/fotografias/m/2023/8/29/f768x1-1467308_1467435_5050.png",
        "https://www.recetasnestle.com.bo/sites/default/files/srh_recipes/fcbd230f6b00313d6b1a8416f1f630b8.jpg"
    ),
    "15" to listOf(
        "https://d3s8tbcesxr4jm.cloudfront.net/recipe-images/v3/bolivian-tucumanas-meat-filled-pastries/2_medium.jpg",
        "https://salzona.com/static/products/tucumanas.png",
        "https://commons.wikimedia.org/wiki/Special:Redirect/file/Tucumanas%20bolivianas.jpg",
        "https://recetasdebolivia.com/wp-content/uploads/2022/02/Receta-de-Empanadas-Fritas-768x576.jpg",
        "https://boliviaesturismo.com/wp-content/uploads/2020/04/tucumanas.jpg"
    ),
    "16" to listOf(
        "https://s2-receitas.glbimg.com/AXCxxU8HC8gwSEYdFf1XfmML5KE%3D/1200x0/filters%3Aformat%28jpeg%29/https%3A/i.s3.glbimg.com/v1/AUTH_1f540e0b94d8437dbbc39d567a1dee68/internal_photos/bs/2024/0/b/OYWhRVSv6fSvLgW0Kr4w/bolinho-arroz-eduardo-sterblitch.jpg",
        "https://recipesblob.oetker.com.br/assets/1fb36e43dfa74ff2abf844f21c39c8a3/1440x580/bolinho-de-arroz.jpg",
        "https://static.itdg.com.br/images/1200-675/49dc1a832774c48f9935c335508861c4/320105-original.jpg",
        "https://www.receiteria.com.br/wp-content/uploads/bolinho-de-arroz-facil-730x480.jpg",
        "https://i.ytimg.com/vi/XfJXwf5JV4g/maxresdefault.jpg"
    ),
    "17" to listOf(
        "https://www.gastronomiadealtura.com/images/gastronomy/es/7_85F436FF3178_huminta-3.webp",
        "https://storyteller.travel/wp-content/uploads/2021/02/Bolivian-foods-768x548.jpg",
        "https://static.wixstatic.com/media/12c138_b73236860ff34d86b9c2337d10280824~mv2.jpg/v1/fill/w_980%2Ch_980%2Cal_c%2Cq_85%2Cusm_0.66_1.00_0.01%2Cenc_avif%2Cquality_auto/12c138_b73236860ff34d86b9c2337d10280824~mv2.jpg",
        "https://wongfood.vtexassets.com/arquivos/ids/315415/44989-01-7180.jpg?v=637034112756630000",
        "https://boliviaesturismo.com/wp-content/uploads/2020/04/humintas.jpg",
        "https://i.ytimg.com/vi/SiFRfqh0imc/maxresdefault.jpg"
    ),
    "18" to listOf(
        "android.resource://com.snacklapaz.app/drawable/turron_mani",
        "https://www.theargentinianmarket.com.au/cdn/shop/files/Turronx1-04.png?v=1699222434",
        "https://cdn11.bigcommerce.com/s-3stx4pub31/images/stencil/608x608/products/2124/32196/Arcor_Turrn_de_Man_Classic_Peanut_Christmas_Nougat_280_g_9.87_oz__37453.1732034605.jpg?c=3",
        "https://maxiconsumo.com/media/catalog/product/cache/dee42de555cd0e5c071d2951391ded3b/8/0/8062_1732076445673d639dee6134.73614535.jpg",
        "https://www.arcor.com/ar/landings/turron-mani/img/turron.png"
    ),
    "19" to listOf(
        "android.resource://com.snacklapaz.app/drawable/gomas_mascar",
        "https://golosinastrome.com/productos/jpg/26-%20CHICLE%20BOLITA%20GRANEL.jpg",
        "https://www.caramelospaco.com/679-large_default_2x/bola-de-chicle.jpg"
    ),
    "20" to listOf(
        "https://comoencasahn.com/cdn/shop/files/Screenshot2024-08-09at10.19.01PM.png?v=1723263642",
        "https://mercaldas.vtexassets.com/arquivos/ids/1324097/Gomas-TRULULU-neon-x70-g_129860.jpg?v=638478395782070000",
        "https://productosalimenticiosdiana.vtexassets.com/arquivos/ids/155495-440-440?aspect=true&height=440&v=638521765720730000&width=440",
        "https://cdn.pixabay.com/photo/2017/08/06/04/00/gummy-bears-2589581_1280.jpg"
    ),
    "21" to listOf(
        "https://img07.shop-pro.jp/PA01361/612/product/140121355_o1.jpg?cmsp_timestamp=20200301093250",
        "https://images.squarespace-cdn.com/content/v1/53f1fa4ee4b0b87c659cab68/36f5b892-992e-4f71-b9bb-9933f07c8a78/El%2BCeibo%2BBolivia.jpg",
        "https://image1.shopserve.jp/shop.thecitybakery.jp/pic-labo/20220322_273.jpg",
        "https://www.chocolateselceibo.com/wp-content/uploads/2020/04/chocolate-amargo.jpg"
    )
)

fun Product.galleryImages(): List<String> {
    val availableImages = (galleryImagesByProductId[id].orEmpty() + imageUrl)
        .filter { it.isNotBlank() }
        .filterNot { it in blockedGalleryImages }
        .distinct()

    if (availableImages.isEmpty()) return emptyList()
    return availableImages.take(MinGalleryImages)
}
