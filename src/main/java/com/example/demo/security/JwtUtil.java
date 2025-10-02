package com.example.demo.security;

import io.jsonwebtoken.*;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private static final long EXPIRATION_MS = 3600000L; // 1 hora

    public JwtUtil() {
        try {
            this.privateKey = loadPrivateKey("private_key.pem");
            this.publicKey = loadPublicKey("public_key.pem");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar chaves RSA", e);
        }
    }

    private PrivateKey loadPrivateKey(String resourcePath) throws Exception {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (PemReader pemReader = new PemReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            byte[] content = pemReader.readPemObject().getContent();
            // PKCS#1 -> RSAPrivateCrtKeySpec
            // Parse manual dos valores da chave
            // Para simplificar, vamos usar BouncyCastle para converter em RSAPrivateCrtKeySpec
            org.bouncycastle.asn1.pkcs.RSAPrivateKey rsaPrivateKey = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(content);
            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                    rsaPrivateKey.getModulus(),
                    rsaPrivateKey.getPublicExponent(),
                    rsaPrivateKey.getPrivateExponent(),
                    rsaPrivateKey.getPrime1(),
                    rsaPrivateKey.getPrime2(),
                    rsaPrivateKey.getExponent1(),
                    rsaPrivateKey.getExponent2(),
                    rsaPrivateKey.getCoefficient()
            );
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }

    private PublicKey loadPublicKey(String resourcePath) throws Exception {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("-----.*KEY-----", "")
                    .replaceAll("\\s", "");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        }
    }

    public String gerarToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String extrairUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean validarToken(String token, String username) {
        String tokenUsername = extrairUsername(token);
        return tokenUsername != null && tokenUsername.equals(username) && !isTokenExpirado(token);
    }

    private boolean isTokenExpirado(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
