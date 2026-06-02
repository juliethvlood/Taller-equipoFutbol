package com.equipofutbol.equipofutbol_adso.dto;

import java.util.List;
import lombok.Data;

/**
 * DTO que encapsula la respuesta con los datos completos de un jugador,
 * incluyendo la lista detallada de sus entrenamientos y su puntaje promedio.
 * Se anota con @Data de Lombok para generar automáticamente getters, setters
 * y otros métodos. Es el DTO de respuesta principal del módulo de jugadores:
 * se utiliza en los endpoints de consulta individual (GET /user/{id}) y en el
 * ranking de los 5 mejores (GET /user/top5). La lista de entrenamientos permite
 * al cliente conocer el desglose de cada sesión de entrenamiento y cómo se
 * compone el puntaje total del jugador.
 */
@Data
public class UserResponseDTO {

    /** Identificador único del jugador en la base de datos. */
    private long id;

    /** Nombre del jugador. */
    private String nombre;

    /** Posición del jugador en el campo de juego. */
    private String posicion;

    /** Número de camiseta del jugador. */
    private int numeroCamiseta;

    /** Puntaje total promedio calculado a partir de todos sus entrenamientos. */
    private double puntajeTotal;

    /** Lista de entrenamientos del jugador con el desglose de aportes y puntajes individuales. */
    private List<EntrenamientoDTO> entrenamientos;
}