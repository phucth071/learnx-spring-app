package com.hcmute.utezbe.security.jwt;

import com.hcmute.utezbe.domain.RequestContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final List<AntPathRequestMatcher> excludedPaths = List.of(
            new AntPathRequestMatcher("/api/v1/auth/authenticate", "POST"),
            new AntPathRequestMatcher("/api/v1/auth/oauth2/oauth-authenticate", "POST"),
            new AntPathRequestMatcher("/api/v1/auth/user/info", "GET")
    );

    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {


        final String authHeader = request.getHeader("Authorization");
        RequestContext.start();
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);
        Authentication authentication = jwtService.verifyAndGetAuthentication(jwt);
        if(authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else
        {
        System.out.println("JWTAuthenticationFilter.doFilterInternal: jwt = " + jwt);
            String userEmail = jwtService.extractUserEmail(jwt, response);
            if(userEmail != null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails, response)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludedPaths.stream().anyMatch(p -> p.matches(request));
    }

}