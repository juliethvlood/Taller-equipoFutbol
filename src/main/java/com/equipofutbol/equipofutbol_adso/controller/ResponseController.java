package com.equipofutbol.equipofutbol_adso.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.ResultadoRequestDTO;
import com.equipofutbol.equipofutbol_adso.service.ResultadoService;

/**
 * Controlador REST que expone los endpoints para registrar los resultados de
 * los entrenamientos de los jugadores.
 * Se anota con @RestController y @RequestMapping("/resultados") para definir
 * la ruta base. Inyecta ResultadoService mediante @Autowired para delegar la
 * lógica de validación y persistencia de los resultados de entrenamiento.
 * Este controlador está excluido de la validación JWT en JwtValidationFilter
 * (shouldNotFilter), por lo que es accesible sin autenticación para facilitar
 * el registro de entrenamientos desde herramientas como Postman.
 */
@RestController
@RequestMapping("/resultados")
public class ResponseController {

    /**
     * Servicio de resultados inyectado automáticamente por Spring.
     * Se encarga de validar la existencia del jugador, controlar el límite de
     * 3 entrenamientos y persistir el nuevo resultado en la base de datos.
     */
    @Autowired
    private ResultadoService resultadoService;

    /**
     * Endpoint para registrar un nuevo resultado de entrenamiento.
     * Recibe un JSON con las métricas del entrenamiento (pases efectivos,
     * potencia de tiro, velocidad del jugador) y el número de camiseta del
     * jugador embebido en el objeto "users". Valida los campos con Jakarta
     * Validation (@Valid), y delega en ResultadoService.createResultado()
     * para procesar y guardar el resultado. Retorna un mensaje de
     * confirmación con estado HTTP 201 (CREATED) si el registro es exitoso,
     * o un error 400 si el jugador no existe o se excede el límite de
     * entrenamientos.
     * 
     * @param resultadoRequestDTO DTO con las métricas y la referencia al jugador.
     * @return ResponseEntity con MessageResponseDTO indicando el resultado.
     */
    @PostMapping
    public ResponseEntity<MessageResponseDTO> createResultado(
            @Valid @RequestBody ResultadoRequestDTO resultadoRequestDTO) {
        MessageResponseDTO messageResponseDTO = resultadoService.createResultado(resultadoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponseDTO);
    }
}
