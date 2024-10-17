package com.hcmute.utezbe.security.jwt;

import com.hcmute.utezbe.domain.RequestContext;
import com.hcmute.utezbe.entity.RefreshToken;
import com.hcmute.utezbe.service.RefreshTokenService;
import com.hcmute.utezbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

//        String header = request.getHeader("Authorization");
//
//        if (header == null) {
//            try {
//                filterChain.doFilter(request, response);
//            } finally {
//                RequestContext.start();
//            }
//            return;
//        }
//        final String jwt = header.substring(7);
        Cookie[] cookies = request.getCookies();
        String jwt = null;
        if (cookies != null) {
            jwt = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("access_token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        if (jwt == null) {
            try {
                filterChain.doFilter(request, response);
            } finally {
                RequestContext.start();
            }
            return;
        }
        UsernamePasswordAuthenticationToken authentication = null;
        final String userEmail = jwtService.extractUserEmail(jwt, response);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails, response)) {
                Long userId = userService.findByEmailIgnoreCase(userEmail).get().getId();
                RequestContext.setUserId(userId);
                authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}