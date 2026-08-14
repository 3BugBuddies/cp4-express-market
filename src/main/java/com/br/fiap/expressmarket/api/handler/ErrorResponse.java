package com.br.fiap.expressmarket.api.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Corpo padrão das respostas de erro")
public record ErrorResponse(

        @Schema(example = "2026-08-13T23:45:02.086121Z")
        Instant timestamp,

        @Schema(example = "400")
        int status,

        @Schema(example = "Bad Request")
        String error,

        @Schema(example = "Falha de validação nos campos enviados.")
        String message,

        @Schema(example = "/mercado")
        String path,

        @Schema(description = "Só vem em falhas de validação: campo para mensagem rejeitada",
                example = "{\"nome\": \"O nome deve ter entre 3 e 50 caracteres\"}")
        Map<String, String> errors

) {

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, null);
    }

    public static ErrorResponse of(int status, String error, String message, String path,
                                   Map<String, String> errors) {
        return new ErrorResponse(Instant.now(), status, error, message, path, errors);
    }
}
