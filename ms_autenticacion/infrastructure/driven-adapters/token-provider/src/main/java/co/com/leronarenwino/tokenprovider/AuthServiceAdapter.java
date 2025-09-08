package co.com.leronarenwino.tokenprovider;

import co.com.leronarenwino.model.gateway.AuthService;
import co.com.leronarenwino.model.Credentials;
import co.com.leronarenwino.model.Auth;
import co.com.leronarenwino.tokenprovider.config.JwtProperties;
import co.com.leronarenwino.tokenprovider.util.JwtUtils;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceAdapter implements AuthService {

    private final JwtUtils jwtUtils;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;

    public AuthServiceAdapter(JwtUtils jwtUtils, ReactiveAuthenticationManager authenticationManager, JwtProperties jwtProperties) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public Mono<Auth> login(Credentials credentials) {
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        credentials.email(),
                        credentials.password()))
                .map(authentication -> {
                    String token = jwtUtils.createToken(authentication);
                    long expiresIn = jwtProperties.getToken().getValidity().toSeconds();
                    return new Auth(token, expiresIn);
                })
                .onErrorMap(ignored -> new IllegalArgumentException("Invalid credentials"));
    }

    @Override
    public Mono<String> validateTokenAndExtractUsername(String token) {
        return Mono.fromCallable(() -> {
            try {
                DecodedJWT decodedJWT = jwtUtils.validateToken(token);
                return jwtUtils.extractUsername(decodedJWT);
            } catch (JWTVerificationException e) {
                throw new IllegalArgumentException("Invalid or expired token");
            }
        });
    }

}