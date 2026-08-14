package com.br.fiap.expressmarket.api.service;

import com.br.fiap.expressmarket.api.dto.ProdutoPatchRequest;
import com.br.fiap.expressmarket.api.dto.ProdutoRequest;
import com.br.fiap.expressmarket.api.entity.Produto;
import com.br.fiap.expressmarket.api.exception.NotFoundException;
import com.br.fiap.expressmarket.api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Produto findById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Produto", id));
    }

    public Produto update(Long id, ProdutoRequest request) {
        Produto produtoExistente = findById(id);
        produtoExistente.setNome(request.nome());
        produtoExistente.setTipo(request.tipo());
        produtoExistente.setSetor(request.setor());
        produtoExistente.setTamanho(request.tamanho());
        produtoExistente.setPreco(request.preco());
        return produtoRepository.save(produtoExistente);
    }

    public Produto patch(Long id, ProdutoPatchRequest request) {
        Produto produtoExistente = findById(id);
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
    }

    public void deleteById(Long id) {
        produtoRepository.delete(findById(id));
    }
}
