package com.example.demo.repository;

import com.example.demo.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Busca produto pelo nome exato
    Optional<Produto> findByNome(String nome);

    // Busca produtos por categoria (contendo, ignorando maiúsculas/minúsculas)
    List<Produto> findByCategoriaContainingIgnoreCase(String categoria);

    // Busca produtos pelo nome parcial (ignore case)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca produtos pelo atributo perecível
    List<Produto> findByPerecivel(Boolean perecivel);

    // Verifica se um produto existe pelo nome
    boolean existsByNome(String nome);

    // ================================
    // Query nativa para trazer produtos junto com estoque (opcional)
    // Retorna apenas Produto; para mais campos do estoque, precisaria DTO ou Object[]
    // ================================
@Query(value = "SELECT e.id_estoque, e.id_produto, e.id_panificadora, e.quantidade, e.data_validade, e.status, " +
               "p.id_produto, p.nome, p.categoria, p.unidade_medida, p.perecivel, p.marca " +
               "FROM estoque e " +
               "INNER JOIN produto p ON p.id_produto = e.id_produto", nativeQuery = true)
    List<Produto> findProdutosPorCategoria(@Param("categoria") String categoria);
}
