package com.br.fiap.expressmarket.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Campos a alterar em um produto. Todos opcionais — os ausentes são preservados")
public record ProdutoPatchRequest(

        @Schema(example = "Detergente Neutro 1L")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
        String nome,

        @Schema(example = "Produto de Limpeza")
        @Size(min = 3, max = 50, message = "O tipo deve ter entre 3 e 50 caracteres")
        String tipo,

        @Schema(example = "Limpeza")
        @Size(min = 3, max = 50, message = "O setor deve ter entre 3 e 50 caracteres")
        String setor,

        @Schema(example = "1L")
        @Size(min = 2, max = 50, message = "O tamanho deve ter entre 2 e 50 caracteres")
        String tamanho,

        @Schema(example = "4.79")
        @Positive(message = "O preço deve ser um valor positivo")
        Double preco

) {
}
