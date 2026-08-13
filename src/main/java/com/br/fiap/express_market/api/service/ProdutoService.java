package com.br.fiap.express_market.api.service;

import com.br.fiap.express_market.api.dto.ProdutoPatchRequest;
import com.br.fiap.express_market.api.dto.ProdutoRequest;
import com.br.fiap.express_market.api.entity.Produto;
import com.br.fiap.express_market.api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public Produto save(ProdutoRequest request) {
        Produto produto = Produto.builder()
                .nome(request.nome())
                .tipo(request.tipo())
                .setor(request.setor())
                .tamanho(request.tamanho())
                .preco(request.preco())
                .build();

        return produtoRepository.save(produto);
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }

    /**
     * PUT — substitui integralmente o produto. Retorna Optional.empty() se o id não existir,
     * evitando que um PUT em id inexistente acabe criando um registro novo.
     */
    public Optional<Produto> update(Long id, ProdutoRequest request) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            produtoExistente.setNome(request.nome());
            produtoExistente.setTipo(request.tipo());
            produtoExistente.setSetor(request.setor());
            produtoExistente.setTamanho(request.tamanho());
            produtoExistente.setPreco(request.preco());
            return produtoRepository.save(produtoExistente);
        });
    }

    /**
     * PATCH — atualiza somente os campos enviados no corpo da requisição.
     * Os campos nulos do JSON são ignorados e mantêm o valor já persistido.
     */
    public Optional<Produto> patch(Long id, ProdutoPatchRequest request) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            if (request.nome() != null) {
                produtoExistente.setNome(request.nome());
            }
            if (request.tipo() != null) {
                produtoExistente.setTipo(request.tipo());
            }
            if (request.setor() != null) {
                produtoExistente.setSetor(request.setor());
            }
            if (request.tamanho() != null) {
                produtoExistente.setTamanho(request.tamanho());
            }
            if (request.preco() != null) {
                produtoExistente.setPreco(request.preco());
            }
            return produtoRepository.save(produtoExistente);
        });
    }

    public boolean deleteById(Long id) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            produtoRepository.delete(produtoExistente);
            return true;
        }).orElse(false);
    }
}
