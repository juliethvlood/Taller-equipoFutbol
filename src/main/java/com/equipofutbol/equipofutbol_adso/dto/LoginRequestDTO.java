package com.equipofutbol.equipofutbol_adso.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO que recibe las credenciales de inicio de sesión desde el cliente.
 * Se anota con @Data de Lombok para generar automáticamente getters, setters
 * y otros métodos. Las anotaciones de Jakarta Validation (@NotBlank) aseguran
 * que el cuerpo de la petición llegue con los campos obligatorios antes de que
 * el controlador procese la solicitud. Si alguno de estos campos está vacío o
 * ausente, Spring lanzará una MethodArgumentNotValidException que es capturada
 * por GlobalExceptionHandler, devolviendo un error 400 con los detalles de la
 * validación fallida.
 */
@Data
public class LoginRequestDTO {

    /** Nombre de usuario para identificar la cuenta. No puede estar en blanco. */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    /** Contraseña del usuario. No puede estar en blanco. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}