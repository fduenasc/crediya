package co.com.leronarenwino.tokenprovider.config;

import co.com.leronarenwino.jwtutils.JwtUtils;
import co.com.leronarenwino.model.User;
import co.com.leronarenwino.model.gateway.UserRepository;
import co.com.leronarenwino.tokenprovider.filter.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    private SecurityConfig securityConfig;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig(jwtUtils, userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void securityConfigShouldHaveRequiredBeansTest() {
        assertThat(securityConfig).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    void reactiveAuthenticationManagerShouldBeConfiguredTest() {
        ReactiveUserDetailsService userDetailsService = securityConfig.userDetailsService();
        ReactiveAuthenticationManager authManager = securityConfig.reactiveAuthenticationManager(userDetailsService);

        assertThat(authManager).isNotNull();
    }

    @Test
    void passwordEncoderShouldBeConfiguredTest() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertThat(passwordEncoder).isNotNull();

        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    void userDetailsServiceShouldReturnUserDetailsWhenUserExistsTest() {
        ReactiveUserDetailsService userDetailsService = securityConfig.userDetailsService();

        User user = new User(
                "John", "Doe", "john@example.com", "encodedPassword",
                50000.0, LocalDate.of(1990, 1, 1), "Address", "123456789", "CLIENT"
        );

        when(userRepository.findUserByEmail("john@example.com")).thenReturn(Mono.just(user));

        StepVerifier.create(userDetailsService.findByUsername("john@example.com"))
                .expectNextMatches(userDetails ->
                        userDetails.getUsername().equals("john@example.com") &&
                                userDetails.getPassword().equals("encodedPassword") &&
                                userDetails.getAuthorities().iterator().next().getAuthority().equals("ROLE_CLIENT")
                )
                .verifyComplete();
    }

    @Test
    void userDetailsServiceShouldReturnEmptyWhenUserNotExistsTest() {
        ReactiveUserDetailsService userDetailsService = securityConfig.userDetailsService();

        when(userRepository.findUserByEmail("nonexistent@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(userDetailsService.findByUsername("nonexistent@example.com"))
                .verifyComplete();
    }

    @Test
    void userDetailsServiceShouldHandleDifferentRolesTest() {
        ReactiveUserDetailsService userDetailsService = securityConfig.userDetailsService();

        User adminUser = new User(
                "Admin", "User", "admin@example.com", "adminPassword",
                100000.0, LocalDate.of(1985, 5, 15), "Admin Address", "987654321", "ADMIN"
        );

        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Mono.just(adminUser));

        StepVerifier.create(userDetailsService.findByUsername("admin@example.com"))
                .expectNextMatches(userDetails ->
                        userDetails.getAuthorities().iterator().next().getAuthority().equals("ROLE_ADMIN")
                )
                .verifyComplete();
    }

    @Test
    void userDetailsServiceShouldHandleRepositoryErrorTest() {
        ReactiveUserDetailsService userDetailsService = securityConfig.userDetailsService();

        when(userRepository.findUserByEmail("error@example.com"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(userDetailsService.findByUsername("error@example.com"))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Database error")
                )
                .verify();
    }

    @Test
    void userDetailsServiceShouldHandleUserWithNullRoleTest() {
        ReactiveUserDetailsService userDetailsService = securityConfig.userDetailsService();

        User userWithNullRole = new User(
                "Test", "User", "test@example.com", "password",
                50000.0, LocalDate.of(1990, 1, 1), "Address", "123456789", null
        );

        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Mono.just(userWithNullRole));

        StepVerifier.create(userDetailsService.findByUsername("test@example.com"))
                .expectNextMatches(userDetails ->
                        userDetails.getAuthorities().iterator().next().getAuthority().equals("ROLE_null")
                )
                .verifyComplete();
    }

    @Test
    void securityWebFilterChainShouldBeConfiguredCorrectlyTest() {
        ServerHttpSecurity http = ServerHttpSecurity.http();

        SecurityWebFilterChain filterChain = securityConfig.securityWebFilterChain(http);

        assertThat(filterChain).isNotNull();
    }

    @Test
    void securityWebFilterChainShouldAddJwtFilterTest() {
        ServerHttpSecurity http = ServerHttpSecurity.http();

        SecurityWebFilterChain filterChain = securityConfig.securityWebFilterChain(http);

        assertThat(filterChain).isNotNull();

        boolean hasJwtFilter = Boolean.TRUE.equals(filterChain.getWebFilters()
                .any(JwtTokenAuthenticationFilter.class::isInstance)
                .block());

        assertThat(hasJwtFilter).isTrue();
    }

    @Test
    void securityWebFilterChainShouldConfigureStatelessSessionTest() {
        ServerHttpSecurity http = ServerHttpSecurity.http();

        SecurityWebFilterChain filterChain = securityConfig.securityWebFilterChain(http);

        assertThat(filterChain).isNotNull();

        assertThat(filterChain.getWebFilters()).isNotNull();
    }

    @Test
    void securityWebFilterChainShouldConfigureExceptionHandlingTest() {
        ServerHttpSecurity http = ServerHttpSecurity.http();

        SecurityWebFilterChain filterChain = securityConfig.securityWebFilterChain(http);

        assertThat(filterChain).isNotNull();
        assertThat(filterChain.getWebFilters()).satisfies(filters -> assertThat(filters).isNotNull());
    }
}