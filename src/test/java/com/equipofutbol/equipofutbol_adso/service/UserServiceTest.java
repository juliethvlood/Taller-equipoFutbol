package com.equipofutbol.equipofutbol_adso.service;

import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.UserRequestDTO;
import com.equipofutbol.equipofutbol_adso.dto.UserResponseDTO;
import com.equipofutbol.equipofutbol_adso.entity.Employees;
import com.equipofutbol.equipofutbol_adso.entity.Resultados;
import com.equipofutbol.equipofutbol_adso.enums.UserRole;
import com.equipofutbol.equipofutbol_adso.repository.EmployeesRepository;
import com.equipofutbol.equipofutbol_adso.repository.ResultadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private EmployeesRepository jugadorRepository;

    @Mock
    private ResultadoRepository resultadoRepository;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequest;
    private Employees jugador;
    private Resultados resultado1;
    private Resultados resultado2;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequestDTO();
        userRequest.setNombre("Messi");
        userRequest.setPosicion("Delantero");
        userRequest.setNumeroCamiseta(10);

        jugador = new Employees();
        jugador.setId(1L);
        jugador.setUsername("Messi");
        jugador.setPosicion("Delantero");
        jugador.setNumeroCamiseta(10);
        jugador.setRole(UserRole.JUGADOR);

        resultado1 = new Resultados();
        resultado1.setId(1L);
        resultado1.setNumeroEntrenamiento(1);
        resultado1.setPasesEfectivos(80);
        resultado1.setVelocidadJugador(70);
        resultado1.setPotenciaTiro(90);
        resultado1.setUsers(jugador);

        resultado2 = new Resultados();
        resultado2.setId(2L);
        resultado2.setNumeroEntrenamiento(2);
        resultado2.setPasesEfectivos(85);
        resultado2.setVelocidadJugador(75);
        resultado2.setPotenciaTiro(88);
        resultado2.setUsers(jugador);
    }

    @Test
    void createUser_WithNewCamiseta_ShouldCreatePlayer() {
        when(jugadorRepository.existsByNumeroCamiseta(10)).thenReturn(false);
        when(jugadorRepository.save(any(Employees.class))).thenReturn(jugador);

        MessageResponseDTO response = userService.createUser(userRequest);

        assertNotNull(response);
        assertEquals("Jugador creado exitosamente", response.getMessage());
        verify(jugadorRepository).save(any(Employees.class));
    }

    @Test
    void createUser_WithExistingCamiseta_ShouldThrowException() {
        when(jugadorRepository.existsByNumeroCamiseta(10)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(userRequest));
        assertTrue(exception.getMessage().contains("ya esta asignado"));
        verify(jugadorRepository, never()).save(any(Employees.class));
    }

    @Test
    void getByNumeroCamiseta_WithExistingPlayerAndResults_ShouldReturnData() {
        jugador.setListResultados(List.of(resultado1, resultado2));
        when(jugadorRepository.obtenerPuntajePorCamiseta(10)).thenReturn(Optional.of(jugador));

        UserResponseDTO response = userService.getByNumeroCamiseta(10);

        assertNotNull(response);
        assertEquals("Messi", response.getNombre());
        assertEquals("Delantero", response.getPosicion());
        assertEquals(10, response.getNumeroCamiseta());
        assertNotNull(response.getEntrenamientos());
        assertEquals(2, response.getEntrenamientos().size());
        assertTrue(response.getPuntajeTotal() > 0);
    }

    @Test
    void getByNumeroCamiseta_WithNonExistentPlayer_ShouldThrowException() {
        when(jugadorRepository.obtenerPuntajePorCamiseta(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getByNumeroCamiseta(99));
        assertTrue(exception.getMessage().contains("No se encontró"));
    }

    @Test
    void getByNumeroCamiseta_WithNoResults_ShouldThrowException() {
        jugador.setListResultados(List.of());
        when(jugadorRepository.obtenerPuntajePorCamiseta(10)).thenReturn(Optional.of(jugador));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getByNumeroCamiseta(10));
        assertTrue(exception.getMessage().contains("no tiene resultados"));
    }

    @Test
    void obtenerTablaDePuntajes_ShouldReturnAllPlayersWithResults() {
        Employees jugador2 = new Employees();
        jugador2.setId(2L);
        jugador2.setUsername("Ronaldo");
        jugador2.setPosicion("Delantero");
        jugador2.setNumeroCamiseta(9);

        Resultados r3 = new Resultados();
        r3.setId(3L);
        r3.setNumeroEntrenamiento(1);
        r3.setPasesEfectivos(70);
        r3.setVelocidadJugador(80);
        r3.setPotenciaTiro(95);
        r3.setUsers(jugador2);
        jugador2.setListResultados(List.of(r3));

        jugador.setListResultados(List.of(resultado1, resultado2));

        when(jugadorRepository.obtenerPuntajesDeTodosLosJugadores())
                .thenReturn(List.of(jugador, jugador2));

        List<UserResponseDTO> response = userService.obtenerTablaDePuntajes();

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void obtenerTablaDePuntajes_ShouldSkipPlayersWithoutResults() {
        Employees sinResultados = new Employees();
        sinResultados.setId(3L);
        sinResultados.setUsername("SinDatos");
        sinResultados.setListResultados(null);

        jugador.setListResultados(List.of(resultado1));

        when(jugadorRepository.obtenerPuntajesDeTodosLosJugadores())
                .thenReturn(List.of(jugador, sinResultados));

        List<UserResponseDTO> response = userService.obtenerTablaDePuntajes();

        assertEquals(1, response.size());
        assertEquals("Messi", response.get(0).getNombre());
    }

    @Test
    void obtenerTop5_WithLessThan3Trainings_ShouldReturnMessage() {
        when(resultadoRepository.countDistinctNumeroEntrenamiento()).thenReturn(2L);

        Object result = userService.obtenerTop5();

        assertTrue(result instanceof MessageResponseDTO);
        MessageResponseDTO msg = (MessageResponseDTO) result;
        assertTrue(msg.getMessage().contains("3 entrenamientos"));
    }

    @Test
    void obtenerTop5_With3OrMoreTrainings_ShouldReturnTop5() {
        jugador.setListResultados(List.of(resultado1, resultado2));

        Employees jugador2 = new Employees();
        jugador2.setId(2L);
        jugador2.setUsername("Ronaldo");
        jugador2.setNumeroCamiseta(9);
        Resultados r3 = new Resultados();
        r3.setId(3L);
        r3.setNumeroEntrenamiento(1);
        r3.setPasesEfectivos(70);
        r3.setVelocidadJugador(80);
        r3.setPotenciaTiro(95);
        r3.setUsers(jugador2);
        jugador2.setListResultados(List.of(r3));

        when(resultadoRepository.countDistinctNumeroEntrenamiento()).thenReturn(3L);
        when(jugadorRepository.obtenerPuntajesDeTodosLosJugadores())
                .thenReturn(List.of(jugador, jugador2));

        Object result = userService.obtenerTop5();

        assertFalse(result instanceof MessageResponseDTO);
        @SuppressWarnings("unchecked")
        List<UserResponseDTO> top5 = (List<UserResponseDTO>) result;
        assertFalse(top5.isEmpty());
        assertTrue(top5.size() <= 5);
    }
}
