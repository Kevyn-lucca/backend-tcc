package com.example.demo.Controller;

import com.example.demo.model.Usuario;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtUtil jwtUtil;
        
    public AuthController(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }    

    @Autowired
    private AuthService authService;

@PostMapping("/registrar")
public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
    try {
        // Validações básicas
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Email é obrigatório"));
        }
        
        if (request.getSenha() == null || request.getSenha().length() < 6) {
            return ResponseEntity.badRequest().body(createErrorResponse("Senha deve ter pelo menos 6 caracteres"));
        }

        // VALIDAÇÕES DOS NOVOS CAMPOS OBRIGATÓRIOS
        if (request.getPerfil() == null || request.getPerfil().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Perfil é obrigatório"));
        }
        
        if (request.getIdPanificadora() == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("ID da panificadora é obrigatório"));
        }

        // Registrar usuário
        Usuario usuario = authService.registrarUsuario(
            request.getEmail().trim().toLowerCase(),
            request.getSenha(),
            request.getNome(),
            request.getPerfil(),
            request.getIdPanificadora()
        );

        // Resposta de sucesso
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Usuário registrado com sucesso");
        response.put("usuario", Map.of(
            "id", usuario.getId(),
            "email", usuario.getEmail(),
            "nome", usuario.getNome(),
            "perfil", usuario.getPerfil(),
            "idPanificadora", usuario.getIdPanificadora()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(createErrorResponse("Erro interno do servidor"));
    }
}
    




 @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest request) {
        try {
            // Validação básica
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Email é obrigatório"));
            }
            
            if (request.getSenha() == null || request.getSenha().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Senha é obrigatória"));
            }

            // Autenticar usuário
            Usuario usuario = authService.autenticarUsuario(
                request.getEmail().trim().toLowerCase(),
                request.getSenha()
            );

            if (usuario != null) {
                // Resposta de sucesso
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login realizado com sucesso");
                response.put("usuario", Map.of(
                    "id", usuario.getId(),
                    "email", usuario.getEmail(),
                    "nome", usuario.getNome(),
                    "token", jwtUtil.gerarToken(usuario.getNome()) 
                ));
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Email ou senha inválidos"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro interno do servidor"));
        }
    }

@GetMapping("/verificar-email/{email}")
    public ResponseEntity<?> verificarEmail(@PathVariable String email) {
        try {
            boolean emailExiste = authService.verificarEmailExistente(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("disponivel", !emailExiste);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erro ao verificar email"));
        }
    }

@GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Auth Service");
        return ResponseEntity.ok(response);
    }

    // Método auxiliar para criar respostas de erro
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return errorResponse;
    }

    // Classes DTO internas
  public static class RegistroRequest {
    private String email;
    private String senha;
    private String nome;
    private String perfil;          // NOVO CAMPO OBRIGATÓRIO
    private Integer idPanificadora; // NOVO CAMPO OBRIGATÓRIO

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    
    public Integer getIdPanificadora() { return idPanificadora; }
    public void setIdPanificadora(Integer idPanificadora) { this.idPanificadora = idPanificadora; }
}
    public static class LoginRequest {
        private String email;
        private String senha;

        // Getters e Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }
}