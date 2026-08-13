package com.br.fiap.express_market.api.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Corpo aceito no PATCH. Todo campo é opcional — o que vier nulo mantém o valor já persistido.
 * Por isso não há @NotBlank nem @NotNull aqui: @Size e @Positive ignoram nulos e só validam
 * os campos que o cliente realmente enviou.
 */
public record ProdutoPatchRequest(

        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
        String nome,

        @Size(min = 3, max = 50, message = "O tipo deve ter entre 3 e 50 caracteres")
        String tipo,

        @Size(min = 3, max = 50, message = "O setor deve ter entre 3 e 50 caracteres")
        String setor,

        @Size(min = 2, max = 50, message = "O tamanho deve ter entre 2 e 50 caracteres")
        String tamanho,

        @Positive(message = "O preço deve ser um valor positivo")
        Double preco

) {
}
