package com.example.dtos;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Serdeable
@Builder
@Data
public class CustomerDTO {
    String name;
    String email;
}
