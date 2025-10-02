package com.example.demo.repository;

import com.example.demo.model.Panificadora;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanificadoraRepository extends JpaRepository<Panificadora, Long> {
       List<Panificadora> findByCnpj(String nome);

    List<Panificadora> findByNomeContainingIgnoreCase(String nome);
}
