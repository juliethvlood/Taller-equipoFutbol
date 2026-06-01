package com.equipofutbol.equipofutbol_adso.dto;


import lombok.Data;

@Data
public class JwtResponseDTO {
    private String jwt;
    private String role;
    private String name;

    public JwtResponseDTO(String jwt, String role, String name) {
        this.jwt = jwt;
        this.role = role;
        this.name = name;
    }
}
