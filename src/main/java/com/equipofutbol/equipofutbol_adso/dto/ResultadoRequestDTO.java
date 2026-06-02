package com.equipofutbol.equipofutbol_adso.dto;

import com.equipofutbol.equipofutbol_adso.entity.Employees;
import lombok.Data;

/**
 * DTO que recibe los datos de un entrenamiento realizado por un jugador.
 * Se anota con @Data de Lombok. Contiene las métricas de rendimiento del
 * jugador (pases, potencia, velocidad) y una referencia a la entidad Employees
 * para identificar a qué jugador pertenece el entrenamiento. El campo "users"
 * se utiliza en el controlador para recibir el número de camiseta del jugador
 * desde el JSON, y luego ResultadoService lo procesa para buscar al jugador
 * en la base de datos. El id se genera automáticamente y el numeroEntrenamiento
 * se asigna secuencialmente en el servicio.
 */
@Data
public class ResultadoRequestDTO {

    /** Identificador único del resultado (se asigna automáticamente en BD). */
    private Long id;

    /** Número del entrenamiento (lo asigna el servicio secuencialmente: 1, 2 o 3). */
    private int numeroEntrenamiento;

    /** Cantidad de pases efectivos realizados durante el entrenamiento. */
    private int pasesEfectivos;

    /** Potencia de tiro alcanzada durante el entrenamiento. */
    private long potenciaTiro;

    /** Velocidad del jugador registrada durante el entrenamiento. */
    private long velocidadJugador;

    /** Referencia al jugador que realizó el entrenamiento (se envía el número de camiseta en el JSON). */
    private Employees users;
}
