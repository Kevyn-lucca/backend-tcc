package com.example.demo.Controller;

import com.example.demo.model.Panificadora;
import com.example.demo.service.PanificadoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/panificadora")
public class PanificadoraController {

    @Autowired
    private PanificadoraService panificadoraService;

    @GetMapping
    public ResponseEntity<List<Panificadora>> listarPanificadoras() {
        List<Panificadora> panificadoras = panificadoraService.listarTodos();
        return ResponseEntity.ok(panificadoras);
    }

    @GetMapping("find/{id}")
    public ResponseEntity<Panificadora> buscarPanificadora(@PathVariable Long id) {
        return panificadoraService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Panificadora não encontrada"));
    }

    @PostMapping("adicionar")
    public ResponseEntity<?> criarPanificadora(@RequestBody Panificadora panificadora) {
        try {
            Panificadora criada = panificadoraService.criarPanificadora(panificadora);
            return ResponseEntity.status(HttpStatus.CREATED).body(criada);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CNPJ já está em uso!");
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> atualizarPanificadora(@PathVariable Long id,
                                                   @RequestBody Panificadora panificadoraAtualizada) {
        try {
            Panificadora atualizada = panificadoraService.atualizarPanificadora(id, panificadoraAtualizada);
            return ResponseEntity.ok(atualizada);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CNPJ já está em uso!");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deletarPanificadora(@PathVariable Long id) {
        panificadoraService.deletarPanificadora(id);
        return ResponseEntity.ok("Panificadora deletada com sucesso!");
    }
}
