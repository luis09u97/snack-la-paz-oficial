begin transaction;

alter table produtos add column if not exists ingredientes text;

insert into categorias (id_categoria, nome, descricao, status)
values
    (1, 'Comidas', 'Pratos bolivianos completos e porções tradicionais', 'ATIVA'),
    (2, 'Bebidas', 'Refrigerantes, néctares e bebidas tradicionais da Bolívia', 'ATIVA'),
    (3, 'Lanches', 'Salteñas, tucumanas, humintas e lanches bolivianos', 'ATIVA'),
    (4, 'Doces', 'Doces, chicles, gomas e chocolates', 'ATIVA')
on conflict (id_categoria) do update set
    nome = excluded.nome,
    descricao = excluded.descricao,
    status = excluded.status;

insert into produtos (id_produto, id_categoria, nome, descricao, preco, estoque, imagem, ingredientes, status)
values
    (1, 1, 'Pollo a la Broaster', 'Frango crocante servido com batatas fritas, bem dourado e suculento.', 28.00, 30, 'android.resource://com.snacklapaz.app/drawable/pollo_broaster', 'Frango, batata, farinha temperada, alho, cominho, pimenta suave e sal.', 'ATIVO'),
    (2, 1, 'Salchipapa', 'Porção generosa de batatas, salsicha grelhada, queijo e molhos.', 18.00, 35, 'https://img0.didiglobal.com/static/soda_public/img_69e68a65b600cf47968efa5009fa7f5a.png', 'Batata frita, salsicha, queijo, alface, ketchup, maionese e molho especial.', 'ATIVO'),
    (3, 1, 'Pollo al Espiedo', 'Frango assado inteiro no espeto, servido com batatas e limão.', 32.00, 18, 'https://cdn.store.link/products/store2705/pd5vbp-chatgpt%20image%203%20ene%202026%2C%2002_00_23%20p.m..png?versionId=eyvTUrCQPohu_ZoGY.4OwEdv9sgxa.Zm', 'Frango, batata, limão, alho, ervas, páprica, cominho e sal.', 'ATIVO'),
    (4, 1, 'Pescado Frito', 'Peixe frito crocante servido com batatas e salada fresca.', 30.00, 20, 'https://d1w7312wesee68.cloudfront.net/36l-UnIZMrwc4kY_IRZO9IaGN3QzFdOi-eTGXMHYBUA/resize%3Afit%3A720%3A720/plain/s3%3A/toasttab/restaurants/restaurant-267554000000000000/menu/items/0/item-200000053504977730_1749076470.jpg', 'Peixe, batata, alface, tomate, limão, farinha, alho e sal.', 'ATIVO'),
    (5, 1, 'Sopa de Maní', 'Sopa boliviana cremosa de amendoim com batata palha e ervas.', 22.00, 24, 'https://jacaranda.com.bo/wp-content/uploads/2025/01/IMG_Jacaranda_Sopa_Mani.webp', 'Amendoim, carne, batata, arroz ou macarrão, salsa, cebola, alho e especiarias.', 'ATIVO'),
    (6, 1, 'Pique a lo Macho', 'Prato boliviano farto com carne, salsicha, batatas, ovo e molhos.', 34.00, 16, 'https://static.wixstatic.com/media/929756_cff315b5f72b4f9c810215e96ba1fb37~mv2.jpg/v1/fill/w_626%2Ch_648%2Cal_c%2Cq_85%2Cusm_0.66_1.00_0.01%2Cenc_avif%2Cquality_auto/929756_cff315b5f72b4f9c810215e96ba1fb37~mv2.jpg', 'Carne bovina, salsicha, batata, ovo, tomate, cebola, pimentão e molho picante.', 'ATIVO'),
    (7, 1, 'Silpancho', 'Carne empanada fina sobre arroz, batatas, salada e ovo frito.', 29.00, 18, 'https://images.mrcook.app/recipe-image/019d4647-d948-7024-b7c7-0472a5e479ea/019d4647-dfb5-7c0c-9013-1a4791d2635d?cacheKey=V2VkLCAwMSBBcHIgMjAyNiAwMjoyMTo0NyBHTVQ%3D', 'Carne bovina, arroz, batata, ovo, tomate, cebola roxa, farinha de rosca e temperos.', 'ATIVO'),
    (8, 2, 'Coca Quina', 'Refrigerante boliviano gelado com sabor marcante e tradicional.', 6.00, 40, 'https://compro.bo/cdn/shop/files/COKAQUINA330ML_1200x1200.jpg?v=1700956276', 'Água gaseificada, açúcar, extratos vegetais, acidulante e aroma.', 'ATIVO'),
    (9, 2, 'Simba', 'Refrigerante Simba gelado, ideal para acompanhar lanches bolivianos.', 8.00, 32, 'https://farmacorp.com/cdn/shop/files/909714_1200x1200.jpg?v=1773894260', 'Água gaseificada, açúcar, aroma de fruta, acidulante e conservante.', 'ATIVO'),
    (10, 2, 'Pura Vida', 'Néctar de fruta Pura Vida, refrescante e bem colorido.', 8.00, 30, 'https://cdn.shopify.com/s/files/1/0517/5495/9018/files/7771259756798.jpg?v=1769662420', 'Água, polpa de fruta, açúcar, vitamina C, acidulante e aroma.', 'ATIVO'),
    (11, 2, 'Coca-Cola', 'Coca-Cola gelada para acompanhar qualquer pedido.', 7.00, 45, 'https://mir-s3-cdn-cf.behance.net/project_modules/fs/c1dd12131268523.61920dae18e81.jpg', 'Água gaseificada, açúcar, extrato de noz de cola, cafeína, corante caramelo e aroma.', 'ATIVO'),
    (12, 2, 'Pepsi', 'Pepsi gelada com apresentação clássica de garrafa.', 7.00, 45, 'https://knjaz.rs/wp-content/uploads/2023/02/pepsi-Blue-500mL-Avgust24.png', 'Água gaseificada, açúcar, extrato de cola, cafeína, corante caramelo e aroma.', 'ATIVO'),
    (13, 2, 'Mocochinche', 'Bebida tradicional boliviana feita com pêssego desidratado.', 6.50, 28, 'https://fsa.bo/productos/22285-01.jpg', 'Pêssego desidratado, água, açúcar, canela e cravo.', 'ATIVO'),
    (14, 3, 'Salteña', 'Lanche boliviano assado com massa dourada e recheio suculento.', 12.00, 50, 'android.resource://com.snacklapaz.app/drawable/saltena', 'Massa, frango ou carne, batata, ervilha, ovo, azeitona, caldo e especiarias.', 'ATIVO'),
    (15, 3, 'Tucumana', 'Pastel boliviano frito, crocante e recheado.', 10.00, 40, 'https://d3s8tbcesxr4jm.cloudfront.net/recipe-images/v3/bolivian-tucumanas-meat-filled-pastries/2_medium.jpg', 'Massa de trigo, carne moída, ervilha, batata, cebola e especiarias.', 'ATIVO'),
    (16, 3, 'Bolinho de Arroz', 'Bolinho frito dourado, crocante por fora e macio por dentro.', 7.50, 35, 'https://s2-receitas.glbimg.com/AXCxxU8HC8gwSEYdFf1XfmML5KE%3D/1200x0/filters%3Aformat%28jpeg%29/https%3A/i.s3.glbimg.com/v1/AUTH_1f540e0b94d8437dbbc39d567a1dee68/internal_photos/bs/2024/0/b/OYWhRVSv6fSvLgW0Kr4w/bolinho-arroz-eduardo-sterblitch.jpg', 'Arroz, queijo, ovo, farinha, cheiro-verde, alho e sal.', 'ATIVO'),
    (17, 3, 'Humintas', 'Lanche tradicional de milho envolto na palha.', 9.00, 28, 'https://www.gastronomiadealtura.com/images/gastronomy/es/7_85F436FF3178_huminta-3.webp', 'Milho, queijo, leite, manteiga, erva-doce, açúcar e sal.', 'ATIVO'),
    (18, 4, 'Turrón de Maní', 'Doce de amendoim firme, cortado em porções individuais.', 6.00, 30, 'android.resource://com.snacklapaz.app/drawable/turron_mani', 'Amendoim, açúcar, mel, clara de ovo e baunilha.', 'ATIVO'),
    (19, 4, 'Chicles', 'Chicles coloridos com visual chamativo para crianças e adultos.', 4.00, 60, 'android.resource://com.snacklapaz.app/drawable/gomas_mascar', 'Açúcar, goma base, glicose, corantes e aroma tutti-frutti.', 'ATIVO'),
    (20, 4, 'Gomas', 'Gomas doces coloridas, macias e com sabor de frutas.', 5.00, 55, 'https://comoencasahn.com/cdn/shop/files/Screenshot2024-08-09at10.19.01PM.png?v=1723263642', 'Açúcar, xarope de glicose, gelatina, acidulante, corantes e aromas.', 'ATIVO'),
    (21, 4, 'Chocolates', 'Chocolate boliviano com apresentação premium.', 9.00, 38, 'https://img07.shop-pro.jp/PA01361/612/product/140121355_o1.jpg?cmsp_timestamp=20200301093250', 'Cacau, açúcar, manteiga de cacau, leite em pó e baunilha.', 'ATIVO')
on conflict (id_produto) do update set
    id_categoria = excluded.id_categoria,
    nome = excluded.nome,
    descricao = excluded.descricao,
    preco = excluded.preco,
    estoque = excluded.estoque,
    imagem = excluded.imagem,
    ingredientes = excluded.ingredientes,
    status = excluded.status;

delete from produtos where id_produto > 21;
delete from categorias where id_categoria not in (1, 2, 3, 4);

select setval(pg_get_serial_sequence('categorias', 'id_categoria'), coalesce(max(id_categoria), 1))
from categorias;

select setval(pg_get_serial_sequence('produtos', 'id_produto'), coalesce(max(id_produto), 1))
from produtos;

commit;
