package com.technodom.OrderStatisticBackend.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technodom.OrderStatisticBackend.entity.Role;
import com.technodom.OrderStatisticBackend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader;
        String token;
        DecodedJWT decodedJWT;
        String username;
        String[] roles;
        Collection<GrantedAuthority> authorities;
        UsernamePasswordAuthenticationToken authenticationToken;
        if (request.getServletPath().equals("/api/authenticate") ||  request.getServletPath().equals("/api/token/refresh")){
            filterChain.doFilter(request,response);
        } else {
            authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){

                try {
                    token = authorizationHeader.substring("Bearer ".length());
                    decodedJWT = jwtUtil.getDecodedJwt(token);
                    username = decodedJWT.getSubject();
                    roles = decodedJWT.getClaim("roles").asArray(String.class);
                    authorities = new ArrayList<>();
                    stream(roles).forEach(role-> authorities.add(new Role(null,role)));
                    authenticationToken = new UsernamePasswordAuthenticationToken(username,null,authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request,response);
                } catch (Exception e) {
                    log.info("Error logging in: {}",e.getMessage());
                    response.setStatus(FORBIDDEN.value() );
                    Map<String,String> error = new HashMap<>();
                    error.put("error_message",e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),error);
                }
            }else {
                filterChain.doFilter(request,response);
            }
        }
    }
}
