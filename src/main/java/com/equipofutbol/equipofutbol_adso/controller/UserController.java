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

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        MessageResponseDTO messageResponseDTO = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponseDTO);
    }

    @GetMapping("/top5")
    public ResponseEntity<List<UserResponseDTO>> obtenerTop5() {
        List<UserResponseDTO> result = userService.obtenerTop5();

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("{id}")
    public ResponseEntity<List<UserResponseDTO>> getAll(@PathVariable int id ) {
        List<UserResponseDTO> response = userService.getAll(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        List<UserResponseDTO> response = userService.obtenerTop5();
        return ResponseEntity.ok(response);
    }
}
