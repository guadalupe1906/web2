package com.example.demo.controller;

import com.example.demo.DTO.FavoritoResponseDTO;
import com.example.demo.DTO.ListaRequestDTO;
import com.example.demo.DTO.ListaResponseDTO;
import com.example.demo.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.DTO.MoverFavoritosRequestDTO;

import java.util.List;

@RestController
@RequestMapping("/api/listas")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    @Operation(summary = "Crear una lista")
    @PostMapping
    public ResponseEntity<ListaResponseDTO> crear(@Valid @RequestBody ListaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(listaService.crear(request));
    }

    @Operation(summary = "Listar todas las listas")
    @GetMapping
    public ResponseEntity<List<ListaResponseDTO>> listar() {
        return ResponseEntity.ok(listaService.listarTodas());
    }

    @Operation(summary = "Obtener una lista por id")
    @GetMapping("/{id}")
    public ResponseEntity<ListaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.obtenerPorId(id));
    }

    @Operation(summary = "Listar los favoritos de una lista")
    @GetMapping("/{id}/favoritos")
    public ResponseEntity<List<FavoritoResponseDTO>> favoritos(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.favoritosDeLista(id));
    }

    @Operation(summary = "Eliminar una lista vacía", description = "Elimina una lista solo si no tiene favoritos asociados. Si la lista tiene favoritos, se lanzará un error 400.")
    @ApiResponse(responseCode = "204", description = "Lista eliminada exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        listaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mover favoritos a otra lista", description = "Mueve todos los favoritos de la lista origen a la lista destino. Se requiere el id de la lista destino en el cuerpo de la solicitud.")
    @PostMapping("/{origenId}/mover-favoritos")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Long origenId,
            @Valid @RequestBody MoverFavoritosRequestDTO request) {
        listaService.moverFavoritos(origenId, request.destinoId());
        return ResponseEntity.ok().build();
    }
}