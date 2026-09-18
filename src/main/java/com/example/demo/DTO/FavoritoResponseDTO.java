package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Lo que la API devuelve al cliente. Sí incluye "id" y "fechaAgregado".
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoResponseDTO {
    private Long id;
    private Long productoId;
    private String nota;
    private LocalDate fechaAgregado;
}