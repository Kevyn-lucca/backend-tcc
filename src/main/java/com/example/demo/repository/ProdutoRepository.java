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
    
    Optional<Produto> findByNome(String nome);
    
    List<Produto> findByCategoria(String categoria);
    
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    
    List<Produto> findByPerecivel(Boolean perecivel);
    
    @Query("SELECT p FROM Produto p WHERE LOWER(p.categoria) LIKE LOWER(CONCAT('%', :categoria, '%'))")
    List<Produto> findByCategoriaContainingIgnoreCase(@Param("categoria") String categoria);
    
    boolean existsByNome(String nome);
}