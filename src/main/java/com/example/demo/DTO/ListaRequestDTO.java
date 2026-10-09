package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ListaRequestDTO {

    @NotBlank(message = "El nombre de la lista es obligatorio")
    private String nombre;
}