package com.br.fiap.express_market.api.service;

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

    public Produto save(Produto produto) {
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
    public Optional<Produto> update(Long id, Produto produto) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            produto.setId(id);
            return produtoRepository.save(produto);
        });
    }

    /**
     * PATCH — atualiza somente os campos enviados no corpo da requisição.
     * Os campos nulos do JSON são ignorados e mantêm o valor já persistido.
     */
    public Optional<Produto> patch(Long id, Produto produtoParcial) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            if (produtoParcial.getNome() != null) {
                produtoExistente.setNome(produtoParcial.getNome());
            }
            if (produtoParcial.getTipo() != null) {
                produtoExistente.setTipo(produtoParcial.getTipo());
            }
            if (produtoParcial.getSetor() != null) {
                produtoExistente.setSetor(produtoParcial.getSetor());
            }
            if (produtoParcial.getTamanho() != null) {
                produtoExistente.setTamanho(produtoParcial.getTamanho());
            }
            if (produtoParcial.getPreco() != null) {
                produtoExistente.setPreco(produtoParcial.getPreco());
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
