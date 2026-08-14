package com.br.fiap.expressmarket.api.controller;

import com.br.fiap.expressmarket.api.dto.ProdutoPatchRequest;
import com.br.fiap.expressmarket.api.dto.ProdutoRequest;
import com.br.fiap.expressmarket.api.dto.ProdutoResponse;
import com.br.fiap.expressmarket.api.assembler.ProdutoModelAssembler;
import com.br.fiap.expressmarket.api.handler.ErrorResponse;
import com.br.fiap.expressmarket.api.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/mercado")
@RequiredArgsConstructor
@Tag(name = "Mercado", description = "CRUD dos produtos do mercado express (tabela TDS_TB_mercado)")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoModelAssembler assembler;

    @PostMapping
    @Operation(summary = "Cadastra um novo produto",
            description = "O id é gerado pela sequence SQ_TDS_TB_mercado. "
                    + "A resposta traz o header Location apontando para o recurso criado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Produto criado",
                            content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<EntityModel<ProdutoResponse>> create(@RequestBody @Valid ProdutoRequest request) {
        EntityModel<ProdutoResponse> produtoCriado = assembler.toModel(produtoService.save(request));
        return ResponseEntity
                .created(produtoCriado.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(produtoCriado);
    }

    @GetMapping
    @Operation(summary = "Lista todos os produtos",
            description = "Consulta a tabela TDS_TB_mercado. Os itens vêm dentro de _embedded, "
                    + "cada um com seus próprios links de navegação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProdutoResponse.class))))
            })
    public ResponseEntity<CollectionModel<EntityModel<ProdutoResponse>>> findAll() {
        List<EntityModel<ProdutoResponse>> produtos = produtoService.findAll()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(produtos,
                linkTo(methodOn(ProdutoController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto por id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produto encontrado",
                            content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<EntityModel<ProdutoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(produtoService.findById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Substitui um produto por completo",
            description = "Todos os campos são obrigatórios. Em id inexistente devolve 404 — "
                    + "o PUT não cria registro novo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produto atualizado",
                            content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<EntityModel<ProdutoResponse>> update(@PathVariable Long id,
                                                               @RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.ok(assembler.toModel(produtoService.update(id, request)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente um produto",
            description = "Somente os campos enviados são alterados. Os ausentes mantêm o valor já persistido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produto atualizado",
                            content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<EntityModel<ProdutoResponse>> patch(@PathVariable Long id,
                                                              @RequestBody @Valid ProdutoPatchRequest request) {
        return ResponseEntity.ok(assembler.toModel(produtoService.patch(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um produto pelo id",
            description = "Exclui o registro da tabela TDS_TB_mercado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Produto removido"),
                    @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
