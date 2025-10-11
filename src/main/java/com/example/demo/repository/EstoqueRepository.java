package com.example.demo.repository;

import com.example.demo.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    @Query(value = "SELECT p.nome, e.quantidade, e.data_validade, e.status, p.marca " +
                   "FROM estoque e " +
                   "INNER JOIN produto p ON p.id_produto = e.id_produto",
           nativeQuery = true)
    List<Object[]> listarEstoqueComProduto();
}
