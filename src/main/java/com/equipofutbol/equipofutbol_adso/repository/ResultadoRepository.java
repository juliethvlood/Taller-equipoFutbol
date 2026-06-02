package com.equipofutbol.equipofutbol_adso.repository;

import com.equipofutbol.equipofutbol_adso.entity.Resultados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repositorio JPA para la entidad Resultados.
 * Extiende JpaRepository para heredar los métodos CRUD estándar y define una
 * consulta personalizada para contar el total de entrenamientos registrados.
 * Este conteo es fundamental para la lógica de negocio que limita a un máximo
 * de 3 entrenamientos por semana, y también para asignar el número de
 * entrenamiento de forma secuencial.
 */
public interface ResultadoRepository extends JpaRepository<Resultados, Long> {

    /**
     * Cuenta la cantidad total de entrenamientos registrados en la base de datos.
     * Este valor se utiliza en ResultadoService.createResultado() para determinar
     * el número del próximo entrenamiento (count + 1) y para verificar que no se
     * haya superado el límite de 3 entrenamientos permitidos. Si el conteo actual
     * es 3 o más, el servicio rechazará el registro de un nuevo entrenamiento.
     * 
     * @return Número total de entrenamientos registrados en la base de datos.
     */
    @Query("SELECT COUNT(r.numeroEntrenamiento) FROM Resultados r ")
    long countNumeroEntrenamiento();
}
