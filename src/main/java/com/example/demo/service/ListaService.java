package com.example.demo.service;

import com.example.demo.DTO.FavoritoResponseDTO;
import com.example.demo.DTO.ListaRequestDTO;
import com.example.demo.DTO.ListaResponseDTO;
import com.example.demo.exception.ListaConFavoritosException;
import com.example.demo.model.Favorito;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import com.example.demo.repository.UsuarioJpaRepository;
import com.example.demo.repository.UsuarioEntity; // Asegúrate de importar tu entidad de usuario
import java.util.NoSuchElementException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service 
public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;
    private final UsuarioJpaRepository usuarioJpaRepository; // Inyectado para buscar al usuario autenticado

    public ListaService(ListaRepository listaRepository, 
                        FavoritoRepository favoritoRepository, 
                        UsuarioJpaRepository usuarioJpaRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    public ListaResponseDTO crear(ListaRequestDTO requestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuario = obtenerUsuarioAutenticado(auth);

        Lista lista = new Lista();
        lista.setNombre(requestDTO.getNombre());
        lista.setUsuarioId(usuario.getId());

        return mapearLista(listaRepository.save(lista));
    }

    public List<ListaResponseDTO> listarTodas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuario = obtenerUsuarioAutenticado(auth);

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Lista> listas;
        if (isAdmin) {
            // Consigna 4.4: Si es admin, ve todas
            listas = listaRepository.findAll();
        } else {
            // Consigna 4.4: Si es usuario común, ve solo las suyas filtrando por usuarioId
            listas = listaRepository.findAll().stream()
                    .filter(l -> l.getUsuarioId() != null && l.getUsuarioId().equals(usuario.getId()))
                    .toList();
        }

        return listas.stream().map(this::mapearLista).toList();
    }

    public ListaResponseDTO obtenerPorId(Long id) {
        Lista lista = validarYObtenerListaSiTienePermiso(id);
        return mapearLista(lista);
    }

    public List<FavoritoResponseDTO> favoritosDeLista(Long id) {
        validarYObtenerListaSiTienePermiso(id); // Valida que exista y tenga permisos (403 o 404)
        
        return favoritoRepository.findByListaId(id).stream()
                .map(this::mapearFavorito)
                .toList();
    }

    public void eliminar(Long id) {
        validarYObtenerListaSiTienePermiso(id);

        if (!favoritoRepository.findByListaId(id).isEmpty()) {
            throw new ListaConFavoritosException(id);
        }
        listaRepository.deleteById(id);
    }

    @Transactional
    public void moverFavoritos(Long origenId, Long destinoId) {
        // Validar permisos sobre ambas listas
        validarYObtenerListaSiTienePermiso(origenId);
        validarYObtenerListaSiTienePermiso(destinoId);

        // Reasignar todos los favoritos de la lista origen a la destino
        var favoritosOrigen = favoritoRepository.findByListaId(origenId);
        for (var favorito : favoritosOrigen) {
            favorito.setListaId(destinoId);
            favoritoRepository.save(favorito);
        }

        // Eliminar la lista origen
        listaRepository.deleteById(origenId);
    }

    /**
     * Método auxiliar centralizado para cumplir con la Consigna 4.3:
     * - Verifica si la lista existe (lanza 404 si no).
     * - Verifica si el usuario es ADMIN o dueño de la lista (lanza 403 Forbidden si no lo es).
     */
    private Lista validarYObtenerListaSiTienePermiso(Long id) {
        Lista lista = listaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lista no encontrada con id " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuario = obtenerUsuarioAutenticado(auth);

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si no es admin y el usuarioId de la lista no coincide con el usuario logueado -> 403 Forbidden
        if (!isAdmin && (lista.getUsuarioId() == null || !lista.getUsuarioId().equals(usuario.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permisos para acceder a esta lista");
        }

        return lista;
    }

    private UsuarioEntity obtenerUsuarioAutenticado(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Long usuarioId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay un token JWT válido en la petición");
        }

        return usuarioJpaRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado no encontrado"));
    }

    private ListaResponseDTO mapearLista(Lista lista) {
        return new ListaResponseDTO(lista.getId(), lista.getNombre());
    }

    private FavoritoResponseDTO mapearFavorito(Favorito f) {
        return new FavoritoResponseDTO(f.getId(), f.getProductoId(), f.getNota(),
                f.getFechaAgregado(), f.getListaId());
    }
}