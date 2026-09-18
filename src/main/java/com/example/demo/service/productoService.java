package com.example.demo.service;

import com.example.demo.DTO.productoDTO;
import com.example.demo.DTO.external.DummyProduct;
import com.example.demo.DTO.external.DummyProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service // le dice a Spring que esta clase es un componente de la capa de lógica de negocio,
          // y que la registre como bean para poder inyectarla en el Controller
public class productoService {

    private final RestClient dummyJsonClient; // el bean que definimos en RestClientConfig

    // Spring inyecta automáticamente el RestClient acá,
    // porque es el único bean de ese tipo que existe en el proyecto
    public productoService(RestClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

    // Trae TODOS los productos desde DummyJSON y los devuelve ya mapeados a mi DTO
    public List<productoDTO> obtenerTodos() {
        DummyProductResponse response = dummyJsonClient.get()
                .uri("/products")           // se concatena con la baseUrl -> https://dummyjson.com/products
                .retrieve()                 // ejecuta la llamada HTTP
                .body(DummyProductResponse.class); // deserializa el JSON a mi clase "espejo"

        return response.getProducts().stream()
                .map(this::mapearADto)      // transformo cada DummyProduct en un ProductoDTO
                .collect(Collectors.toList());
    }

    // Trae UN producto puntual por id
    public productoDTO obtenerPorId(Long id) {
        DummyProduct producto = dummyJsonClient.get()
                .uri("/products/{id}", id)  // reemplaza {id} por el valor real -> /products/5
                .retrieve()
                .body(DummyProduct.class);

        return mapearADto(producto);
    }

    // Método privado de mapeo: convierte el "espejo" del JSON externo en mi propio contrato
    private productoDTO mapearADto(DummyProduct producto) {
        return new productoDTO(
                producto.getId(),
                producto.getTitle(),
                producto.getCategory(),
                producto.getPrice()
        );
    }
}