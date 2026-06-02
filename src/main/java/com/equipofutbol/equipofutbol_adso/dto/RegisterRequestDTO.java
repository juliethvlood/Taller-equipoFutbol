package com.equipofutbol.equipofutbol_adso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO que recibe los datos de registro de un nuevo usuario desde el cliente.
 * Se anota con @Data de Lombok y utiliza Jakarta Validation para garantizar
 * que los campos obligatorios lleguen en la petición. El campo "rol" se define
 * como Long para permitir que el cliente envíe 0 (ADMINISTRATOR) o 1 (JUGADOR)
 * como valor numérico, y posteriormente AuthService lo convierte al enum
 * UserRole usando el índice del array de valores. Esta estrategia evita que el
 * cliente tenga que conocer los nombres exactos del enum y simplifica la
 * integración desde aplicaciones frontend o Postman.
 */
@Data
public class RegisterRequestDTO {

    /** Nombre de usuario para la nueva cuenta. No puede estar en blanco. */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    /** Contraseña del nuevo usuario. No puede estar en blanco. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    /**
     * Rol asignado al usuario como valor numérico: 0 = ADMINISTRATOR,
     * 1 = JUGADOR. No puede ser nulo.
     */
    @NotNull(message = "El rol es obligatorio")
    private Long rol;
}