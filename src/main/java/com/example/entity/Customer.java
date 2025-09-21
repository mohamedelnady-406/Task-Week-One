package com.example.entity;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@Getter
@Table(name ="customers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Introspected
@ToString
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String email;
}
