package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Lo que el cliente envía al crear o actualizar un favorito.
// No incluye "id" (lo asigna el servidor) ni "fechaAgregado" (la pone el sistema).
@Data
public class FavoritoRequestDTO {

    @NotNull(message = "El id del producto es obligatorio")
    private Long productoId;

    @NotBlank(message = "La nota no puede estar vacía")
    private String nota;
}