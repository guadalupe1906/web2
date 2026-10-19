package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {
    private Long id;
    private String email;
    private String passwordHash;
    private String rol; // "USER" o "ADMIN"
}