package ar.iua.edu.trabajointegrador.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ar.iua.edu.trabajointegrador.auth.IUserBusiness;
import ar.iua.edu.trabajointegrador.auth.custom.CustomAuthenticationManager;
import ar.iua.edu.trabajointegrador.auth.filters.JWTAuthorizationFilter;
import ar.iua.edu.trabajointegrador.controllers.Constants;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true)
public class SecurityConfiguration {

	// si no quiero bloquearle el acceso a ninguna ruta
	/*
	 * @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	 * // CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS // CSRF:0 //
	 * https://developer.mozilla.org/es/docs/Glossary/CSRF
	 * http.cors(CorsConfigurer::disable);
	 * http.csrf(AbstractHttpConfigurer::disable); http.authorizeHttpRequests(auth
	 * -> auth.requestMatchers("/**").permitAll().anyRequest().authenticated());
	 * return http.build(); }
	 */

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	        .cors(Customizer.withDefaults())   
	        .csrf(AbstractHttpConfigurer::disable) 
	        .authorizeHttpRequests(auth -> auth


	            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

	            .requestMatchers(HttpMethod.POST, Constants.URL_LOGIN + "/**").permitAll()
	            .requestMatchers(HttpMethod.POST, Constants.URL_BASE + "/register/**").permitAll()
	            .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
	            .requestMatchers("/ui/**", "/demo/**","/temperaturas/**", Constants.URL_ALARMA + "/**").permitAll()
	            
				.requestMatchers(Constants.URL_ALARMA + "/**").permitAll()
	            .requestMatchers(HttpMethod.POST, Constants.URL_CONCILIACION).hasRole("ADMIN")
	            .requestMatchers(HttpMethod.POST, Constants.URL_CONCILIACION + "/**").hasRole("ADMIN")

	            .anyRequest().authenticated()
	        )
	        .sessionManagement(session ->
	            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        );

	    http.addFilter(new JWTAuthorizationFilter(authenticationManager()));

	    return http.build();
	}

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
	public AuthenticationManager authenticationManager() {
		return new CustomAuthenticationManager(bCryptPasswordEncoder(), userBusiness);
	}


	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://cernikiw3.chickenkiller.com")); // Tu puerto de Vue
	 
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
	    configuration.setAllowCredentials(true);
	    
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
	

}
