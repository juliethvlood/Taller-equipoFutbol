package com.equipofutbol.equipofutbol_adso.service;

import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.ResultadoRequestDTO;
import com.equipofutbol.equipofutbol_adso.entity.Employees;
import com.equipofutbol.equipofutbol_adso.entity.Resultados;
import com.equipofutbol.equipofutbol_adso.repository.EmployeesRepository;
import com.equipofutbol.equipofutbol_adso.repository.ResultadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private EmployeesRepository jugadorRepository;

    @Mock
    private ResultadoRepository resultadoRepository;

    @InjectMocks
    private ResultadoService resultadoService;

    private ResultadoRequestDTO request;
    private Employees jugador;

    @BeforeEach
    void setUp() {
        request = new ResultadoRequestDTO();
        request.setNumeroEntrenamiento(1);
        request.setPasesEfectivos(80);
        request.setPotenciaTiro(90);
        request.setVelocidadJugador(70);
        request.setNumeroCamiseta(10);

        jugador = new Employees();
        jugador.setId(1L);
        jugador.setUsername("Messi");
        jugador.setNumeroCamiseta(10);
    }

    @Test
    void createResultado_WithValidData_ShouldCreateSuccessfully() {
        when(jugadorRepository.findByCamiseta(10)).thenReturn(Optional.of(jugador));
        when(resultadoRepository.save(any(Resultados.class))).thenReturn(new Resultados());

        MessageResponseDTO response = resultadoService.createResultado(request);

        assertNotNull(response);
        assertEquals("Resultado del entrenamiento 1 creado exitosamente", response.getMessage());
        verify(resultadoRepository).save(any(Resultados.class));
    }

    @Test
    void createResultado_WithNonExistentPlayer_ShouldThrowException() {
        when(jugadorRepository.findByCamiseta(99)).thenReturn(Optional.empty());
        request.setNumeroCamiseta(99);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resultadoService.createResultado(request));
        assertTrue(exception.getMessage().contains("No se encontro"));
        verify(resultadoRepository, never()).save(any(Resultados.class));
    }

    @Test
    void createResultado_ShouldSetCorrectFields() {
        when(jugadorRepository.findByCamiseta(10)).thenReturn(Optional.of(jugador));
        when(resultadoRepository.save(any(Resultados.class))).thenAnswer(i -> i.getArgument(0));

        resultadoService.createResultado(request);

        verify(resultadoRepository).save(argThat(r ->
            r.getNumeroEntrenamiento() == 1 &&
            r.getPasesEfectivos() == 80 &&
            r.getPotenciaTiro() == 90 &&
            r.getVelocidadJugador() == 70 &&
            r.getUsers().getId() == 1L
        ));
    }

    @Test
    void createResultado_WithTraining2_ShouldSucceed() {
        request.setNumeroEntrenamiento(2);
        when(jugadorRepository.findByCamiseta(10)).thenReturn(Optional.of(jugador));
        when(resultadoRepository.save(any(Resultados.class))).thenReturn(new Resultados());

        MessageResponseDTO response = resultadoService.createResultado(request);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("entrenamiento 2"));
    }

    @Test
    void createResultado_WithTraining3_ShouldSucceed() {
        request.setNumeroEntrenamiento(3);
        when(jugadorRepository.findByCamiseta(10)).thenReturn(Optional.of(jugador));
        when(resultadoRepository.save(any(Resultados.class))).thenReturn(new Resultados());

        MessageResponseDTO response = resultadoService.createResultado(request);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("entrenamiento 3"));
    }
}
