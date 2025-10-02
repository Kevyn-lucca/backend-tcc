package com.example.demo.service;

import com.example.demo.model.Panificadora;
import com.example.demo.repository.PanificadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PanificadoraService {

    @Autowired
    private PanificadoraRepository panificadoraRepository;

    // Criar uma nova panificadora
    public Panificadora criarPanificadora(Panificadora panificadora) {
        return panificadoraRepository.save(panificadora);
    }

    // Buscar todas as panificadoras
    public List<Panificadora> listarTodos() {
        return panificadoraRepository.findAll();
    }

    // Buscar panificadora por ID
    public Optional<Panificadora> buscarPorId(Long id) {
        return panificadoraRepository.findById(id);
    }

    // Buscar panificadora por CNPJ
    public List<Panificadora> buscarPorCnpj(String cnpj) {
        return panificadoraRepository.findByCnpj(cnpj);
    }

    // Buscar panificadoras por nome (contendo o texto)
    public List<Panificadora> buscarPorNome(String nome) {
        return panificadoraRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Atualizar uma panificadora
    public Panificadora atualizarPanificadora(Long id, Panificadora panificadoraAtualizada) {
        return panificadoraRepository.findById(id)
                .map(panificadora -> {
                    panificadora.setNome(panificadoraAtualizada.getNome());
                    panificadora.setCnpj(panificadoraAtualizada.getCnpj());
                    panificadora.setEndereco(panificadoraAtualizada.getEndereco());
                    panificadora.setTelefone(panificadoraAtualizada.getTelefone());
                    return panificadoraRepository.save(panificadora);
                })
                .orElseGet(() -> {
                    panificadoraAtualizada.setId(id);
                    return panificadoraRepository.save(panificadoraAtualizada);
                });
    }

    // Deletar uma panificadora
    public void deletarPanificadora(Long id) {
        panificadoraRepository.deleteById(id);
    }

    // Verificar se uma panificadora existe pelo ID
    public boolean existePorId(Long id) {
        return panificadoraRepository.existsById(id);
    }
}