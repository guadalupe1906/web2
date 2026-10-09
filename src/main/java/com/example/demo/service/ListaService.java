package com.example.demo.service;
import com.example.demo.DTO.FavoritoResponseDTO;
import com.example.demo.DTO.ListaRequestDTO;
import com.example.demo.DTO.ListaResponseDTO;
import com.example.demo.exception.ListaConFavoritosException;
import com.example.demo.model.Favorito;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import java.util.NoSuchElementException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service 

public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;

    public ListaService(ListaRepository listaRepository, FavoritoRepository favoritoRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    public ListaResponseDTO crear(ListaRequestDTO requestDTO) {
        Lista lista = new Lista();
        lista.setNombre(requestDTO.getNombre());
        return mapearLista(listaRepository.save(lista));
    }

    public List<ListaResponseDTO> listarTodas() {
        return listaRepository.findAll().stream().map(this::mapearLista).toList();
    }

    public ListaResponseDTO obtenerPorId(Long id) {
        Lista lista = listaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lista no encontrada con id " + id));
        return mapearLista(lista);
    }

    public List<FavoritoResponseDTO> favoritosDeLista(Long id) {
        if (!listaRepository.existsById(id)) {
            throw new NoSuchElementException("Lista no encontrada con id " + id);
        }
        return favoritoRepository.findByListaId(id).stream()
                .map(this::mapearFavorito)
                .toList();
    }

    public void eliminar(Long id) {
        if (!listaRepository.existsById(id)) {
            throw new NoSuchElementException("Lista no encontrada con id " + id);
        }
        if (!favoritoRepository.findByListaId(id).isEmpty()) {
            throw new ListaConFavoritosException(id);
        }
        listaRepository.deleteById(id);
    }

    private ListaResponseDTO mapearLista(Lista lista) {
        return new ListaResponseDTO(lista.getId(), lista.getNombre());
    }

    private FavoritoResponseDTO mapearFavorito(Favorito f) {
        return new FavoritoResponseDTO(f.getId(), f.getProductoId(), f.getNota(),
                f.getFechaAgregado(), f.getListaId());
    }

    @Transactional
    public void moverFavoritos(Long origenId, Long destinoId) {
        // 1. Validar que la lista origen exista
        if (!listaRepository.existsById(origenId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista origen no existe");
        }

        // 2. Validar que la lista destino exista
        if (!listaRepository.existsById(destinoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista destino no existe");
        }

        // 3. Reasignar todos los favoritos de la lista origen a la destino
        var favoritosOrigen = favoritoRepository.findByListaId(origenId);
        for (var favorito : favoritosOrigen) {
            favorito.setListaId(destinoId);
            favoritoRepository.save(favorito);
        }

        // 4. Eliminar la lista origen
        listaRepository.deleteById(origenId);
    }

}



