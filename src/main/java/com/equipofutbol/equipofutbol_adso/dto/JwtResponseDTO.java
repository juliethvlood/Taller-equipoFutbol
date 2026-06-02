package com.equipofutbol.equipofutbol_adso.dto;

import lombok.Data;

/**
 * DTO que encapsula la respuesta de autenticación cuando el usuario inicia
 * sesión o renueva su token JWT.
 * Se anota con @Data de Lombok para generar getters, setters y otros métodos
 * automáticos, pero también incluye un constructor explícito con todos los
 * argumentos para facilitar la creación de instancias en los servicios de
 * autenticación. Este DTO viaja desde el AuthService hasta el AuthController
 * y finalmente se serializa a JSON para que el cliente pueda almacenar el token
 * en localStorage o sessionStorage y enviarlo en el encabezado Authorization
 * de las peticiones subsiguientes.
 */
@Data
public class JwtResponseDTO {
    /** Token JWT generado que el cliente debe enviar en cada petición autenticada. */
    private String jwt;

    /** Nombre del rol asignado al usuario (ADMINISTRATOR o JUGADOR). */
    private String role;

    /** Nombre de usuario autenticado. */
    private String name;

    /**
     * Constructor que inicializa los tres campos de la respuesta.
     * Se utiliza en AuthService.login() y AuthService.refreshToken() para construir
     * la respuesta después de validar las credenciales o refrescar el token.
     * 
     * @param jwt  Token JWT generado.
     * @param role Rol del usuario autenticado.
     * @param name Nombre de usuario autenticado.
     */
    public JwtResponseDTO(String jwt, String role, String name) {
        this.jwt = jwt;
        this.role = role;
        this.name = name;
    }
}
