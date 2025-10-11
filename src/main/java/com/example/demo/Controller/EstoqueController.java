package com.example.demo.Controller;

import com.example.demo.model.Estoque;
import com.example.demo.service.EstoqueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
@CrossOrigin(origins = "*")
public class EstoqueController {

    private final EstoqueService service;

    public EstoqueController(EstoqueService service) {
        this.service = service;
    }

    // Listar todos os estoques com produto carregado
@GetMapping
public List<Estoque> listar() {
    return service.listar();
}


    // Buscar estoque por ID
    @GetMapping("/{id}")
    public Estoque buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // Criar novo estoque
    @PostMapping("/criar")
    public Estoque criar(@RequestBody Estoque estoque) {
        return service.salvar(estoque);
    }

    // Atualizar estoque existente
    @PutMapping("/update/{id}")
    public Estoque atualizar(@PathVariable Long id, @RequestBody Estoque estoqueAtualizado) {
        Estoque existente = service.buscarPorId(id);
        if (existente == null) return null;

        existente.setProduto(estoqueAtualizado.getProduto());
        existente.setIdProduto(estoqueAtualizado.getIdProduto());
        existente.setIdPanificadora(estoqueAtualizado.getIdPanificadora());
        existente.setQuantidade(estoqueAtualizado.getQuantidade());
        existente.setDataValidade(estoqueAtualizado.getDataValidade());
        existente.setStatus(estoqueAtualizado.getStatus());

        return service.salvar(existente);
    }

    // Deletar estoque
    @DeleteMapping("/delete/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
