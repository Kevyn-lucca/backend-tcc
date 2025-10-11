package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InviteTokenService {

    private final SecureRandom random = new SecureRandom();
    public  Map<String, Instant> tokens = new ConcurrentHashMap<>();
    private final Duration EXPIRATION = Duration.ofMinutes(10);

    /**
     * Gera um token aleatório com validade de 10 minutos.
     */
    public String gerarToken(String perfil, Long idPanificadora) {
        String tokenUUID = UUID.randomUUID().toString();
        String payload = perfil + ":" + idPanificadora + ":" + Instant.now().toEpochMilli();
        String token = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((tokenUUID + ":" + payload).getBytes());

        // Salva token com timestamp de expiração
        tokens.put(token, Instant.now().plus(EXPIRATION));
        return token;
    }

    /**
     * Valida o token: retorna true se válido, false se inválido ou expirado.
     */
    public boolean validarToken(String token) {
        Instant expiry = tokens.get(token);
        if (expiry == null) return false;

        if (expiry.isAfter(Instant.now())) {
            return true;
        } else {
            tokens.remove(token); // remove se expirado
            return false;
        }
    }

    /**
     * Marca o token como usado (remove da memória)
     */
    public void consumirToken(String token) {
        tokens.remove(token);
    }

    /**
     * Gera uma string aleatória segura
     */
    public String gerarTokenSeguro() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
