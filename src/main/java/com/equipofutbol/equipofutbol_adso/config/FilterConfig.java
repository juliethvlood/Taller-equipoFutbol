package com.equipofutbol.equipofutbol_adso.config;

import com.equipofutbol.equipofutbol_adso.filter.JwtValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración que registra el filtro de validación JWT en la cadena
 * de filtros de la aplicación.
 * Spring Boot gestiona los filtros de servlet mediante FilterRegistrationBean,
 * que permite definir el orden de ejecución, las URL a las que se aplica y otros
 * parámetros sin necesidad de modificar el web.xml. Al declarar este @Bean en una
 * clase @Configuration, Spring registra el filtro JwtValidationFilter para que
 * intercepte todas las peticiones entrantes antes de que lleguen a los controladores.
 * Esto es esencial para implementar la seguridad a nivel de HTTP sin depender de
 * Spring Security, manteniendo el control sobre qué rutas requieren autenticación.
 */
@Configuration
public class FilterConfig {

    /**
     * Registra el JwtValidationFilter en la cadena de filtros de la aplicación.
     * El método recibe el filtro como parámetro y Spring lo inyecta automáticamente
     * gracias a la anotación @Bean. Se crea un FilterRegistrationBean que envuelve
     * el filtro, se configura para que se aplique a todas las rutas ("/*") y se le
     * asigna el orden 1, lo que garantiza que se ejecute antes que cualquier otro
     * filtro personalizado. De esta forma, cada petición HTTP pasa primero por la
     * validación del token JWT y solo si es válida continúa hacia el controlador.
     * 
     * @param jwtValidationFilter Instancia del filtro de validación JWT, inyectada
     *                            automáticamente por Spring.
     * @return FilterRegistrationBean configurado con el filtro, las URL y el orden.
     */
    @Bean
    FilterRegistrationBean<JwtValidationFilter> jwtFilter(JwtValidationFilter jwtValidationFilter) {
        FilterRegistrationBean<JwtValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtValidationFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}