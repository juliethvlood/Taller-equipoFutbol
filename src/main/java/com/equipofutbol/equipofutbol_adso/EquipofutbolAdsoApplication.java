package com.equipofutbol.equipofutbol_adso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 * Se anota con @SpringBootApplication, que es una meta-anotación que combina
 * @Configuration, @EnableAutoConfiguration y @ComponentScan. Esto le indica a
 * Spring que debe escanear el paquete base y todos sus subpaquetes en busca de
 * componentes, servicios, repositorios y controladores, registrándolos
 * automáticamente en el contexto de la aplicación. Al ejecutar el método main,
 * Spring Boot levanta el servidor embebido (Tomcat en este caso) en el puerto
 * configurado (9090) y monta la aplicación en el context-path (/api/v1/),
 * dejándola lista para atender peticiones REST.
 */
@SpringBootApplication
public class EquipofutbolAdsoApplication {

	/**
	 * Punto de entrada de la aplicación. Delega en SpringApplication.run() para
	 * inicializar el contenedor de IoC, configurar el contexto y arrancar el
	 * servidor web embebido. A partir de aquí, Spring se encarga de todo el ciclo
	 * de vida de los beans, la detección de componentes y la exposición de los
	 * endpoints.
	 * 
	 * @param args Argumentos de línea de comandos (soportados por Spring Boot para
	 *             sobrescribir propiedades, perfiles, etc.).
	 */
	public static void main(String[] args) {
		SpringApplication.run(EquipofutbolAdsoApplication.class, args);
	}

}
