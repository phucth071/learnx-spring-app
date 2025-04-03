package com.learnx.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${app.jwtSecret}")
    private String SECRET_KEY;

    public String extractUserEmail(String token, HttpServletResponse response) throws IOException {
        return extractClaim(token, Claims::getSubject, response);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, HttpServletResponse response) throws IOException {
        final Claims claims = extractAllClaims(token, response);
        if(claims != null) {
            return claimsResolver.apply(claims);
        }
        return null;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put("ROLE", userDetails.getAuthorities() != null ? userDetails.getAuthorities() : "STUDENT");
        return Jwts
                .builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS512, getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, HttpServletResponse response) throws IOException {
        final String username = extractUserEmail(token, response);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, response);
    }

    public boolean isTokenExpired(String token, HttpServletResponse response) throws IOException {
        return extractExpiration(token, response).before(new Date());
    }

    private Date extractExpiration(String token, HttpServletResponse response) throws IOException {
        return extractClaim(token, Claims::getExpiration, response);
    }

    private Claims extractAllClaims(String token, HttpServletResponse response) throws IOException {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(getSignInKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Expired!");
            return null;
        } catch (MalformedJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Invalid!");
            return null;
        }
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
