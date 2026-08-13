package com.br.fiap.express_market.api.controller;

import com.br.fiap.express_market.api.entity.Produto;
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
    public ResponseEntity<EntityModel<Produto>> create(@RequestBody @Valid Produto produto) {
        EntityModel<Produto> produtoCriado = assembler.toModel(produtoService.save(produto));
        return ResponseEntity
                .created(produtoCriado.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(produtoCriado);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Produto>>> findAll() {
        List<EntityModel<Produto>> produtos = produtoService.findAll()
                .stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(produtos,
                linkTo(methodOn(ProdutoController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Produto>> findById(@PathVariable Long id) {
        return produtoService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Produto>> update(@PathVariable Long id, @RequestBody @Valid Produto produto) {
        return produtoService.update(id, produto)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<Produto>> patch(@PathVariable Long id, @RequestBody Produto produto) {
        return produtoService.patch(id, produto)
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
