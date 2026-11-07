package com.example.demo;

import com.example.demo.DTO.RegistroRequest;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService; // Ajusta al nombre de tu service de usuarios si difiere

    @Test
    void registrar_passwordEnTextoPlano_nuncaLaGuardaSinCifrar() {
        // Arrange
        String rawPassword = "miPasswordSeguro123";
        String encodedPassword = "$2a$10$encodedHashMockedString";

        String email = "nuevo@correo.com";
        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegistroRequest request = new RegistroRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        usuarioService.registrar(request);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals(encodedPassword, usuarioCaptor.getValue().getPasswordHash());
        assertNotEquals(rawPassword, usuarioCaptor.getValue().getPasswordHash());
        verify(passwordEncoder, times(1)).encode(rawPassword);
    }
}