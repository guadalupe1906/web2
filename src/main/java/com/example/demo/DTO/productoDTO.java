package com.example.demo.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Lombok: genera getters, setters, equals, hashCode y toString automáticamente
@NoArgsConstructor  // Lombok: genera un constructor vacío
@AllArgsConstructor // Lombok: genera un constructor con todos los campos

public class productoDTO {

    private Long id;
    private String titulo;
    private String categoria;
    private Double precio;
}

