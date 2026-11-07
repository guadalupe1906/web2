package com.example.demo;

import com.example.demo.DTO.ListaRequestDTO;
import com.example.demo.DTO.ListaResponseDTO;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import com.example.demo.repository.UsuarioEntity;
import com.example.demo.repository.UsuarioJpaRepository;
import com.example.demo.service.ListaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListaServiceImplTest {

    @Mock
    private ListaRepository listaRepository;

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private UsuarioJpaRepository usuarioJpaRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ListaService listaService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void crear_usuarioAutenticado_asociaListaAlUsuario() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(10L);

        UsuarioEntity usuarioMock = new UsuarioEntity();
        usuarioMock.setId(10L);
        when(usuarioJpaRepository.findById(10L)).thenReturn(Optional.of(usuarioMock));

        ListaRequestDTO requestDTO = new ListaRequestDTO();
        requestDTO.setNombre("Favoritos Tech");

        when(listaRepository.save(any(Lista.class))).thenAnswer(invocation -> {
            Lista l = invocation.getArgument(0);
            l.setId(100L);
            return l;
        });

        // Act
        ListaResponseDTO resultado = listaService.crear(requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Favoritos Tech", resultado.getNombre());
        verify(listaRepository, times(1)).save(argThat(lista -> lista.getUsuarioId().equals(10L)));
    }
}