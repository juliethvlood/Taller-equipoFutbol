package com.equipofutbol.equipofutbol_adso.service;

import com.equipofutbol.equipofutbol_adso.dto.EntrenamientoDTO;
import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.UserRequestDTO;
import com.equipofutbol.equipofutbol_adso.dto.UserResponseDTO;
import com.equipofutbol.equipofutbol_adso.entity.Resultados;
import com.equipofutbol.equipofutbol_adso.enums.UserRole;
import com.equipofutbol.equipofutbol_adso.exception.SecurityAuthorizationException;
import com.equipofutbol.equipofutbol_adso.entity.Employees;
import com.equipofutbol.equipofutbol_adso.repository.EmployeesRepository;
import com.equipofutbol.equipofutbol_adso.config.SecurityContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Servicio que gestiona la creación y consulta de jugadores, así como el
 * cálculo de puntajes y la generación del ranking de los 5 mejores.
 * Se anota con @Service para que Spring lo detecte y registre como un bean.
 * Inyecta EmployeesRepository para acceder a los datos de los jugadores y
 * sus resultados. Contiene la lógica de negocio central de la aplicación:
 * creación de jugadores con validación de unicidad de camiseta y rol,
 * cálculo del puntaje de cada entrenamiento aplicando la fórmula ponderada
 * (pases * 0.5 + velocidad * 0.3 + potencia * 0.2), y determinación del
 * equipo titular (top 5) ordenando los jugadores por su puntaje promedio
 * descendente. Los métodos privados mapToResponse() y mapToEntrenamiento()
 * centralizan la conversión de entidades a DTOs para mantener la consistencia.
 */
@Service
public class UserService {

    /**
     * Repositorio de jugadores inyectado automáticamente por Spring mediante
     * @Autowired. Proporciona acceso a los métodos de consulta personalizados
     * como obtenerPuntajesDeTodosLosJugadores(), obtenerPuntajePorCamiseta() y
     * existsByNumeroCamiseta().
     */
    @Autowired
    private EmployeesRepository jugadorRepository;

    @Autowired
    private SecurityContext security;

    /**
     * Método privado para reutilizar la lógica de validación de administrador.
     */
    private void validateAdminRole() {
        if (!UserRole.ADMINISTRATOR.name().equals(security.getCurrentRole())) {
            throw new SecurityAuthorizationException("EL rol: '" + security.getCurrentRole() + "' no esta permitido");
        }
    }
    /**
     * Crea un nuevo jugador en el sistema.
     * Primero verifica que el número de camiseta no esté ya asignado a otro
     * jugador usando existsByNumeroCamiseta(); si ya existe, lanza una
     * IllegalArgumentException. Luego convierte el valor numérico del rol
     * (Long) al enum UserRole y valida que sea JUGADOR (índice 1), lanzando
     * una excepción si el rol es ADMINISTRATOR o está fuera de rango. Crea
     * una nueva entidad Employees con nombre, posición, número de camiseta
     * y rol, y la guarda en la base de datos. Retorna un mensaje de éxito.
     * 
     * @param userRequestDTO DTO con nombre, posición, número de camiseta y rol.
     * @return MessageResponseDTO indicando que el jugador fue creado.
     */
    public MessageResponseDTO createUser(UserRequestDTO userRequestDTO) {

        validateAdminRole();


        if (jugadorRepository.existsByNumeroCamiseta(userRequestDTO.getNumeroCamiseta())) {
            throw new IllegalArgumentException(
                "El numero de camiseta " + userRequestDTO.getNumeroCamiseta() +
                " ya esta asignado a otro jugador.");
        }

        UserRole role;
        try {
            int roleIndex = userRequestDTO.getRol().intValue();
            role = UserRole.values()[roleIndex];
            if (role != UserRole.JUGADOR) {
                throw new IllegalArgumentException(
                    "El rol asignado no es válido para un jugador. Debe ser: 1 (JUGADOR)");
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido. Debe ser: 0 (ADMINISTRATOR), 1 (JUGADOR)");
        }

        Employees user = new Employees();
        user.setUsername(userRequestDTO.getNombre());
        user.setPosicion(userRequestDTO.getPosicion());
        user.setNumeroCamiseta(userRequestDTO.getNumeroCamiseta());
        user.setRole(role);

        jugadorRepository.save(user);
        return new MessageResponseDTO("Jugador creado exitosamente");
    }

    /**
     * Obtiene la información de un jugador específico por su número de camiseta,
     * incluyendo la lista de sus entrenamientos y el puntaje promedio calculado.
     * Consulta el repositorio usando obtenerPuntaje() que retorna una lista con
     * el jugador y sus resultados cargados mediante JOIN FETCH. Mapea la entidad
     * a UserResponseDTO usando mapToResponse().
     * 
     * @param numeroCamiseta Número de camiseta del jugador a consultar.
     * @return Lista con un único UserResponseDTO con los datos del jugador.
     */
    public List<UserResponseDTO> getAll(int numeroCamiseta) {

        if (UserRole.ADMINISTRATOR.name().equals(security.getCurrentRole()) && UserRole.JUGADOR.name().equals(security.getCurrentRole())) {
            throw new SecurityAuthorizationException("EL rol: '" + security.getCurrentRole() + "' no esta permitido");
        }

        List<UserResponseDTO> response = new ArrayList<>();
        List<Employees> responseItem = jugadorRepository.obtenerPuntaje(numeroCamiseta);
        response.addAll(mapToResponse(responseItem));
        return response;
    }

    /**
     * Obtiene la tabla completa de todos los jugadores con sus puntajes
     * promedio calculados a partir de todos sus entrenamientos.
     * Consulta el repositorio usando obtenerPuntajesDeTodosLosJugadores(), que
     * trae todos los jugadores con sus resultados cargados. Luego mapea cada
     * entidad a UserResponseDTO, calculando el promedio de los puntajes de
     * entrenamiento. Este método es la base para la generación del ranking y
     * es utilizado por obtenerTop5().
     * 
     * @return Lista de todos los jugadores con sus puntajes y entrenamientos.
     */
    public List<UserResponseDTO> obtenerTablaDePuntajes() {
        List<Employees> listaPuntajes = jugadorRepository.obtenerPuntajesDeTodosLosJugadores();
        return mapToResponse(listaPuntajes);
    }

    /**
     * Obtiene los 5 mejores jugadores ordenados por puntaje total promedio
     * descendente. Llama a obtenerTablaDePuntajes() para obtener todos los
     * jugadores, luego aplica un stream que los ordena por puntajeTotal de
     * mayor a menor usando Comparator.comparingDouble().reversed() y limita
     * el resultado a 5 elementos. Este es el método clave para determinar el
     * equipo titular de 5 jugadores basado en el rendimiento de los
     * entrenamientos registrados. Si no hay suficientes jugadores o
     * entrenamientos, retorna una lista con los que estén disponibles.
     * 
     * @return Lista de los 5 jugadores con mayor puntaje promedio, ordenados
     *         del mejor al peor.
     */
    public List<UserResponseDTO> obtenerTop5() {

        if (UserRole.ADMINISTRATOR.name().equals(security.getCurrentRole())) {
            throw new SecurityAuthorizationException("EL rol: '" + security.getCurrentRole() + "' no esta permitido");
        }
        
        return obtenerTablaDePuntajes().stream()
                .sorted(Comparator.comparingDouble(UserResponseDTO::getPuntajeTotal).reversed())
                .limit(5)
                .toList();
    }

    /**
     * Convierte una lista de entidades Employees a lista de UserResponseDTO.
     * Para cada jugador, extrae ID, nombre, posición y número de camiseta.
     * Convierte cada resultado de entrenamiento a EntrenamientoDTO usando
     * mapToEntrenamiento(). Calcula el puntaje total promedio como el promedio
     * de los puntajes de todos los entrenamientos del jugador. Si el jugador
     * no tiene entrenamientos, el promedio es 0.0.
     * 
     * @param users Lista de entidades Employees a mapear.
     * @return Lista de UserResponseDTO con los datos de los jugadores.
     */
    private List<UserResponseDTO> mapToResponse(List<Employees> users) {
        List<UserResponseDTO> response = new ArrayList<>();

        for (Employees item : users) {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(item.getId());
            dto.setNombre(item.getUsername());
            dto.setPosicion(item.getPosicion());
            dto.setNumeroCamiseta(item.getNumeroCamiseta());

            List<EntrenamientoDTO> entrenamientos = item.getListResultados().stream()
                    .map(this::mapToEntrenamiento)
                    .toList();

            dto.setEntrenamientos(entrenamientos);

            double promedio = entrenamientos.stream()
                    .mapToDouble(EntrenamientoDTO::getPuntajeEntrenamiento)
                    .average()
                    .orElse(0.0);

            dto.setPuntajeTotal(promedio);
            response.add(dto);
        }

        return response;
    }

    /**
     * Convierte una entidad Resultados a EntrenamientoDTO aplicando la fórmula
     * de puntuación del negocio. Calcula los aportes ponderados de cada
     * habilidad: aportePases = pasesEfectivos * 0.5, aporteVelocidad =
     * velocidadJugador * 0.3, aportePotencia = potenciaTiro * 0.2. El puntaje
     * del entrenamiento es la suma de los tres aportes. Esta fórmula da mayor
     * peso a los pases (50%), luego a la velocidad (30%) y finalmente a la
     * potencia (20%).
     * 
     * @param r Entidad Resultados con los datos del entrenamiento.
     * @return EntrenamientoDTO con los aportes y puntaje calculados.
     */
    private EntrenamientoDTO mapToEntrenamiento(Resultados r) {
        EntrenamientoDTO e = new EntrenamientoDTO();
        e.setNumeroEntrenamiento(r.getNumeroEntrenamiento());
        e.setAportePases(r.getPasesEfectivos() * 0.5);
        e.setAporteVelocidad(r.getVelocidadJugador() * 0.3);
        e.setAportePotencia(r.getPotenciaTiro() * 0.2);
        e.setPuntajeEntrenamiento(e.getAportePases() + e.getAporteVelocidad() + e.getAportePotencia());
        return e;
    }

    /**
     * Obtiene el puntaje detallado de un jugador específico por su número de
     * camiseta. Busca el jugador con sus resultados cargados usando
     * obtenerPuntajePorCamiseta(), lanzando una RuntimeException si no existe.
     * Convierte cada resultado a EntrenamientoDTO, calcula el promedio de
     * puntajes y retorna un UserResponseDTO completo con los datos del
     * jugador, su lista de entrenamientos y el puntaje total promedio.
     * 
     * @param numeroCamiseta Número de camiseta del jugador a consultar.
     * @return UserResponseDTO con los datos completos y puntaje del jugador.
     */
    public UserResponseDTO obtenerPuntajePorCamiseta(int numeroCamiseta) {
        Employees jugador = jugadorRepository.obtenerPuntajePorCamiseta(numeroCamiseta)
                .orElseThrow(() -> new RuntimeException(
                    "Jugador con numero de camiseta " + numeroCamiseta + " no encontrado."));

        List<EntrenamientoDTO> entrenamientos = jugador.getListResultados().stream()
                .map(this::mapToEntrenamiento)
                .toList();

        double promedio = entrenamientos.stream()
                .mapToDouble(EntrenamientoDTO::getPuntajeEntrenamiento)
                .average()
                .orElse(0.0);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(jugador.getId());
        response.setNombre(jugador.getUsername());
        response.setPosicion(jugador.getPosicion());
        response.setNumeroCamiseta(jugador.getNumeroCamiseta());
        response.setEntrenamientos(entrenamientos);
        response.setPuntajeTotal(promedio);

        return response;
    }
}
