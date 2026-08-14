package com.br.fiap.expressmarket.api.assembler;

import com.br.fiap.expressmarket.api.controller.ProdutoController;
import com.br.fiap.expressmarket.api.dto.ProdutoResponse;
import com.br.fiap.expressmarket.api.entity.Produto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProdutoModelAssembler implements RepresentationModelAssembler<Produto, EntityModel<ProdutoResponse>> {

    @Override
    public EntityModel<ProdutoResponse> toModel(Produto produto) {
        ProdutoResponse response = new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getTipo(),
                produto.getSetor(),
                produto.getTamanho(),
                produto.getPreco());

        return EntityModel.of(response,
                linkTo(methodOn(ProdutoController.class).findById(produto.getId())).withSelfRel(),
                linkTo(methodOn(ProdutoController.class).findAll()).withRel("mercado"),
                linkTo(methodOn(ProdutoController.class).update(produto.getId(), null)).withRel("update"),
                linkTo(methodOn(ProdutoController.class).patch(produto.getId(), null)).withRel("patch"),
                linkTo(methodOn(ProdutoController.class).delete(produto.getId())).withRel("delete"));
    }
}
