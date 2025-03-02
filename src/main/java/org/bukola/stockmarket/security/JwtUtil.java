package org.bukola.stockmarket.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final MacAlgorithm MAC_ALGORITHM = Jwts.SIG.HS256;
    private static final SecretKey SECRET_KEY = MAC_ALGORITHM.key().build();
    private static final long TOKEN_VALIDITY = 3600 * 1000;

    public static String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SECRET_KEY, MAC_ALGORITHM)
                .compact();
    }

    public static boolean validateToken(String token, String expectedSubject) {
        try {
            final String subject = extractSubject(token);
            return subject.equals(expectedSubject) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public static String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}