package com.hcmute.utezbe.security.jwt;

import com.auth0.jwk.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    public Authentication verifyAndGetAuthentication(String token) {
        try {
            JwkProvider provider = new UrlJwkProvider(new URL("https://www.googleapis.com/oauth2/v1/certs"));
            Jwk jwk = provider.get(Jwts.parser().parseClaimsJws(token).getHeader().getKeyId()   );
            System.out.println(jwk.getPublicKey());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("ROLE"));
            return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
        } catch (ExpiredJwtException e) {
            return null;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (InvalidPublicKeyException e) {
            throw new RuntimeException(e);
        } catch (JwkException e) {
            throw new RuntimeException(e);
        }
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
