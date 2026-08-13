package com.br.fiap.express_market.api.repository;

import com.br.fiap.express_market.api.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
