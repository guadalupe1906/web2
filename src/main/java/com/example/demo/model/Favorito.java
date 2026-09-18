package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorito {
    private Long id;
    private Long productoId;      // referencia al producto externo (el id de DummyJSON)
    private String nota;          // nota personal del usuario
    private LocalDate fechaAgregado;
}