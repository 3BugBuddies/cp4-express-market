package com.br.fiap.express_market.api.controller;

import com.br.fiap.express_market.api.dto.ProdutoPatchRequest;
import com.br.fiap.express_market.api.dto.ProdutoRequest;
import com.br.fiap.express_market.api.dto.ProdutoResponse;
import com.br.fiap.express_market.api.hateoas.ProdutoModelAssembler;
import com.br.fiap.express_market.api.service.ProdutoService;
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
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoModelAssembler assembler;

    @PostMapping
    public ResponseEntity<EntityModel<ProdutoResponse>> create(@RequestBody @Valid ProdutoRequest request) {
        EntityModel<ProdutoResponse> produtoCriado = assembler.toModel(produtoService.save(request));
        return ResponseEntity
                .created(produtoCriado.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(produtoCriado);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProdutoResponse>>> findAll() {
        List<EntityModel<ProdutoResponse>> produtos = produtoService.findAll()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(produtos,
                linkTo(methodOn(ProdutoController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProdutoResponse>> findById(@PathVariable Long id) {
        return produtoService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProdutoResponse>> update(@PathVariable Long id,
                                                               @RequestBody @Valid ProdutoRequest request) {
        return produtoService.update(id, request)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<ProdutoResponse>> patch(@PathVariable Long id,
                                                              @RequestBody @Valid ProdutoPatchRequest request) {
        return produtoService.patch(id, request)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return produtoService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
