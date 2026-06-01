package com.equipofutbol.equipofutbol_adso.filter;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.equipofutbol.equipofutbol_adso.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Filtro para validar tokens JWT 
 * Este filtro se ejecuta una vez por cada solicitud entrante y se encarga de validar el token JWT presente en el encabezado de autorización.
 * Si el token es válido, extrae la información relevante (como el nombre de usuario,
 * el ID de usuario y el ID de rol) y la establece como atributos en la solicitud para que estén disponibles en los controladores.
 * Si el token es inválido o ha expirado, el filtro responde con un error de
 * autenticación sin permitir que la solicitud llegue a los controladores.
 * Además, el filtro omite la validación para las rutas de autenticación (por ejemplo
 * "/api/v1/auth") para permitir que los usuarios puedan iniciar sesión y obtener un token JWT sin necesidad de proporcionar uno válido.
*/
@Component
public class JwtValidationFilter extends OncePerRequestFilter {
    /**
     * Servicio de jwt
     * Se inyecta para validar el token y extraer la información del mismo
     * Se utiliza la anotación @RequiredArgsConstructor de Lombok para generar un constructor que recibe este servicio como parámetro,
     * lo que permite que Spring lo inyecte automáticamente cuando se cree una instancia de este filtro.
     * Al marcar la clase con @Component, Spring la detectará automáticamente durante el escaneo de componentes y
     * la registrará como un bean en el contexto de la aplicación para que pueda ser utilizada en la configuración de filtros.
     * Esto facilita la gestión de dependencias y la integración del filtro en el flujo de solicitudes de la aplicación.
     */
    @Autowired
    private JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws IOException {
        // Obtenemos el header de autorizacion                                
        String autHeader = request.getHeader("Authorization");

        if (autHeader == null || !autHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Header is missing in the request\"}");
            return; // Cortamos la petición para que no llegue al controller
        }

        // Obtenemos el token sin el prefijo "Bearer "
        String token = autHeader.replace("Bearer ", "");

        try {
            // Validamos el token
            if (jwtService.isTokenValid(token)) {
                String username = jwtService.extractUsername(token);
                String userId = jwtService.extractUserId(token);
                String rolId = jwtService.extractRolId(token);

                request.setAttribute("username", username);
                request.setAttribute("userId", userId);
                request.setAttribute("rolId", rolId);
                request.setAttribute("role", rolId);

                // Si todo está bien, pasamos al siguiente paso, ya sea otra validación o al
                // controller directamente
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token is invalid or expired\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Validation failed\"}");
        }
    }

    /**
     * Método que determina si una solicitud debe omitir la validación del token JWT
     * @param request La solicitud HTTP
     * @return true si la solicitud debe omitir la validación, false en caso contrario
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/api/v1/auth")
            || path.startsWith("/api/v1/swagger")
            || path.startsWith("/api/v1/v3/api-docs")
            || path.contains("/api/v1/resultados")
            || path.startsWith("/api/v1/user");
    }
}
