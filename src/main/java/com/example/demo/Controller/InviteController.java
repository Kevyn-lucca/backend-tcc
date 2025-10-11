package com.example.demo.Controller;

import com.example.demo.service.InviteTokenService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/invite")
public class InviteController {

    private final InviteTokenService inviteService;

    public InviteController(InviteTokenService inviteService) {
        this.inviteService = inviteService;
    }

    @PostMapping("/generate")
    public Map<String, Object> generate(@RequestBody GenerateRequest request) {
        String token = inviteService.gerarToken(request.getPerfil(), request.getIdPanificadora());
        return Map.of(
                "success", true,
                "inviteToken", token,
                "expiresIn", "10 minutos"
        );
    }

    // DTO interno para request
    public static class GenerateRequest {
        private String perfil;
        private Long idPanificadora;

        public String getPerfil() { return perfil; }
        public void setPerfil(String perfil) { this.perfil = perfil; }

        public Long getIdPanificadora() { return idPanificadora; }
        public void setIdPanificadora(Long idPanificadora) { this.idPanificadora = idPanificadora; }
    }
}
