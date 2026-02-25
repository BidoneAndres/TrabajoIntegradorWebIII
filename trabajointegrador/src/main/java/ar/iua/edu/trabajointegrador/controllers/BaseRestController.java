package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.security.core.Authentication;
import ar.iua.edu.trabajointegrador.auth.User;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseRestController {
    protected User getUserLogged() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            log.error("Intento de acceso de usuario no autenticado");
            // Puedes lanzar una excepción personalizada para que el controlador devuelva 401
            return null; 
        }

        try {
            return (User) auth.getPrincipal();
        } catch (ClassCastException e) {
            log.error("El principal no es una instancia de User: " + auth.getPrincipal());
            return null;
        }
    }
}
