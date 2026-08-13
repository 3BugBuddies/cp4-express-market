package com.br.fiap.express_market.api.dto;

/**
 * Representação devolvida pela API. É este record que o HATEOAS embrulha em EntityModel,
 * somando os links de navegação aos dados do produto.
 */
public record ProdutoResponse(
        Long id,
        String nome,
        String tipo,
        String setor,
        String tamanho,
        Double preco
) {
}
