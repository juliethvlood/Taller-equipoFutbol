package com.equipofutbol.equipofutbol_adso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La posición es obligatoria")
    private String posicion;

    @Positive(message = "El número de camiseta debe ser un valor positivo")
    private int numeroCamiseta;

    @NotNull(message = "El rol es obligatorio")
    private Long rol;

    private double puntajeTotal;
}
