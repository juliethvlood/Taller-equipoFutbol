package com.equipofutbol.equipofutbol_adso.enums;

/**
 * Enumeración que define los roles de usuario disponibles en el sistema.
 * El orden de declaración de los valores es crítico porque los servicios
 * (AuthService y UserService) convierten valores numéricos recibidos como Long
 * en los DTOs a su correspondiente posición en el array del enum usando
 * UserRole.values()[indice]. De esta forma, ADMINISTRATOR corresponde al índice
 * 0 y JUGADOR al índice 1. Si se agregaran nuevos roles en el futuro, deberán
 * añadirse al final para no romper la correspondencia con los valores existentes.
 */
public enum UserRole {
    ADMINISTRATOR,
    JUGADOR
}

