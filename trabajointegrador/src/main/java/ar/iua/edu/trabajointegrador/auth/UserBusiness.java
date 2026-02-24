package ar.iua.edu.trabajointegrador.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserBusiness implements IUserBusiness {

	@Autowired
	private UserRepository userDAO;



	@Override
	public User load(String usernameOrEmail) throws NotFoundException, BusinessException {
		Optional<User> ou;
		try {
			ou = userDAO.findOneByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (ou.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra el usuari@ email o nombre =" + usernameOrEmail)
					.build();
		}
		return ou.get();
	}

	@Override
	public void changePassword(String usernameOrEmail, String oldPassword, String newPassword, PasswordEncoder pEncoder)
			throws BadPasswordException, NotFoundException, BusinessException {
		User user = load(usernameOrEmail);
		if (!pEncoder.matches(oldPassword, user.getPassword())) {
			throw BadPasswordException.builder().build();
		}
		user.setPassword(pEncoder.encode(newPassword));
		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public void disable(String usernameOrEmail) throws NotFoundException, BusinessException {
		setDisable(usernameOrEmail, false);
	}

	@Override
	public void enable(String usernameOrEmail) throws NotFoundException, BusinessException {
		setDisable(usernameOrEmail, true);
	}

	private void setDisable(String usernameOrEmail, boolean enable) throws NotFoundException, BusinessException {
		User user = load(usernameOrEmail);
		user.setEnabled(enable);
		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public List<User> list() throws BusinessException {
		try {
			return userDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}
	
	@Override
	public User register(User user, PasswordEncoder pEncoder) throws BusinessException {
		try {
			// 1. Validar que el usuario o email no existan previamente
			Optional<User> existingUser = userDAO.findOneByUsernameOrEmail(user.getUsername(), user.getEmail());
			if (existingUser.isPresent()) {
				throw BusinessException.builder()
						.message("El nombre de usuario o email ya se encuentra registrado")
						.build();
			}

			// 2. Encriptar la contraseña recibida
			user.setPassword(pEncoder.encode(user.getPassword()));

			// 3. Activar las banderas de Spring Security
			user.setEnabled(true);
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setCredentialsNonExpired(true);

			Role roleUser = new Role();
			roleUser.setId(2);
			roleUser.setName("ROLE_USER");
			Set<Role> roles = new HashSet<>();
			roles.add(roleUser);
			
			user.setRoles(roles);
			
			
			// 5. Guardar en la base de datos
			// Hibernate guardará primero el User y luego insertará automáticamente 
			// la relación en la tabla intermedia 'userroles'
			return userDAO.save(user);

		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error inesperado al registrar usuario: {}", e.getMessage(), e);
			throw BusinessException.builder().message("Error interno al registrar el usuario").ex(e).build();
		}
	}

	

}