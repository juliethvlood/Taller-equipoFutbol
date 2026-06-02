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

/**
 * Servicio de autenticación que gestiona el registro, inicio de sesión y
 * renovación de tokens JWT.
 * Se anota con @Service para que Spring lo detecte y registre como un bean
 * en el contexto de la aplicación. Inyecta EmployeesRepository para acceder
 * a los datos de los usuarios, PasswordEncoder (configurado como BCrypt en
 * AppConfig) para cifrar y verificar contraseñas, y JwtService para generar
 * y refrescar tokens. El constructor está anotado con @Autowired, lo que le
 * indica a Spring que debe resolver e inyectar las tres dependencias al
 * crear la instancia del servicio. Cada método encapsula una operación de
 * autenticación con sus validaciones específicas: unicidad del username en
 * registro, verificación de credenciales en login, y confirmación de
 * existencia del usuario en refreshToken.
 */
@Service
public class AuthService {

    private final EmployeesRepository employeesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Constructor con inyección de dependencias.
     * Spring utiliza @Autowired para resolver automáticamente los tres
     * parámetros: EmployeesRepository (acceso a BD), PasswordEncoder
     * (cifrado BCrypt) y JwtService (generación de tokens). Al ser
     * finales, estos campos se asignan una sola vez y no pueden cambiar
     * durante el ciclo de vida del bean.
     */
    @Autowired
    public AuthService(EmployeesRepository employeesRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.employeesRepository = employeesRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Primero verifica que el nombre de usuario no exista previamente en la
     * base de datos; si ya existe, lanza una RuntimeException con un mensaje
     * descriptivo. Luego convierte el valor numérico del rol (Long: 0 o 1)
     * a su correspondiente enum UserRole usando UserRole.values()[índice],
     * validando que el índice esté dentro del rango permitido (0 =
     * ADMINISTRATOR, 1 = JUGADOR). Crea una nueva entidad Employees con los
     * datos proporcionados, cifra la contraseña con BCrypt mediante
     * passwordEncoder.encode(), marca el usuario como activo y establece la
     * fecha de creación actual. Finalmente guarda el registro y retorna un
     * mensaje de éxito.
     * 
     * @param request DTO con username, password y rol del nuevo usuario.
     * @return MessageResponseDTO indicando que el registro fue exitoso.
     */
    public MessageResponseDTO register(RegisterRequestDTO request) {
        if (employeesRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya esta registrado: " + request.getUsername());
        }

        UserRole role;
        try {
            int roleIndex = request.getRol().intValue();
            role = UserRole.values()[roleIndex];
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
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

    /**
     * Autentica un usuario con sus credenciales y genera un token JWT.
     * Busca al usuario por username en la base de datos; si no existe, lanza
     * una excepción. Verifica que la contraseña proporcionada coincida con la
     * almacenada (cifrada) usando passwordEncoder.matches(). Comprueba que la
     * cuenta del usuario esté activa (active = true). Si todas las validaciones
     * son exitosas, genera un token JWT mediante jwtService.generateToken()
     * pasando el ID del usuario, el nombre del rol y el username, y retorna
     * un JwtResponseDTO con el token, el rol y el nombre del usuario.
     * 
     * @param request DTO con username y password del usuario.
     * @return JwtResponseDTO con el token JWT, rol y nombre del usuario.
     */
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

    /**
     * Refresca un token JWT generando uno nuevo a partir del token existente.
     * Llama a jwtService.refreshToken() para obtener un nuevo token con los
     * mismos claims pero nueva fecha de expiración. Extrae el rol y el username
     * del token original. Verifica que el usuario asociado al token aún exista
     * en la base de datos (por si fue eliminado después de obtener el token).
     * Si el usuario no existe, lanza una excepción. En caso de éxito, retorna
     * un nuevo JwtResponseDTO con el token renovado.
     * 
     * @param token Token JWT actual (puede estar expirado).
     * @return JwtResponseDTO con el nuevo token JWT.
     * @throws Exception Si el token es inválido o el usuario no existe.
     */
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
