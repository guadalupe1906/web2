package com.example.demo.controller;

import com.example.demo.DTO.FavoritoRequestDTO;
import com.example.demo.DTO.FavoritoResponseDTO;
import com.example.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @Operation(summary = "Crea un nuevo favorito")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // fuerza que la respuesta sea 201, no el 200 por defecto
    public FavoritoResponseDTO crear(@Valid @RequestBody FavoritoRequestDTO requestDTO) {
        // @Valid dispara las validaciones (@NotNull, @NotBlank) del DTO de entrada
        return favoritoService.crear(requestDTO);
    }

    @Operation(summary = "Lista todos los favoritos")
    @GetMapping
    public List<FavoritoResponseDTO> listar() {
        return favoritoService.listarTodos();
    }

    @Operation(summary = "Obtiene un favorito por su id")
    @GetMapping("/{id}")
    public FavoritoResponseDTO obtenerPorId(@PathVariable Long id) {
        return favoritoService.obtenerPorId(id);
    }

    @Operation(summary = "Actualiza un favorito existente")
    @PutMapping("/{id}")
    public FavoritoResponseDTO actualizar(@PathVariable Long id,
                                          @Valid @RequestBody FavoritoRequestDTO requestDTO) {
        return favoritoService.actualizar(id, requestDTO);
    }

    @Operation(summary = "Elimina un favorito")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // fuerza 204, sin cuerpo de respuesta
    public void eliminar(@PathVariable Long id) {
        favoritoService.eliminar(id);
    }
}