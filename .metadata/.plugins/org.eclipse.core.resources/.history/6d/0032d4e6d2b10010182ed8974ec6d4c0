package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.iua.edu.trabajointegrador.models.persistence.UsuarioExterno;
import ar.iua.edu.trabajointegrador.models.persistence.UsuarioExternoRepository;
import ar.iua.edu.trabajointegrador.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioExternoRepository repo;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioExternoRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioExterno login) {
        var user = repo.findByEmail(login.getEmail())
                .filter(u -> u.getPassword().equals(login.getPassword()))
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(token);
    }
}
