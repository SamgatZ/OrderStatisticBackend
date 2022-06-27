package com.technodom.OrderStatisticBackend.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technodom.OrderStatisticBackend.domain.AuthenticationRequest;
import com.technodom.OrderStatisticBackend.entity.User;
import com.technodom.OrderStatisticBackend.service.UserServiceImpl;
import com.technodom.OrderStatisticBackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final JwtUtil jwtUtil;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception {
        User user;
        String accessToken;
        String refreshToken;
        Map<String,String> tokens;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getIin(), authenticationRequest.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        }
        catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        user = (User) userService.loadUserByUsername(authenticationRequest.getIin());
        accessToken = jwtUtil.generateAccessToken(user);
        refreshToken = jwtUtil.generateRefreshToken(user);
        tokens = new HashMap<>();
        tokens.put("accessToken",accessToken);
        tokens.put("refreshToken",refreshToken);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping(value = "/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DecodedJWT decodedJWT;
        String username;
        User user;
        String accessToken;
        Map<String,String> tokens;
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
                String refresh_token = authorizationHeader.substring("Bearer ".length());
            try {
                decodedJWT = jwtUtil.getDecodedJwt(refresh_token);
                username = decodedJWT.getSubject();
                user = userService.getUser(username);
                accessToken = jwtUtil.generateAccessToken(user);
                tokens = new HashMap<>();
                tokens.put("access_token",accessToken);
                tokens.put("refresh_token",refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
            } catch (Exception e) {
                response.setHeader("error",e.getMessage());
                response.setStatus(FORBIDDEN.value() );
                Map<String,String> error = new HashMap<>();
                error.put("error_message",e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
        }else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
