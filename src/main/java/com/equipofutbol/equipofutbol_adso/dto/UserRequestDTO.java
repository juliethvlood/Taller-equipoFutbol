package com.equipofutbol.equipofutbol_adso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO que recibe los datos para crear un nuevo jugador en el sistema.
 * Se anota con @Data de Lombok y utiliza Jakarta Validation para garantizar
 * que los campos obligatorios estén presentes y tengan valores válidos antes
 * de llegar al servicio. El campo "rol" debe ser 1 (JUGADOR) porque este DTO
 * está diseñado exclusivamente para crear jugadores, no administradores. El
 * campo "puntajeTotal" se incluye pero no se utiliza en la entrada, ya que el
 * puntaje se calcula automáticamente a partir de los entrenamientos registrados.
 */
@Data
public class UserRequestDTO {

    /** Nombre completo del jugador. No puede estar en blanco. */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /** Posición del jugador en el campo (ej: Delantero, Defensa, Portero). No puede estar en blanco. */
    @NotBlank(message = "La posición es obligatoria")
    private String posicion;

    /** Número de camiseta del jugador. Debe ser un valor positivo y único. */
    @Positive(message = "El número de camiseta debe ser un valor positivo")
    private int numeroCamiseta;

    /**
     * Rol del usuario como valor numérico. Debe ser 1 (JUGADOR).
     * No puede ser nulo.
     */
    @NotNull(message = "El rol es obligatorio")
    private Long rol;

    /** Puntaje total del jugador (se calcula automáticamente; no se usa en la entrada). */
    private double puntajeTotal;
}
