package com.equipofutbol.equipofutbol_adso.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserResponseDTO {

    private long id;
    private String nombre;
    private String posicion;
    private int numeroCamiseta;
    private double puntajeTotal;
    
    private List<EntrenamientoDTO> entrenamientos;
}