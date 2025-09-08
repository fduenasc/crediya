package co.com.leronarenwino.tokenprovider.util;

import co.com.leronarenwino.tokenprovider.config.JwtProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getKey().getPrivateKey());

        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Instant now = Instant.now();

        var jwtBuilder = JWT.create()
                .withIssuer(jwtProperties.getUser().getGenerator())
                .withSubject(username)
                .withClaim(jwtProperties.getClaims().getAuthoritiesKey(), authorities)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(jwtProperties.getToken().getValidity())))
                .withNotBefore(Date.from(now.plus(jwtProperties.getToken().getNotBefore())));

        if (jwtProperties.getToken().isGenerateJwtId()) {
            jwtBuilder.withJWTId(UUID.randomUUID().toString());
        }

        return jwtBuilder.sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getKey().getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtProperties.getUser().getGenerator())
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Token JWT inválido", e);
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }
}