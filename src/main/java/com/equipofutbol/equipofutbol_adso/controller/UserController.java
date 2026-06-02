package com.equipofutbol.equipofutbol_adso.controller;

import com.equipofutbol.equipofutbol_adso.dto.UserRequestDTO;
import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.UserResponseDTO;
import com.equipofutbol.equipofutbol_adso.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone los endpoints para la gestión de jugadores.
 * Se anota con @RestController y @RequestMapping("/user") para definir la
 * ruta base. Inyecta UserService mediante @Autowired para delegar la lógica
 * de creación de jugadores, consulta individual y obtención del ranking de
 * los 5 mejores (equipo titular). Este controlador está excluido de la
 * validación JWT en JwtValidationFilter y es accesible sin autenticación,
 * permitiendo que cualquier cliente pueda consultar los puntajes y crear
 * nuevos jugadores.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * Servicio de jugadores inyectado automáticamente por Spring.
     * Proporciona los métodos para crear jugadores validando unicidad de
     * camiseta y rol, consultar jugadores individuales y obtener el ranking
     * de los 5 mejores puntajes promediados.
     */
    @Autowired
    private UserService userService;

    /**
     * Endpoint para crear un nuevo jugador en el sistema.
     * Recibe un JSON con nombre, posición, número de camiseta y rol del
     * jugador. Valida los campos con Jakarta Validation (@Valid) y delega
     * en UserService.createUser() para verificar que el número de camiseta
     * no esté duplicado y que el rol sea JUGADOR. Retorna un mensaje de
     * confirmación con estado HTTP 201 (CREATED) si la creación es exitosa,
     * o un error 400 si el número de camiseta ya existe o el rol es inválido.
     * 
     * @param userRequestDTO DTO con nombre, posición, número de camiseta y rol.
     * @return ResponseEntity con MessageResponseDTO y estado 201.
     */
    @PostMapping
    public ResponseEntity<MessageResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        MessageResponseDTO messageResponseDTO = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponseDTO);
    }

    /**
     * Endpoint que retorna los 5 mejores jugadores ordenados por puntaje
     * promedio descendente. Este es el endpoint clave del negocio: determina
     * el equipo titular de 5 jugadores basado en el rendimiento promedio de
     * todos los entrenamientos registrados. Delega en UserService.obtenerTop5()
     * que calcula los puntajes y limita el resultado a los 5 mejores. Si no
     * se han registrado los 3 entrenamientos semanales, el ranking se genera
     * con los datos disponibles.
     * 
     * @return ResponseEntity con la lista de los 5 mejores jugadores y estado 200.
     */
    @GetMapping("/top5")
    public ResponseEntity<List<UserResponseDTO>> obtenerTop5() {
        List<UserResponseDTO> result = userService.obtenerTop5();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Endpoint que retorna la información detallada de un jugador específico
     * por su número de camiseta, incluyendo la lista de sus entrenamientos
     * y el puntaje promedio calculado. Recibe el número de camiseta como
     * variable de ruta y delega en UserService.getAll().
     * 
     * @param id Número de camiseta del jugador a consultar.
     * @return ResponseEntity con la lista de datos del jugador y estado 200.
     */
    @GetMapping("{id}")
    public ResponseEntity<List<UserResponseDTO>> getAll(@PathVariable int id) {
        List<UserResponseDTO> response = userService.getAll(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint que retorna el ranking de los 5 mejores jugadores (equipo
     * titular). Es un alias de GET /user/top5 para cuando se accede sin
     * especificar una ruta adicional. Delega en UserService.obtenerTop5()
     * para obtener los jugadores con mayor puntaje promedio.
     * 
     * @return ResponseEntity con la lista de los 5 mejores jugadores y estado 200.
     */
    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> response = userService.obtenerTop5();
        return ResponseEntity.ok(response);
    }
}
