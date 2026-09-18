package com.example.demo.repository;

import com.example.demo.model.Favorito;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository // le avisa a Spring que esta clase es la implementación de acceso a datos
public class InMemoryFavoritoRepository implements FavoritoRepository {

    // El "almacén" en memoria: un mapa id -> Favorito
    private final Map<Long, Favorito> favoritos = new ConcurrentHashMap<>();

    // Contador para ir generando ids únicos automáticamente
    private final AtomicLong contadorId = new AtomicLong(1);

    @Override
    public Favorito save(Favorito favorito) {
        Long nuevoId = contadorId.getAndIncrement();
        favorito.setId(nuevoId);
        favoritos.put(nuevoId, favorito);
        return favorito;
    }

    @Override
    public List<Favorito> findAll() {
        return favoritos.values().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return Optional.ofNullable(favoritos.get(id));
    }

    @Override
    public Favorito update(Favorito favorito) {
        favoritos.put(favorito.getId(), favorito);
        return favorito;
    }

    @Override
    public void deleteById(Long id) {
        favoritos.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return favoritos.containsKey(id);
    }
}