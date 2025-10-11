package com.example.demo.service;

import com.example.demo.model.Estoque;
import com.example.demo.repository.EstoqueRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    private final EstoqueRepository repository;

    public EstoqueService(EstoqueRepository repository) {
        this.repository = repository;
    }

public List<Estoque> listar() {
    List<Estoque> estoques = repository.findAll();
    estoques.forEach(e -> e.getProduto().getNome());
    return estoques;
}

    public Estoque salvar(Estoque estoque) {
        return repository.save(estoque);
    }

    public Estoque buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
