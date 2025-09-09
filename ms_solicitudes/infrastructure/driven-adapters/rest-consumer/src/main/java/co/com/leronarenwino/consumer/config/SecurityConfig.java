package co.com.leronarenwino.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String BASE_URL = "/api/v1";
    private static final String LOAN_APPLICATION_URL = BASE_URL + "/loan-application";
    private static final String LOAN_APPLICATION_URL_ID = LOAN_APPLICATION_URL + "/{id}";
    private static final String ADVISOR_ROLE = "ADVISOR";

    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    public SecurityConfig(JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter) {
        this.jwtTokenAuthenticationFilter = jwtTokenAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints públicos
                        .pathMatchers("/h2/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()

                        // Endpoints protegidos
                        .pathMatchers(HttpMethod.POST,LOAN_APPLICATION_URL).hasRole("CLIENT")
                        .pathMatchers(HttpMethod.GET,LOAN_APPLICATION_URL).hasRole(ADVISOR_ROLE)
                        .pathMatchers(HttpMethod.PUT, LOAN_APPLICATION_URL_ID).hasRole(ADVISOR_ROLE)
                        .pathMatchers(HttpMethod.GET,"/api/v1/user").hasAnyRole("CLIENT", ADVISOR_ROLE)

                        // Cualquier otra petición requiere autenticación
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtTokenAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> Mono.empty();
    }
}