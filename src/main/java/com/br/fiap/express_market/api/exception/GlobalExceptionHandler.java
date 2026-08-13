package com.br.fiap.express_market.api.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Traduz falhas de validação em respostas 400 legíveis, no lugar do stacktrace padrão.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Disparado quando o corpo de um POST ou PUT anotado com @Valid não passa nas constraints. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBodyValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new TreeMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> erros.put(erro.getField(), erro.getDefaultMessage()));
        return montarResposta(erros);
    }

    /** Disparado pelo Hibernate no flush do PATCH, já que o corpo parcial não passa pelo @Valid. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleEntityValidation(ConstraintViolationException ex) {
        Map<String, String> erros = new TreeMap<>();
        for (ConstraintViolation<?> violacao : ex.getConstraintViolations()) {
            erros.put(violacao.getPropertyPath().toString(), violacao.getMessage());
        }
        return montarResposta(erros);
    }

    private ResponseEntity<Map<String, Object>> montarResposta(Map<String, String> erros) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", HttpStatus.BAD_REQUEST.value());
        corpo.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        corpo.put("errors", erros);
        return ResponseEntity.badRequest().body(corpo);
    }
}
