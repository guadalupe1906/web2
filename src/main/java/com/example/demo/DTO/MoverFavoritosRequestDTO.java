package com.example.demo.DTO;
import jakarta.validation.constraints.NotNull;

public record MoverFavoritosRequestDTO(
    @NotNull(message = "El id de la lista destino es obligatorio")
    Long destinoId
) {}
