package com.example.demo.Controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

@GetMapping
public ResponseEntity<?> listarUsuarios() {

  List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios); 
}

    @GetMapping("find/{id}")
    public Usuario buscarUsuario(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

@PutMapping("update/{id}")
public ResponseEntity<?> alteraUsuario(@PathVariable Long id,
                                       @RequestBody Usuario usuarioAtualizado) {
    try {
        Usuario atualizado = usuarioService.atualizarUsuario(id, usuarioAtualizado);
        return ResponseEntity.ok(atualizado);
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("E-mail já está em uso!");
    }
}





@DeleteMapping("delete/{id}")
public ResponseEntity<String> deletarUsuario(@PathVariable Long id) {
    boolean deletado = usuarioService.deletarUsuario(id);

    if (deletado) {
        return ResponseEntity.ok("Usuário deletado com sucesso!");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Usuário não encontrado");
    }
}

}