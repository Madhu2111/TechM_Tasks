
package com.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode("ygP/AHLE5gAhgb9jC7+3u+4ZezPCemWImLCQlaFCdNw=")
    ); // ✅ Correct key generation

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // ✅ Fix: getUsername() instead of getEmail()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Duration.ofHours(1).toMillis())) // ⏳ 1-hour expiration
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // ✅ Fix: Correct key usage
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // ✅ Correct key usage
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT Token has expired.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("JWT Token is unsupported.");
        } catch (MalformedJwtException e) {
            throw new JwtException("JWT Token is malformed.");
        } catch (SignatureException e) {
            throw new JwtException("JWT signature is invalid.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT claims string is empty.");
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
