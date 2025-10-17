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


    public String gerarToken(String perfil, Long idPanificadora) {
        String tokenUUID = UUID.randomUUID().toString();
        String payload = perfil + ":" + idPanificadora + ":" + Instant.now().toEpochMilli();
        String token = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((tokenUUID + ":" + payload).getBytes());

        tokens.put(token, Instant.now().plus(EXPIRATION));
        return token;
    }

 
    public boolean validarToken(String token) {
        Instant expiry = tokens.get(token);
        if (expiry == null) return false;

        if (expiry.isAfter(Instant.now())) {
            return true;
        } else {
            tokens.remove(token); 
            return false;
        }
    }


    public void consumirToken(String token) {
        tokens.remove(token);
    }

    public String gerarTokenSeguro() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
