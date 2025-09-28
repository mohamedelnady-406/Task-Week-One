package com.example.provider;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.AuthenticationProvider;
import jakarta.inject.Singleton;

import java.util.List;



@Singleton
public class SimpleAuthenticationProvider implements AuthenticationProvider {

    @Override
    public @NonNull AuthenticationResponse authenticate(Object requestContext, @NonNull AuthenticationRequest authRequest) {
        return null;
    }

    @Override
    public @NonNull AuthenticationResponse authenticate(@NonNull AuthenticationRequest authRequest) {
        String identity = authRequest.getIdentity().toString();
        String secret   = authRequest.getSecret().toString();
        System.out.println(String.format("Authenticating {%s} / {%s}", identity, secret));
        /*
        or check a database repository
        */
        if ("user".equals(identity) && "password".equals(secret)) {
            return AuthenticationResponse.success(identity, List.of("ROLE_USER"));
        } else {
            return AuthenticationResponse.failure();
        }
    }
}
