package com.example.demo.service;

import com.example.demo.DTO.FavoritoRequestDTO;
import com.example.demo.DTO.FavoritoResponseDTO;
import com.example.demo.model.Favorito;
import com.example.demo.repository.FavoritoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    // Dependemos de la INTERFAZ, no de la implementación en memoria directamente
    private final FavoritoRepository favoritoRepository;

    public FavoritoService(FavoritoRepository favoritoRepository) {
        this.favoritoRepository = favoritoRepository;
    }

    // Crea un nuevo favorito a partir de lo que mandó el cliente
    public FavoritoResponseDTO crear(FavoritoRequestDTO requestDTO) {
        Favorito favorito = new Favorito();
        favorito.setProductoId(requestDTO.getProductoId());
        favorito.setNota(requestDTO.getNota());
        favorito.setFechaAgregado(LocalDate.now()); // la fecha la pone el sistema, no el cliente

        Favorito guardado = favoritoRepository.save(favorito);
        return mapearAResponseDTO(guardado);
    }

    // Devuelve todos los favoritos
    public List<FavoritoResponseDTO> listarTodos() {
        return favoritoRepository.findAll().stream()
                .map(this::mapearAResponseDTO)
                .collect(Collectors.toList());
    }

    // Busca uno por id. Si no existe, lanza una excepción
    // (que vamos a capturar más adelante con @ControllerAdvice para devolver 404)
    public FavoritoResponseDTO obtenerPorId(Long id) {
        Favorito favorito = favoritoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Favorito no encontrado con id " + id));
        return mapearAResponseDTO(favorito);
    }

    // Actualiza un favorito existente
    public FavoritoResponseDTO actualizar(Long id, FavoritoRequestDTO requestDTO) {
        if (!favoritoRepository.existsById(id)) {
            throw new NoSuchElementException("Favorito no encontrado con id " + id);
        }

        Favorito favorito = favoritoRepository.findById(id).get();
        favorito.setProductoId(requestDTO.getProductoId());
        favorito.setNota(requestDTO.getNota());
        // la fecha de agregado NO se pisa al actualizar, se mantiene la original

        Favorito actualizado = favoritoRepository.update(favorito);
        return mapearAResponseDTO(actualizado);
    }

    // Elimina un favorito
    public void eliminar(Long id) {
        if (!favoritoRepository.existsById(id)) {
            throw new NoSuchElementException("Favorito no encontrado con id " + id);
        }
        favoritoRepository.deleteById(id);
    }

    // Traductor entidad -> DTO de salida
    private FavoritoResponseDTO mapearAResponseDTO(Favorito favorito) {
        return new FavoritoResponseDTO(
                favorito.getId(),
                favorito.getProductoId(),
                favorito.getNota(),
                favorito.getFechaAgregado()
        );
    }
}