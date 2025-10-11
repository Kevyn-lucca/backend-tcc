package com.example.demo.Controller;

import com.example.demo.model.Usuario;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    private com.example.demo.service.InviteTokenService inviteTokenService;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // -------------------------------
    // REGISTRAR USUÁRIO
    // -------------------------------
@PostMapping("/registrar")
public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
    try {

        // --- Validações básicas ---
        if (request.getEmail() == null || request.getEmail().trim().isEmpty())
            return ResponseEntity.badRequest().body(createErrorResponse("Email é obrigatório"));

        if (request.getSenha() == null || request.getSenha().length() < 6)
            return ResponseEntity.badRequest().body(createErrorResponse("Senha deve ter pelo menos 6 caracteres"));

        if (request.getTokenConvite() == null || request.getTokenConvite().trim().isEmpty())
            return ResponseEntity.badRequest().body(createErrorResponse("Token de convite é obrigatório"));



        String token = request.getTokenConvite().trim();

        // --- Valida token em memória ---
        if (!inviteTokenService.validarToken(token)) {
            return ResponseEntity.badRequest().body(createErrorResponse("Token de convite inválido ou expirado"));
        }

        // --- Extrai dados do payload do token ---
        String decoded = new String(Base64.getDecoder().decode(token));
        // Token gerado como: UUID + ":" + perfil + ":" + idPanificadora + ":" + timestamp
        String[] parts = decoded.split(":");
        if (parts.length < 4) {
            return ResponseEntity.badRequest().body(createErrorResponse("Token de convite inválido"));
        }
        String perfil = parts[1];
        Integer idPanificadora = Integer.parseInt(parts[2]);

        // --- Cria usuário usando dados do token ---
        Usuario usuario = authService.registrarUsuario(
                request.getEmail().trim().toLowerCase(),
                request.getSenha(),
                request.getNome(),
                perfil,
                idPanificadora
        );

        // --- Marca token como usado ---
        inviteTokenService.tokens.remove(token); // remove do mapa em memória

        // --- Monta resposta ---
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


    // -------------------------------
    // LOGIN
    // -------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty())
                return ResponseEntity.badRequest().body(createErrorResponse("Email é obrigatório"));
            if (request.getSenha() == null || request.getSenha().isEmpty())
                return ResponseEntity.badRequest().body(createErrorResponse("Senha é obrigatória"));

            Usuario usuario = authService.autenticarUsuario(
                    request.getEmail().trim().toLowerCase(),
                    request.getSenha()
            );

            if (usuario == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Email ou senha inválidos"));

            String accessToken = jwtUtil.gerarToken(usuario.getId());
            String refreshToken = jwtUtil.gerarRefreshToken(usuario.getId());

            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(60 * 60 * 24 * 30)
                    .build();

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "Login realizado com sucesso");
            body.put("usuario", Map.of(
                    "id", usuario.getId(),
                    "email", usuario.getEmail(),
                    "nome", usuario.getNome(),
                    "perfil", usuario.getPerfil(),
                    "idPanificadora", usuario.getIdPanificadora(),
                    "token", accessToken
            ));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(body);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro interno do servidor"));
        }
    }

    // -------------------------------
    // REFRESH TOKEN
    // -------------------------------
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            var cookie = WebUtils.getCookie(request, "refresh_token");
            if (cookie == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Token de atualização ausente"));

            String refreshToken = cookie.getValue();
            if (!jwtUtil.validarToken(refreshToken))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Refresh token inválido"));

            Long userId = jwtUtil.extrairId(refreshToken);
            if (userId == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Falha ao extrair ID do token"));

            String novoAccessToken = jwtUtil.gerarToken(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", novoAccessToken
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Falha ao atualizar token"));
        }
    }

    // -------------------------------
    // LOGOUT
    // -------------------------------
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie expiredCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .body(Map.of("success", true, "message", "Logout efetuado"));
    }

    // -------------------------------
    // HEALTH CHECK
    // -------------------------------
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "OK", "service", "Auth Service"));
    }

    // -------------------------------
    // AUXILIARES
    // -------------------------------
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return errorResponse;
    }

  public static class RegistroRequest {
    private String email;
    private String senha;
    private String nome;
    private String perfil;
    private Integer idPanificadora;

    @JsonProperty("inviteToken") // mapeia o JSON "inviteToken" para este campo
    private String tokenConvite;

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

    public String getTokenConvite() { return tokenConvite; }
    public void setTokenConvite(String tokenConvite) { this.tokenConvite = tokenConvite; }
}

    public static class LoginRequest {
        private String email;
        private String senha;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
}
