package com.example.dtos;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Serdeable
@Builder
@Data
@Introspected
public class CustomerDTO {
    @NotNull
    String name;

    @NotNull
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Invalid email format"
    )
    String email;
}
