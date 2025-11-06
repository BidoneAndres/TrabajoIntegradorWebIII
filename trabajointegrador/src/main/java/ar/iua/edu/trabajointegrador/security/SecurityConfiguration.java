package ar.iua.edu.trabajointegrador.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ar.iua.edu.trabajointegrador.auth.IUserBusiness;
import ar.iua.edu.trabajointegrador.auth.custom.CustomAuthenticationManager;
import ar.iua.edu.trabajointegrador.auth.filters.JWTAuthorizationFilter;
import ar.iua.edu.trabajointegrador.controllers.Constants;

public class SecurityConfiguration {
	@Bean
	PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowedOrigins("*");
			}
		};
	}

	@Autowired
	private IUserBusiness userBusiness;

	@Bean
	AuthenticationManager authenticationManager() {
		return new CustomAuthenticationManager(bCryptPasswordEncoder(), userBusiness);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		
		/*Basicamente estamos diciendo que no hace falta estar autenticado para autenticarse
		 *  tambien permitimos las documentaciones y todo eso con los permit all 
		*/
		http.csrf(AbstractHttpConfigurer::disable);
		http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, Constants.URL_LOGIN).permitAll() 
				.requestMatchers("/v3/api-docs/**").permitAll().requestMatchers("/swagger-ui.html").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll().requestMatchers("/ui/**").permitAll() //Todo esto es la documentacion, que nos va a hacer una pagina web
				.requestMatchers("/demo/**").permitAll().anyRequest().authenticated());
		//http.httpBasic(Customizer.withDefaults());
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.addFilter(new JWTAuthorizationFilter(authenticationManager()));
		return http.build();

	}

}
