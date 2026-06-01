package com.equipofutbol.equipofutbol_adso.service;

import com.equipofutbol.equipofutbol_adso.dto.JwtResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.LoginRequestDTO;
import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.RegisterRequestDTO;
import com.equipofutbol.equipofutbol_adso.entity.Employees;
import com.equipofutbol.equipofutbol_adso.enums.UserRole;
import com.equipofutbol.equipofutbol_adso.repository.EmployeesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmployeesRepository employeesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private Employees employee;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setRol(1L);

        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        employee = new Employees();
        employee.setId(1L);
        employee.setUsername("testuser");
        employee.setPassword("encodedPassword");
        employee.setRole(UserRole.JUGADOR);
        employee.setActive(true);
    }

    @Test
    void register_WithNewUsername_ShouldCreateEmployee() {
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(employeesRepository.save(any(Employees.class))).thenReturn(employee);

        MessageResponseDTO response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Empleado registrado exitosamente", response.getMessage());
        verify(employeesRepository).save(any(Employees.class));
    }

    @Test
    void register_WithExistingUsername_ShouldThrowException() {
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.of(employee));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));
        assertTrue(exception.getMessage().contains("ya esta registrado"));
        verify(employeesRepository, never()).save(any(Employees.class));
    }

    @Test
    void register_WithAdminRoleIndex_ShouldCreateAdmin() {
        registerRequest.setRol(0L);
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(employeesRepository.save(any(Employees.class))).thenReturn(employee);

        authService.register(registerRequest);

        verify(employeesRepository).save(argThat(e -> e.getRole() == UserRole.ADMINISTRATOR));
    }

    @Test
    void register_WithInvalidRoleIndex_ShouldThrowException() {
        registerRequest.setRol(99L);
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));
        assertTrue(exception.getMessage().contains("Rol inválido"));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("1", "JUGADOR", "testuser")).thenReturn("test-jwt-token");

        JwtResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("test-jwt-token", response.getJwt());
        assertEquals("JUGADOR", response.getRole());
        assertEquals("testuser", response.getName());
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertTrue(exception.getMessage().contains("no registrado"));
    }

    @Test
    void login_WithWrongPassword_ShouldThrowException() {
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertTrue(exception.getMessage().contains("incorrecta"));
    }

    @Test
    void login_WithInactiveEmployee_ShouldThrowException() {
        employee.setActive(false);
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertTrue(exception.getMessage().contains("no esta activo"));
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewToken() throws Exception {
        String oldToken = "old-jwt";
        String newToken = "new-jwt";

        when(jwtService.refreshToken(oldToken)).thenReturn(newToken);
        when(jwtService.extractRole(oldToken)).thenReturn("JUGADOR");
        when(jwtService.extractUsername(oldToken)).thenReturn("testuser");
        when(employeesRepository.findByUsername("testuser")).thenReturn(Optional.of(employee));

        JwtResponseDTO response = authService.refreshToken(oldToken);

        assertNotNull(response);
        assertEquals(newToken, response.getJwt());
        assertEquals("JUGADOR", response.getRole());
        assertEquals("testuser", response.getName());
    }

    @Test
    void refreshToken_WithTokenOfNonExistentUser_ShouldThrowException() throws Exception {
        when(jwtService.refreshToken("token")).thenReturn("new-token");
        when(jwtService.extractRole("token")).thenReturn("JUGADOR");
        when(jwtService.extractUsername("token")).thenReturn("unknown");
        when(employeesRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.refreshToken("token"));
        assertTrue(exception.getMessage().contains("no encontrado"));
    }
}
