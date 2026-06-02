package com.equipofutbol.equipofutbol_adso.dto;

import lombok.Data;

/**
 * DTO que representa el desglose detallado de un entrenamiento individual para
 * ser mostrado en las respuestas de la API.
 * Se anota con @Data de Lombok para que el compilador genere automáticamente
 * los métodos getter, setter, toString(), equals() y hashCode(), eliminando el
 * boilerplate de la clase. Este DTO se construye en la capa de servicio a partir
 * de la entidad Resultados y contiene los aportes ponderados de cada habilidad
 * (pases, velocidad, potencia) aplicando las fórmulas definidas en la lógica de
 * negocio, así como el puntaje total del entrenamiento que es la suma de los tres
 * aportes. Se utiliza dentro de UserResponseDTO para mostrar la trazabilidad de
 * cómo se calculó el puntaje promedio de un jugador.
 */
@Data
public class EntrenamientoDTO {

    /** Número identificador del entrenamiento (1, 2 o 3). */
    private int numeroEntrenamiento;

    /** Aporte calculado de los pases efectivos: pasesEfectivos * 0.5 */
    private double aportePases;

    /** Aporte calculado de la velocidad: velocidadJugador * 0.3 */
    private double aporteVelocidad;

    /** Aporte calculado de la potencia de tiro: potenciaTiro * 0.2 */
    private double aportePotencia;

    /** Puntaje total del entrenamiento: suma de aportePases + aporteVelocidad + aportePotencia */
    private double puntajeEntrenamiento;
}