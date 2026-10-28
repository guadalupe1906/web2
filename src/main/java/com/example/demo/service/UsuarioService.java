package com.example.demo.service;

import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;
import com.example.demo.DTO.RegistroRequest;
import com.example.demo.DTO.UsuarioResponse;

public interface UsuarioService {
    UsuarioResponse registrar(RegistroRequest request);
    LoginResponse login(LoginRequest request);
}