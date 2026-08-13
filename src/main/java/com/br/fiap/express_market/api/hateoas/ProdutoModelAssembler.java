package com.br.fiap.express_market.api.hateoas;

import com.br.fiap.express_market.api.controller.ProdutoController;
import com.br.fiap.express_market.api.entity.Produto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Monta a representação HATEOAS de um Produto (nível 3 de maturidade de Richardson):
 * além dos dados, a resposta carrega os links das ações possíveis sobre o recurso,
 * de modo que o cliente navegue pela API sem precisar montar URLs na mão.
 */
@Component
public class ProdutoModelAssembler implements RepresentationModelAssembler<Produto, EntityModel<Produto>> {

    @Override
    public EntityModel<Produto> toModel(Produto produto) {
        return EntityModel.of(produto,
                linkTo(methodOn(ProdutoController.class).findById(produto.getId())).withSelfRel(),
                linkTo(methodOn(ProdutoController.class).findAll()).withRel("mercado"),
                linkTo(methodOn(ProdutoController.class).update(produto.getId(), null)).withRel("update"),
                linkTo(methodOn(ProdutoController.class).patch(produto.getId(), null)).withRel("patch"),
                linkTo(methodOn(ProdutoController.class).delete(produto.getId())).withRel("delete"));
    }
}
