package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import ar.iua.edu.trabajointegrador.auth.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseRestController {
	protected User getUserLogged() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user;
    }
}
