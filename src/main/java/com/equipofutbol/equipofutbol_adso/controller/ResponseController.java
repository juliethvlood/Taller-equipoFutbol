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

@RestController
@RequestMapping("/resultados")
public class ResponseController {

    @Autowired
    private ResultadoService resultadoService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createResultado(@Valid @RequestBody ResultadoRequestDTO resultadoRequestDTO){
        MessageResponseDTO messageResponseDTO = resultadoService.createResultado(resultadoRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponseDTO);
    } 
        

    
}
