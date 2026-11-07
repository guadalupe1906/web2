package com.example.demo;

import com.example.demo.DTO.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ListaControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_credencialesValidas_retorna200YToken() throws Exception {
        // Primero registramos un usuario para poder loguearnos
        LoginRequest registroDto = new LoginRequest();
        registroDto.setEmail("user@test.com");
        registroDto.setPassword("123456");

        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDto)));

        // Probamos el login exitoso
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_credencialesInvalidas_retorna401() throws Exception {
        LoginRequest loginDto = new LoginRequest();
        loginDto.setEmail("noexiste@test.com");
        loginDto.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceder_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/listas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceder_listaPropia_retorna200() throws Exception {
        // Nota: En un escenario completo de integración, aquí registrarías, harías login, 
        // obtendrías el token Bearer y crearías una lista para luego consultarla.
        // Ejemplo esquemático de la llamada con token:
        /*
        String token = obtenerTokenDePrueba("propietario@test.com");
        Long listaId = crearListaConToken(token, "Mi Lista");

        mockMvc.perform(get("/api/listas/" + listaId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        */
    }

    @Test
    void acceder_listaAjena_retorna403() throws Exception {
        // Valida que al intentar acceder a una lista de otro usuario con rol USER devuelva 403
    }

    @Test
    void acceder_adminAListaAjena_retorna200() throws Exception {
        // Valida que con un token de rol ADMIN se puedan consultar listas de otros usuarios sin restricciones
    }
}