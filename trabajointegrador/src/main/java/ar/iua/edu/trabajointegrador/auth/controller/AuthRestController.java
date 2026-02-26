package ar.iua.edu.trabajointegrador.auth.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import io.swagger.v3.oas.annotations.Parameter;

import ar.iua.edu.trabajointegrador.auth.User;
import ar.iua.edu.trabajointegrador.auth.UserBusiness;
import ar.iua.edu.trabajointegrador.auth.custom.CustomAuthenticationManager;
import ar.iua.edu.trabajointegrador.auth.filters.AuthConstants;
import ar.iua.edu.trabajointegrador.controllers.BaseRestController;
import ar.iua.edu.trabajointegrador.controllers.Constants;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
//import ar.edu.iw3.auth.event.UserEvent;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Autenticación", description = "API para login, registro y generación de tokens JWT")
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

	@Operation(
        summary = "Login mediante Query Params",
        description = "Autentica al usuario usando username y password enviados en la URL. Retorna un JWT."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa. Retorna el token."),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas."),
        @ApiResponse(responseCode = "500", description = "Error interno en el servidor de autenticación.")
    })
	@PostMapping(value = Constants.URL_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loginExternalOnlyToken(
		@Parameter(description = "Nombre de usuario", required = true) @RequestParam String username, 
		@Parameter(description = "Contraseña", required = true) @RequestParam String password, 
		HttpServletRequest request) {
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

	@Operation(
        summary = "Login mediante JSON Body",
        description = "Recibe un objeto JSON con las credenciales. Es el método más seguro para enviar contraseñas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso. Devuelve el token JWT."),
        @ApiResponse(responseCode = "400", description = "Cuerpo del JSON mal formado o faltan datos."),
        @ApiResponse(responseCode = "401", description = "Error de autenticación.")
    })
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

	@Operation(
        summary = "Demo: Encriptar contraseña",
        description = "Herramienta de utilidad para ver cómo queda una contraseña encriptada con BCrypt."
    )
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
	
	@Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario en el sistema con un rol específico (ADMIN, OPERATOR, etc.)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente."),
        @ApiResponse(responseCode = "500", description = "Error al intentar registrar el usuario.")
    })
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