package com.equipofutbol.equipofutbol_adso.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Componente de utilidad que permite acceder al rol del usuario autenticado
 * desde cualquier capa de la aplicación sin necesidad de recibir la petición
 * HTTP como parámetro.
 * Al anotarla con @Component, Spring la detecta durante el escaneo de componentes
 * y la registra como un bean en el contexto. Utiliza RequestContextHolder, una
 * clase de Spring que expone los atributos de la petición actual asociada al hilo
 * de ejecución. Esto resulta útil porque, una vez que el JwtValidationFilter ha
 * establecido el atributo "role" en la petición, cualquier servicio puede consultar
 * este componente para obtener el rol sin acoplarse a la API de servlet.
 */
@Component
public class SecurityContext {

    /**
     * Recupera el rol del usuario autenticado desde los atributos de la petición
     * HTTP actual. Internamente llama a RequestContextHolder.currentRequestAttributes()
     * para obtener el objeto RequestAttributes del hilo en ejecución y extrae el
     * atributo "role" que el JwtValidationFilter almacenó durante la validación del
     * token. Si el atributo no existe (por ejemplo, en una ruta que omitió el filtro),
     * retorna null en lugar de lanzar una excepción, permitiendo que el llamante
     * decida cómo manejar la ausencia de autenticación.
     * 
     * @return El nombre del rol ("ADMINISTRATOR" o "JUGADOR") si existe un token
     *         válido asociado a la petición, o null si no hay rol disponible.
     */
    public String getCurrentRole() {
        Object role = RequestContextHolder.currentRequestAttributes()
                .getAttribute("role", RequestAttributes.SCOPE_REQUEST);

        if (role != null) {
            return role.toString();
        } else {
            return null;
        }
    }
}