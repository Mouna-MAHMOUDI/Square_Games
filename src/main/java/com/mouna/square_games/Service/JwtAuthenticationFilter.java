package com.mouna.square_games.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final com.mouna.square_games.security.JwtService jwtService;

    public JwtAuthenticationFilter(com.mouna.square_games.security.JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        System.out.println("========== JWT FILTER ==========");
        System.out.println("METHOD = " + request.getMethod());
        System.out.println("URI = " + request.getRequestURI());
        System.out.println("AUTH HEADER = " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            boolean valid = jwtService.isTokenValid(token);

            System.out.println("TOKEN VALID = " + valid);

            if (valid) {

                String userId = jwtService.extractUserId(token);
                String role = jwtService.extractRole(token);

                System.out.println("USER ID = " + userId);
                System.out.println("ROLE = " + role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority(role)
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println("AUTHENTICATION CREATED = "
                        + SecurityContextHolder
                        .getContext()
                        .getAuthentication());
            }
        }

        System.out.println("AUTHENTICATION BEFORE CONTROLLER = "
                + SecurityContextHolder.getContext().getAuthentication());

        System.out.println("==============================");


        try {
        filterChain.doFilter(request, response);
    } catch (Exception e) {
        System.out.println("========== ERREUR FILTER CHAIN ==========");
        System.out.println("TYPE = " + e.getClass().getName());
        System.out.println("MESSAGE = " + e.getMessage());
        e.printStackTrace();
        System.out.println("==========================================");
        throw e;
    }
    }
}
