package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(String email, String senha, String nome, String perfil, Integer idPanificadora) {
        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(email.toLowerCase())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        // Validações dos campos obrigatórios
        if (perfil == null || perfil.trim().isEmpty()) {
            throw new IllegalArgumentException("Perfil é obrigatório");
        }
        
        if (idPanificadora == null) {
            throw new IllegalArgumentException("ID da panificadora é obrigatório");
        }

        // Criar novo usuário
        Usuario usuario = new Usuario();
        usuario.setEmail(email.toLowerCase());
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setNome(nome != null ? nome.trim() : "");
        usuario.setPerfil(perfil);
        usuario.setIdPanificadora(idPanificadora); // Supondo que o campo se chama idPanificadora

        return usuarioRepository.save(usuario);
    }

    public Usuario autenticarUsuario(String email, String senha) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email.toLowerCase());
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                // Verificar se a senha corresponde
                if (passwordEncoder.matches(senha, usuario.getSenha())) {
                    return usuario;
                }
            }
            
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Erro durante autenticação: " + e.getMessage());
        }
    }

    public boolean verificarEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email.toLowerCase());
    }
}