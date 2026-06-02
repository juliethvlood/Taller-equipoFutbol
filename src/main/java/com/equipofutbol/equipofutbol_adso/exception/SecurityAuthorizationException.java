package com.equipofutbol.equipofutbol_adso.exception;

/**
 * Excepción personalizada para errores de autorización en la aplicación.
 * Extiende RuntimeException, lo que permite lanzarla desde cualquier capa
 * (servicios, controladores) sin necesidad de declararla en la firma del
 * método o usar bloques try-catch obligatorios. Es capturada específicamente
 * por GlobalExceptionHandler.handleSecurityException(), que la intercepta y
 * retorna un error HTTP 403 (FORBIDDEN) al cliente. Se utiliza para indicar
 * que un usuario autenticado no tiene los permisos necesarios para realizar
 * una operación determinada, por ejemplo, cuando un JUGADOR intenta acceder
 * a una funcionalidad exclusiva de ADMINISTRATOR.
 */
public class SecurityAuthorizationException extends RuntimeException {

    /**
     * Construye una nueva excepción de autorización con un mensaje detallado.
     * El mensaje se pasa al constructor de RuntimeException y queda disponible
     * mediante getMessage(), que es utilizado por GlobalExceptionHandler para
     * incluirlo en la respuesta JSON que se envía al cliente.
     * 
     * @param message El mensaje que explica la razón del fallo de seguridad.
     */
    public SecurityAuthorizationException(String message) {
        super(message);
    }
}
