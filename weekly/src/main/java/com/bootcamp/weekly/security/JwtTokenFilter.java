package com.bootcamp.weekly.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String LOGIN_REQUEST_URI = "/users/login";
    private final UserdetailImpl userDetailService;
    @Autowired
    JwtToken jwtToken;

    private boolean checkJWTToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HEADER);
        return authenticationHeader != null && authenticationHeader.startsWith(PREFIX);
    }

    private String getJWTToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HEADER);
        if (StringUtils.hasText(authenticationHeader) && authenticationHeader.startsWith(PREFIX)) {
            return authenticationHeader.substring(PREFIX.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();

        if (requestURI.contains(LOGIN_REQUEST_URI)) {
            logger.info("End point  :" + LOGIN_REQUEST_URI );
            chain.doFilter(request, response);
            return;
        }

        if (checkJWTToken(request)) {
            String token = getJWTToken(request);
            Claims claims = jwtToken.getAllClaimsFromToken(token);

            if (claims.get("sub") != null) {
                UserDetails userDetails = userDetailService.loadUserByUsername(claims.getSubject());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.isCredentialsNonExpired(), userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}