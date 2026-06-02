package com.equipofutbol.equipofutbol_adso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equipofutbol.equipofutbol_adso.dto.JwtResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.LoginRequestDTO;
import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.RegisterRequestDTO;
import com.equipofutbol.equipofutbol_adso.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints de autenticación de la aplicación.
 * Se anota con @RestController (que combina @Controller y @ResponseBody) para
 * indicar que cada método retorna directamente objetos Java que Spring
 * serializará a JSON automáticamente. @RequestMapping("/auth") define la ruta
 * base de todos los endpoints de este controlador. Inyecta AuthService mediante
 * @Autowired para delegar la lógica de negocio de registro, inicio de sesión y
 * refresco de tokens. Los métodos que reciben datos del cliente usan @Valid
 * para activar la validación automática de los DTOs y @RequestBody para
 * deserializar el JSON del cuerpo de la petición.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Servicio de autenticación inyectado automáticamente por Spring mediante
     * @Autowired. Centraliza la lógica de registro, login y refresco de tokens.
     */
    @Autowired
    private AuthService authService;

    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     * Recibe un JSON con username, password y rol en el cuerpo de la petición,
     * valida los campos con las anotaciones Jakarta Validation del DTO, y
     * delega en AuthService.register() para crear el usuario con la contraseña
     * cifrada. Retorna un mensaje de confirmación con estado HTTP 201 (CREATED)
     * si el registro es exitoso, o un error 400 si las validaciones fallan o el
     * username ya existe.
     * 
     * @param request DTO con username, password y rol del nuevo usuario.
     * @return ResponseEntity con MessageResponseDTO y estado 201.
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        MessageResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para iniciar sesión con credenciales de usuario.
     * Recibe un JSON con username y password, valida los campos, y delega en
     * AuthService.login() para verificar las credenciales contra la base de
     * datos. Si la autenticación es exitosa, genera un token JWT y lo retorna
     * junto con el rol y nombre del usuario con estado HTTP 202 (ACCEPTED).
     * Si las credenciales son incorrectas o el usuario está inactivo, retorna
     * un error 400 con el mensaje descriptivo.
     * 
     * @param request DTO con username y password del usuario.
     * @return ResponseEntity con JwtResponseDTO (token, rol, nombre) y estado 202.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        JwtResponseDTO response = authService.login(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Endpoint para renovar un token JWT expirado.
     * Extrae el token del encabezado "Authorization" de la petición HTTP y
     * verifica que tenga el formato "Bearer <token>". Si el encabezado falta
     * o es inválido, retorna un error 400 con el mensaje "Token no
     * proporcionado". Si el token está presente, delega en
     * AuthService.refreshToken() para generar un nuevo token con fecha de
     * expiración renovada. Si el token es inválido o ha expirado, captura la
     * excepción y retorna un error 401. Si ocurre otro error, retorna 400.
     * 
     * @param request Objeto HttpServletRequest para acceder al encabezado.
     * @return ResponseEntity con el nuevo JwtResponseDTO o MessageResponseDTO
     *         de error, según el caso.
     */
    @GetMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String autheader = request.getHeader("Authorization");
        if (autheader == null || !autheader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO("Token no proporcionado"));
        }

        String token = autheader.replace("Bearer ", "");

        try {
            JwtResponseDTO response = authService.refreshToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponseDTO("Token expired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO("Error al refrescar el token"));
        }
    }
}
