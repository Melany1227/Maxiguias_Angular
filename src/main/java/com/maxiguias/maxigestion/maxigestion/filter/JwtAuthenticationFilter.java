package com.maxiguias.maxigestion.maxigestion.filter;

import com.maxiguias.maxigestion.maxigestion.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain chain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Skip JWT processing for public endpoints
        if (requestPath.startsWith("/api/auth/") || 
            requestPath.startsWith("/h2-console/") ||
            requestPath.startsWith("/api/catalogo/") ||
            requestPath.startsWith("/api/productos/catalogo")) {
            chain.doFilter(request, response);
            return;
        }
        
        final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println("JWT Filter - Authorization header: " + (requestTokenHeader != null ? "presente" : "ausente"));

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            System.out.println("JWT Filter - Token extraído: " + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            try {
                username = jwtUtil.extractUsername(jwtToken);
                System.out.println("JWT Filter - Username extraído: " + username);
            } catch (Exception e) {
                System.out.println("No se pudo obtener el username del token JWT: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("JWT Filter - Header Authorization inválido o ausente");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("JWT Filter - Validando token para usuario: " + username);
            if (jwtUtil.validateToken(jwtToken, username)) {
                String role = jwtUtil.extractRole(jwtToken);
                System.out.println("JWT Filter - Rol extraído: " + role);
                System.out.println("JWT Filter - Creando autoridad: ROLE_" + role);
                
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("JWT Filter - Autenticación establecida para: " + username + " con rol: ROLE_" + role);
                System.out.println("JWT Filter - Authorities: " + authToken.getAuthorities().toString());
            } else {
                System.out.println("JWT Filter - Token inválido para usuario: " + username);
            }
        } else if (username == null) {
            System.out.println("JWT Filter - No se pudo extraer username del token");
        } else {
            System.out.println("JWT Filter - Usuario ya autenticado: " + SecurityContextHolder.getContext().getAuthentication().getName());
        }
        
        chain.doFilter(request, response);
    }
}