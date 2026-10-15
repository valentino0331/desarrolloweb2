package pe.edu.utp.salviafinanzas.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pe.edu.utp.salviafinanzas.security.services.UserDetailsImpl;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad Criptográfica para la Generación y Validación de Tokens JWT (JJWT 0.12.x)
 * Maneja la firma HMAC-SHA256 y control de ciclo de vida del token.
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${salviafinanzas.app.jwtSecret:SalviaFinanzasSecretKeyParaAutenticacionUTP2026SuperSeguraDe32BytesMinimo}")
    private String jwtSecret;

    @Value("${salviafinanzas.app.jwtExpirationMs:86400000}")
    private int jwtExpirationMs; // 24 horas por defecto

    private SecretKey key() {
        byte[] keyBytes;
        if (jwtSecret.length() >= 64 && isBase64(jwtSecret)) {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } else {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isBase64(String str) {
        try {
            Decoders.BASE64.decode(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera un token JWT firmado a partir del objeto de autenticación
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    /**
     * Genera un token JWT para un nombre de usuario específico
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key())
                .compact();
    }

    /**
     * Extrae el sujeto (username) del payload del token JWT
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Valida la firma criptográfica y la vigencia temporal del token
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido o malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Los claims del token JWT están vacíos: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error en validación de token JWT: {}", e.getMessage());
        }
        return false;
    }
}
