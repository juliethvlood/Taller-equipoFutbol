package com.equipofutbol.equipofutbol_adso.service;

import com.equipofutbol.equipofutbol_adso.config.SecurityContext;
import com.equipofutbol.equipofutbol_adso.dto.EntrenamientoDTO;
import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.UserRequestDTO;
import com.equipofutbol.equipofutbol_adso.dto.UserResponseDTO;
import com.equipofutbol.equipofutbol_adso.entity.Resultados;
import com.equipofutbol.equipofutbol_adso.enums.UserRole;
import com.equipofutbol.equipofutbol_adso.entity.Employees;
import com.equipofutbol.equipofutbol_adso.repository.EmployeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class UserService {

    @Autowired
    private EmployeesRepository jugadorRepository;

    public MessageResponseDTO createUser(UserRequestDTO userRequestDTO) {

        if (jugadorRepository.existsByNumeroCamiseta(userRequestDTO.getNumeroCamiseta())) {
            throw new IllegalArgumentException("El numero de camiseta " + userRequestDTO.getNumeroCamiseta() + " ya esta asignado a otro jugador.");
        }

          UserRole role;
       try {
        // Convertimos de forma segura evaluando si el índice existe en el array del Enum
        int roleIndex = userRequestDTO.getRol().intValue();
        role = UserRole.values()[roleIndex];
        if(role != UserRole.JUGADOR){
            throw new IllegalArgumentException("El rol asignado no es válido para un jugador. Debe ser: 1 (JUGADOR)");
        }
    } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
        // Ahora si mandan un índice fuera de rango, responderá limpiamente en lugar de tumbar la petición
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

    public List<UserResponseDTO> getAll(int numeroCamiseta){
        List<UserResponseDTO> response = new ArrayList<>();
        List<Employees> responseItem = jugadorRepository.obtenerPuntaje(numeroCamiseta);
        response.addAll(mapToResponse(responseItem));
        return response;
    }

    public List<UserResponseDTO> obtenerTablaDePuntajes() {
        List<Employees> listaPuntajes = jugadorRepository.obtenerPuntajesDeTodosLosJugadores();
        return mapToResponse(listaPuntajes);
    }

    public List<UserResponseDTO> obtenerTop5() {
        return obtenerTablaDePuntajes().stream()
                .sorted(Comparator.comparingDouble(UserResponseDTO::getPuntajeTotal).reversed())
                .limit(5)
                .toList();
    }

    private List<UserResponseDTO> mapToResponse(List<Employees> users) {
        List<UserResponseDTO> response = new ArrayList<>();

        for (Employees item : users) {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(item.getId());
            dto.setNombre(item.getUsername());
            dto.setPosicion(item.getPosicion());
            dto.setNumeroCamiseta(item.getNumeroCamiseta());

            List<EntrenamientoDTO> entrenamientos = item.getListResultados().stream()
                    .map(r -> mapToEntrenamiento(r))
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

    private EntrenamientoDTO mapToEntrenamiento(Resultados r) {
        EntrenamientoDTO e = new EntrenamientoDTO();

        e.setNumeroEntrenamiento(r.getNumeroEntrenamiento());
        e.setPasesEfectivos(r.getPasesEfectivos());
        e.setPotenciaTiro(r.getPotenciaTiro());
        e.setVelocidadJugador(r.getVelocidadJugador());

        e.setAportePases(r.getPasesEfectivos() * 0.5);
        e.setAporteVelocidad(r.getVelocidadJugador() * 0.3);
        e.setAportePotencia(r.getPotenciaTiro() * 0.2);
        e.setPuntajeEntrenamiento(e.getAportePases() + e.getAporteVelocidad() + e.getAportePotencia());
        return e;
    }

}
