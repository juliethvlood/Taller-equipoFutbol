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

/**
 * Servicio que gestiona el registro de los resultados de entrenamiento de los
 * jugadores.
 * Se anota con @Service para que Spring lo detecte y registre como un bean.
 * Inyecta EmployeesRepository para buscar jugadores por número de camiseta y
 * ResultadoRepository para contar los entrenamientos existentes y guardar los
 * nuevos. La lógica de negocio principal valida que el jugador exista y que no
 * se haya superado el límite de 3 entrenamientos por semana, antes de persistir
 * el resultado. Cada nuevo resultado incrementa el contador global de
 * entrenamientos, asignando el número de entrenamiento de forma secuencial
 * (1, 2, 3).
 */
@Service
public class ResultadoService {

    /**
     * Repositorio de jugadores, inyectado automáticamente por Spring mediante
     * @Autowired. Se utiliza para verificar que el jugador asociado al
     * entrenamiento exista en la base de datos antes de registrar el resultado.
     */
    @Autowired
    private EmployeesRepository jugadorRepository;

    /**
     * Repositorio de resultados, inyectado automáticamente. Se utiliza para
     * contar el número de entrenamientos ya registrados (control de límite) y
     * para persistir el nuevo resultado.
     */
    @Autowired
    private ResultadoRepository resultadoRepository;

    /**
     * Crea un nuevo resultado de entrenamiento para un jugador.
     * Primero busca al jugador por su número de camiseta en la base de datos.
     * Si no existe, lanza una IllegalArgumentException con un mensaje
     * descriptivo indicando que no se encontró al jugador. Luego calcula el
     * número del próximo entrenamiento sumando 1 al conteo actual de
     * entrenamientos registrados (countNumeroEntrenamiento() + 1). Si el
     * resultado supera 3, lanza una excepción indicando que se alcanzó el
     * límite máximo de entrenamientos. Si las validaciones son exitosas, crea
     * una nueva entidad Resultados, asigna las métricas (pases, velocidad,
     * potencia), el número de entrenamiento y el jugador asociado, y guarda
     * el resultado en la base de datos. Retorna un mensaje de confirmación.
     * 
     * @param resultadoRequestDTO DTO con las métricas del entrenamiento y la
     *                            referencia al jugador (número de camiseta).
     * @return MessageResponseDTO indicando que el resultado fue creado.
     */
    public MessageResponseDTO createResultado(ResultadoRequestDTO resultadoRequestDTO) {
        Optional<Employees> user = jugadorRepository.findByCamiseta(
            resultadoRequestDTO.getUsers().getNumeroCamiseta());
        if (user.isEmpty()) {
            throw new IllegalArgumentException(
                "Error: No se encontro un jugador con el numero de camiseta " +
                resultadoRequestDTO.getUsers().getNumeroCamiseta());
        }

        long numeroEntrenamiento = resultadoRepository.countNumeroEntrenamiento() + 1;
        if (numeroEntrenamiento > 3) {
            throw new IllegalArgumentException(
                "Error: El numero de entrenamiento no puede ser mayor a 3. Actualmente hay " +
                (numeroEntrenamiento - 1) + " entrenamientos registrados.");
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
