package com.br.fiap.expressmarket.api.repository;

import com.br.fiap.expressmarket.api.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
