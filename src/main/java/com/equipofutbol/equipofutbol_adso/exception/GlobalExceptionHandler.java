package com.equipofutbol.equipofutbol_adso.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Se anota con @RestControllerAdvice, que combina @ControllerAdvice y
 * @ResponseBody, permitiendo que esta clase intercepte las excepciones lanzadas
 * por cualquier controlador y retorne respuestas JSON estructuradas en lugar de
 * páginas de error. Centraliza el manejo de tres tipos de errores: validación
 * de campos (400), excepciones de negocio (400) y errores de autorización (403).
 * Esto asegura que la API siempre responda con formato JSON consistente y códigos
 * HTTP apropiados, sin necesidad de try-catch en cada controlador.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja los errores de validación que ocurren cuando un DTO anotado con @Valid
     * no cumple las restricciones de Jakarta Validation (@NotBlank, @NotNull, etc.).
     * Se activa automáticamente cuando Spring lanza MethodArgumentNotValidException
     * al fallar la validación del cuerpo de la petición. Recorre todos los errores
     * de campo y construye un mapa donde cada clave es el nombre del campo inválido
     * y cada valor es el mensaje de error definido en la anotación. Retorna el mapa
     * con estado HTTP 400 (BAD_REQUEST).
     * 
     * @param ex Excepción con los detalles de los campos que fallaron en la validación.
     * @return Mapa con nombre de campo y mensaje de error, con estado 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Maneja las excepciones de negocio lanzadas desde los servicios y
     * controladores (por ejemplo, "El nombre de usuario ya está registrado",
     * "No se encontró un jugador con ese número de camiseta", etc.). Captura
     * cualquier RuntimeException y retorna un MessageResponseDTO con el mensaje
     * de la excepción y estado HTTP 400. Esto permite que la lógica de negocio
     * lance excepciones con mensajes descriptivos que se muestran directamente
     * al cliente sin necesidad de try-catch adicionales.
     * 
     * @param ex Excepción con el mensaje del error.
     * @return MessageResponseDTO con el mensaje de error, estado 400.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponseDTO> handleRuntimeException(RuntimeException ex) {
        MessageResponseDTO response = new MessageResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja errores de autorización específicos cuando un usuario intenta acceder
     * a recursos para los que no tiene permisos. Captura SecurityAuthorizationException
     * y retorna un mensaje de error con estado HTTP 403 (FORBIDDEN). Este manejador
     * está preparado para ser utilizado cuando se implemente la verificación de roles
     * en los controladores o servicios.
     * 
     * @param ex Excepción de seguridad con el detalle del error de autorización.
     * @return MessageResponseDTO con el mensaje de error, estado 403.
     */
    @ExceptionHandler(SecurityAuthorizationException.class)
    public ResponseEntity<MessageResponseDTO> handleSecurityException(SecurityAuthorizationException ex) {
        MessageResponseDTO response = new MessageResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}

