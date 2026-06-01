package com.equipofutbol.equipofutbol_adso.service;

import com.equipofutbol.equipofutbol_adso.dto.MessageResponseDTO;
import com.equipofutbol.equipofutbol_adso.dto.ResultadoRequestDTO;
import com.equipofutbol.equipofutbol_adso.entity.Employees;
import com.equipofutbol.equipofutbol_adso.entity.Resultados;
import com.equipofutbol.equipofutbol_adso.repository.EmployeesRepository;
import com.equipofutbol.equipofutbol_adso.repository.ResultadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResultadoService {

    @Autowired
    private EmployeesRepository jugadorRepository;

    @Autowired
    private ResultadoRepository resultadoRepository;

    public MessageResponseDTO createResultado(ResultadoRequestDTO resultadoRequestDTO) {
        Optional<Employees> user = jugadorRepository.findByCamiseta(resultadoRequestDTO.getUsers().getNumeroCamiseta());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Error: No se encontro un jugador con el numero de camiseta " + resultadoRequestDTO.getUsers().getNumeroCamiseta());
        }
        long numeroEntrenamiento = resultadoRepository.countNumeroEntrenamiento() + 1;
        if(numeroEntrenamiento > 3) {
            throw new IllegalArgumentException("Error: El numero de entrenamiento no puede ser mayor a 3. Actualmente hay " + (numeroEntrenamiento - 1) + " entrenamientos registrados.");
        }
        

        Resultados resultado = new Resultados();
        resultado.setPasesEfectivos(resultadoRequestDTO.getPasesEfectivos());
        resultado.setVelocidadJugador(resultadoRequestDTO.getVelocidadJugador());
        resultado.setPotenciaTiro(resultadoRequestDTO.getPotenciaTiro());
        resultado.setNumeroEntrenamiento((int) numeroEntrenamiento);
        resultado.setUsers(user.get());

        resultadoRepository.save(resultado);

        return new MessageResponseDTO("Resultado del entrenamiento creado exitosamente");
    }
}
