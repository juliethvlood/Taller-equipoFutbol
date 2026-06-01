package com.equipofutbol.equipofutbol_adso.dto;

import com.equipofutbol.equipofutbol_adso.entity.Employees;


import lombok.Data;
@Data
public class ResultadoRequestDTO {

    private Long id;
    private int numeroEntrenamiento;
    private int pasesEfectivos;
    private long potenciaTiro;
    private long velocidadJugador;

    private Employees users;
}
