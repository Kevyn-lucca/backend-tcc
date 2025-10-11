package com.example.demo.service;

import com.example.demo.model.RelatorioPerda;
import com.example.demo.repository.RelatorioPerdaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RelatorioPerdaService {

    private final RelatorioPerdaRepository repository;

    public RelatorioPerdaService(RelatorioPerdaRepository repository) {
        this.repository = repository;
    }

    public List<RelatorioPerda> listar() {
        return repository.findAll();
    }

    public RelatorioPerda buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public RelatorioPerda salvar(RelatorioPerda relatorio) {
        return repository.save(relatorio);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
