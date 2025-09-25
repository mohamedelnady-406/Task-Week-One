package com.example.controller;


import com.example.service.JwtService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;


    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/login")
    public HttpResponse<?> signIn(@Body UsernamePasswordCredentials creds){
        String token = jwtService.generateToken(
                creds.getUsername(),
                Map.of("roles", "ROLE_USER")
        );
        return HttpResponse.ok(Map.of("access_token", token));
    }
}
