package com.equipofutbol.equipofutbol_adso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración global de la aplicación.
 * Al anotarla con @Configuration, Spring la reconoce como una fuente de
 * definiciones de beans que deben ser gestionados por el contenedor de
 * inversión de control (IoC). Aquí se centraliza la creación de componentes
 * que no pertenecen a una capa específica pero que son necesarios en toda la
 * aplicación, como el codificador de contraseñas. Esto evita tener que instanciar
 * manualmente estos objetos en cada servicio que los necesite y permite que
 * Spring los inyecte automáticamente donde se declaren como dependencia.
 */
@Configuration
public class AppConfig {

    /**
     * Bean de tipo PasswordEncoder implementado con BCrypt.
     * Se anota con @Bean para que Spring registre el objeto retornado en su
     * contexto de aplicación. Cada vez que un servicio (como AuthService) declare
     * un campo PasswordEncoder con @Autowired, Spring lo inyectará automáticamente.
     * BCrypt es un algoritmo de hash de contraseñas que incorpora un salt aleatorio
     * y es deliberadamente lento para dificultar ataques de fuerza bruta. Se utiliza
     * tanto para cifrar contraseñas al registrar usuarios como para verificar
     * contraseñas durante el inicio de sesión.
     * 
     * @return Instancia de BCryptPasswordEncoder lista para usar.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}                       
