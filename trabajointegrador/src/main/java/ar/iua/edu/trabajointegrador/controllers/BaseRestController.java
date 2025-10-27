package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ar.iua.edu.trabajointegrador.auth.User;

public class BaseRestController {
	protected User getUserLogged() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User usuario = (User) auth.getPrincipal();
		return usuario;
	}
}
