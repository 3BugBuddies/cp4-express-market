package com.br.fiap.expressmarket.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Produto retornado pela API, acompanhado dos links HATEOAS")
public record ProdutoResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "Detergente Neutro 500ml")
        String nome,

        @Schema(example = "Produto de Limpeza")
        String tipo,

        @Schema(example = "Limpeza")
        String setor,

        @Schema(example = "500ml")
        String tamanho,

        @Schema(example = "3.49")
        Double preco

) {
}
