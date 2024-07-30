package rw.companyz.useraccountms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import rw.companyz.useraccountms.models.domains.ApiResponse;
import rw.companyz.useraccountms.security.JwtAuthenticationEntryPoint;
import rw.companyz.useraccountms.security.JwtAuthenticationFilter;
import rw.companyz.useraccountms.services.impl.UserDetailServiceImpl;

import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationEntryPoint unauthorizedHandler;
	private final MessageSource messageSource;

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserDetailServiceImpl userService;
	public static final String[] AUTH_WHITELIST = {
			"/swagger-resources",
			"/swagger-resources/**",
			"/configuration/ui",
			"/configuration/security",
			"/swagger-ui.html",
			"/swagger-ui/**",
			"/webjars/**",
			"/v3/api-docs",
			"/v3/api-docs/**",
			"/api/public/**",
			"/api/public/authenticate",
			"/actuator/*",
			"/swagger-ui/**",
			"/api/v1/users/raw/**",
			"/api/v1/auth/signin",
			"/api/v1/auth/signup",
			"/api/v1/auth/signInToken",
			"/api/v1/auth/verifyToken",
			"/api/v1/auth/forgotPassword",
			"/api/v1/auth/verifyOTP",
			"/api/v1/auth/verifyToken",
			// TODO: add more stuffs for login links, if necessary
	};

	@Bean
	public AuthenticationEntryPoint authenticationErrorHandler() {
		return (request, response, ex) -> {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			ServletOutputStream out = response.getOutputStream();
			new ObjectMapper().writeValue(out, new ApiResponse<String>("Invalid or missing auth token." +
					"",  (Object) "", HttpStatus.UNAUTHORIZED));

			out.flush();
		};
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, ex) -> {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			ServletOutputStream out = response.getOutputStream();
			new ObjectMapper().writeValue(out, new ApiResponse<String>("You are not allowed to access this resource.", (Object) "", HttpStatus.FORBIDDEN));
			out.flush();
		};
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(request -> request
						.requestMatchers(AUTH_WHITELIST).permitAll()
						.anyRequest().authenticated())
				.sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
				.authenticationProvider(authenticationProvider()).addFilterBefore(
						jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).exceptionHandling().authenticationEntryPoint(authenticationErrorHandler()).accessDeniedHandler(accessDeniedHandler()
				);


		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(false);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
			throws Exception {
		return config.getAuthenticationManager();
	}
}
