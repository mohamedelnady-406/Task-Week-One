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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;


    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/auth/login")
    public HttpResponse<?> signIn(@Body @Valid UsernamePasswordCredentials creds) {
        String token = jwtService.generateToken(
                creds.getUsername());
        return HttpResponse.ok(Map.of("access_token", token));
    }
}
