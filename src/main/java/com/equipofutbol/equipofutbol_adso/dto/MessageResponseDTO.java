package com.equipofutbol.equipofutbol_adso.dto;

import lombok.Data;

/**
 * DTO genérico para transmitir mensajes de texto en las respuestas de la API,
 * tanto de éxito como de error.
 * Se anota con @Data de Lombok aunque también define getter, setter y dos
 * constructores de forma explícita. Esto permite que se use con @Autowired
 * indirectamente a través de ResponseEntity y que Jackson lo serialice
 * correctamente a JSON. Es el DTO de respuesta más utilizado en la aplicación:
 * los servicios lo retornan para confirmar operaciones exitosas (creación de
 * usuarios, registro de entrenamientos) y GlobalExceptionHandler lo usa para
 * devolver mensajes de error al cliente.
 */
@Data
public class MessageResponseDTO {
    /** Mensaje descriptivo de la operación realizada o del error ocurrido. */
    private String message;

    /**
     * Constructor con mensaje, utilizado cuando se quiere asignar el valor
     * inmediatamente al crear la instancia.
     * 
     * @param message Texto del mensaje a mostrar al cliente.
     */
    public MessageResponseDTO(String message) {
        this.message = message;
    }

    /** Constructor vacío requerido por Jackson para la deserialización JSON. */
    public MessageResponseDTO() {
    }

    /**
     * Obtiene el mensaje almacenado.
     * 
     * @return Mensaje descriptivo.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Asigna el mensaje a la respuesta.
     * 
     * @param message Texto del mensaje.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
