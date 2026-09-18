package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// Formato uniforme para TODOS los errores de la API,
// sin importar de qué endpoint vengan
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String mensaje;
    private List<String> detalles; // por ejemplo, qué campos fallaron en la validación
}