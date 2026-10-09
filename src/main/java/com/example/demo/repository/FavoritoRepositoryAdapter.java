package com.example.demo.repository;

import com.example.demo.model.Favorito;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository jpa;
    private final ListaJpaRepository listaJpa;
    public FavoritoRepositoryAdapter(FavoritoJpaRepository jpa, ListaJpaRepository listaJpa) {
        this.jpa = jpa;
        this.listaJpa = listaJpa;
    }

    @Override
    public Favorito save(Favorito favorito) {
        return toDomain(jpa.save(toEntity(favorito)));
    }

    @Override
    public List<Favorito> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Favorito update(Favorito favorito) {
        return toDomain(jpa.save(toEntity(favorito)));
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

     @Override
    public List<Favorito> findByListaId(Long listaId) {
        return jpa.findByListaId(listaId).stream().map(this::toDomain).toList();
    }

      // Conversión entre el dominio y la entidad de base de datos
    private Favorito toDomain(FavoritoEntity e) {
        return new Favorito(
                e.getId(),
                e.getProductoId(),
                e.getNota(),
                e.getFechaAgregado(),
                e.getLista() == null ? null : e.getLista().getId());
    }

    private FavoritoEntity toEntity(Favorito f) {
        return new FavoritoEntity(
                f.getId(),
                f.getProductoId(),
                f.getNota(),
                f.getFechaAgregado(),
                f.getListaId() == null ? null : listaJpa.getReferenceById(f.getListaId()));
    }
}

