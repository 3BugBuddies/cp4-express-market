package com.br.fiap.expressmarket.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados de um produto para criação ou substituição completa")
public record ProdutoRequest(

        @Schema(example = "Detergente Neutro 500ml", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
        String nome,

        @Schema(example = "Produto de Limpeza", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O tipo é obrigatório")
        @Size(min = 3, max = 50, message = "O tipo deve ter entre 3 e 50 caracteres")
        String tipo,

        @Schema(example = "Limpeza", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O setor é obrigatório")
        @Size(min = 3, max = 50, message = "O setor deve ter entre 3 e 50 caracteres")
        String setor,

        @Schema(example = "500ml", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O tamanho é obrigatório")
        @Size(min = 2, max = 50, message = "O tamanho deve ter entre 2 e 50 caracteres")
        String tamanho,

        @Schema(example = "3.49", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser um valor positivo")
        Double preco

) {
}
