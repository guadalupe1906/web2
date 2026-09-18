package com.example.demo.controller;

import com.example.demo.DTO.productoDTO;
import com.example.demo.service.productoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // le dice a Spring: "esta clase recibe pedidos HTTP y devuelve JSON directamente"
@RequestMapping("/api/productos") // todas las rutas de este controller empiezan con /api/productos
@Tag(name = "Productos", description = "Catálogo de productos consumido desde una API externa (DummyJSON)")
public class ProductoController {

    private final productoService productoService; // el "cerebro" que ya armamos

    // Spring inyecta automáticamente el Service acá
    public ProductoController(productoService productoService) {
        this.productoService = productoService;
    }

    @Operation(
        summary = "Lista todos los productos disponibles",
        description = "Consulta el servicio externo y devuelve el catálogo completo mapeado a nuestro DTO."
    )
    @GetMapping
    public List<productoDTO> listar() {
        return productoService.obtenerTodos();
    }

    @Operation(
        summary = "Obtiene un producto por su id",
        description = "Busca un producto por id en el catálogo externo y lo devuelve mapeado a nuestro DTO."
    )
    @GetMapping("/{id}")
    public productoDTO obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }
}