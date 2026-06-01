package com.equipofutbol.equipofutbol_adso.dto;


import lombok.Data;

@Data
public class EntrenamientoDTO {

    private int numeroEntrenamiento;
    private int pasesEfectivos;
    private long potenciaTiro;
    private long velocidadJugador;
    
    private double aportePases;
    private double aporteVelocidad;
    private double aportePotencia;
    private double puntajeEntrenamiento;

}