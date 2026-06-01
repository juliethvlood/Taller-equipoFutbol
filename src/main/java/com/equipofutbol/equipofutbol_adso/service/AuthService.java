package com.equipofutbol.equipofutbol_adso.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.equipofutbol.equipofutbol_adso.dto.JwtResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.LoginRequestDTO;
import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.RegisterRequestDTO;
import com.equipofutbol.equipofutbol_adso.entity.Employees;
import com.equipofutbol.equipofutbol_adso.enums.UserRole;
import com.equipofutbol.equipofutbol_adso.repository.EmployeesRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService {

    private final EmployeesRepository employeesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthService(EmployeesRepository employeesRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.employeesRepository = employeesRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public MessageResponseDTO register(RegisterRequestDTO request) {

        if (employeesRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya esta registrado: " + request.getUsername());
        }

       UserRole role;
       try {
        // Convertimos de forma segura evaluando si el índice existe en el array del Enum
        int roleIndex = request.getRol().intValue();
        role = UserRole.values()[roleIndex];
    } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
        // Ahora si mandan un índice fuera de rango, responderá limpiamente en lugar de tumbar la petición
        throw new RuntimeException("Rol inválido. Debe ser: 0 (ADMINISTRATOR), 1 (JUGADOR)");
}

        Employees employee = new Employees();
        employee.setUsername(request.getUsername());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setRole(role);
        employee.setActive(true);
        employee.setCreatedAt(LocalDateTime.now());

        employeesRepository.save(employee);

        return new MessageResponseDTO("Empleado registrado exitosamente");
    }

    public JwtResponseDTO login(LoginRequestDTO request) {

        Optional<Employees> employeeOpt = employeesRepository.findByUsername(request.getUsername());

        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Nombre de usuario no registrado: " + request.getUsername());
        }

        Employees employee = employeeOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new RuntimeException("Contrasena incorrecta");
        }

        if (!employee.isActive()) {
            throw new RuntimeException("El empleado no esta activo. Contacte al administrador");
        }

        String jwt = jwtService.generateToken(
            String.valueOf(employee.getId()),
            employee.getRole().name(),
            employee.getUsername()
        );

        return new JwtResponseDTO(jwt, employee.getRole().name(), employee.getUsername());
    }

    public JwtResponseDTO refreshToken(String token) throws Exception {
        String newToken = jwtService.refreshToken(token);
        String role = jwtService.extractRole(token);
        String username = jwtService.extractUsername(token);

        Optional<Employees> employeeOpt = employeesRepository.findByUsername(username);
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Empleado no encontrado");
        }

        return new JwtResponseDTO(newToken, role, employeeOpt.get().getUsername());
    }
}
