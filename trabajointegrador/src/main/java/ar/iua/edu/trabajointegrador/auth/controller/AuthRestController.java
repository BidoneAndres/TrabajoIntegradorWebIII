package ar.iua.edu.trabajointegrador.auth.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import ar.iua.edu.trabajointegrador.auth.User;
import ar.iua.edu.trabajointegrador.auth.UserBusiness;
import ar.iua.edu.trabajointegrador.auth.custom.CustomAuthenticationManager;
import ar.iua.edu.trabajointegrador.auth.filters.AuthConstants;
import ar.iua.edu.trabajointegrador.controllers.BaseRestController;
import ar.iua.edu.trabajointegrador.controllers.Constants;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;
//import ar.edu.iw3.auth.event.UserEvent;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
public class AuthRestController extends BaseRestController {

	@Autowired
    private final UserBusiness userBusiness;
    
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private IStandartResponseBusiness response;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	

	@Autowired
	private PasswordEncoder pEncoder;


    AuthRestController(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }


	@PostMapping(value = Constants.URL_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loginExternalOnlyToken(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
		Authentication auth = null;
		try {
			auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(username, password));
		} catch (AuthenticationServiceException e0) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e0, e0.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		}

		User user = (User) auth.getPrincipal();
		String token = JWT.create().withSubject(user.getUsername())
				.withClaim("internalId", user.getIdUser())
				.withClaim("roles", new ArrayList<String>(user.getAuthoritiesStr()))
				.withClaim("email", user.getEmail())
				.withClaim("version", "1.0.0")
				.withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));

		

		return new ResponseEntity<String>(token, HttpStatus.OK);
	}

	/**
	 * JSON login endpoint. Accepts { "username": "..", "password": ".." } in the body.
	 * Returns plain text JWT on success.
	 */
	@PostMapping(value = Constants.URL_LOGIN + "/json", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loginJson(@RequestBody LoginRequest body, HttpServletRequest request) {
		if (body == null || body.getUsername() == null || body.getPassword() == null) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, null, "username and password are required"),
					HttpStatus.BAD_REQUEST);
		}
		String username = body.getUsername();
		String password = body.getPassword();
		Authentication auth = null;
		try {
			auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(username, password));
		} catch (AuthenticationServiceException e0) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e0, e0.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		}

		User user = (User) auth.getPrincipal();
		String token = JWT.create().withSubject(user.getUsername())
				.withClaim("internalId", user.getIdUser())
				.withClaim("roles", new ArrayList<String>(user.getAuthoritiesStr()))
				.withClaim("email", user.getEmail())
				.withClaim("version", "1.0.0")
				.withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));



		return new ResponseEntity<String>(token, HttpStatus.OK);
	}

	@PostMapping(value = "/demo/encodepass", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> encodepass(@RequestBody LoginRequest body) {
		try {
			String password = body.getPassword();
			return new ResponseEntity<String>(pEncoder.encode(password), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = Constants.URL_BASE + "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createUser(@RequestBody RegisterRequest body) {
		try {
			User usuario = new User();
			usuario.setUsername(body.getUsername());
			usuario.setPassword(body.getPassword());
			usuario.setEmail(body.getEmail());
			User created = userBusiness.register(usuario, pEncoder, body.getRole());
			return new ResponseEntity<>(created, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}