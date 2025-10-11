package com.example.demo.Controller;

import com.example.demo.model.RelatorioPerda;
import com.example.demo.service.RelatorioPerdaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/relatorio")
public class RelatorioPerdaController {

    private final RelatorioPerdaService service;

    public RelatorioPerdaController(RelatorioPerdaService service) {
        this.service = service;
    }

    @GetMapping
    public List<RelatorioPerda> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public RelatorioPerda buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public RelatorioPerda criar(@RequestBody RelatorioPerda relatorio) {
        return service.salvar(relatorio);
    }

    @PutMapping("update/{id}")
    public RelatorioPerda atualizar(@PathVariable Long id, @RequestBody RelatorioPerda novo) {
        RelatorioPerda existente = service.buscarPorId(id);
        if (existente == null) return null;

        existente.setIdPanificadora(novo.getIdPanificadora());
        existente.setIdProduto(novo.getIdProduto());
        existente.setPercentualPerda(novo.getPercentualPerda());
        existente.setCausa(novo.getCausa());
        existente.setPeriodo(novo.getPeriodo());

        return service.salvar(existente);
    }

    @DeleteMapping("delete/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
