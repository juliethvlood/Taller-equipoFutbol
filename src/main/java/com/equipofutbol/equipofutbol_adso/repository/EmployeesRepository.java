package com.equipofutbol.equipofutbol_adso.repository;

import com.equipofutbol.equipofutbol_adso.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Employees.
 * Extiende JpaRepository, lo que le otorga automáticamente los métodos CRUD
 * básicos (save, findById, findAll, deleteById, etc.) sin necesidad de
 * implementarlos. Además, define consultas personalizadas mediante @Query
 * con JPQL para operaciones específicas como búsqueda por número de camiseta,
 * carga de resultados con JOIN FETCH (para evitar LazyInitializationException),
 * verificación de existencia, y consultas de puntajes. Spring Data JPA
 * implementa automáticamente los métodos de esta interfaz en tiempo de
 * ejecución, de modo que los servicios solo necesitan inyectarla y llamar
 * a los métodos.
 */
public interface EmployeesRepository extends JpaRepository<Employees, Long> {

    /**
     * Busca un jugador por su número de camiseta.
     * 
     * @param numeroCamiseta Número de camiseta del jugador.
     * @return Optional con el jugador si existe, o vacío si no se encuentra.
     */
    @Query("SELECT j FROM Employees j WHERE j.numeroCamiseta = :numeroCamiseta")
    Optional<Employees> findByCamiseta(@Param("numeroCamiseta") int numeroCamiseta);

    /**
     * Busca un jugador por número de camiseta e incluye la carga inmediata de
     * sus resultados de entrenamiento mediante LEFT JOIN FETCH. Esto evita la
     * LazyInitializationException que ocurriría si se intentara acceder a la
     * lista de resultados fuera de una transacción activa. Se usa DISTINCT
     * para eliminar duplicados que podrían generarse por el JOIN.
     * 
     * @param numeroCamiseta Número de camiseta del jugador.
     * @return Optional con el jugador y sus resultados cargados, o vacío.
     */
    @Query("SELECT DISTINCT j FROM Employees j LEFT JOIN FETCH j.listResultados WHERE j.numeroCamiseta = :numeroCamiseta")
    Optional<Employees> obtenerPuntajePorCamiseta(@Param("numeroCamiseta") int numeroCamiseta);

    /**
     * Obtiene todos los jugadores con sus resultados de entrenamiento cargados
     * mediante LEFT JOIN FETCH. Esta consulta es la base para construir la tabla
     * completa de puntajes y el ranking de los 5 mejores jugadores.
     * 
     * @return Lista de todos los jugadores con sus resultados cargados.
     */
    @Query("SELECT j FROM Employees j LEFT JOIN FETCH j.listResultados")
    List<Employees> obtenerPuntajesDeTodosLosJugadores();

    /**
     * Busca un usuario por su nombre de usuario. Se utiliza en el proceso de
     * autenticación para verificar si el username ya está registrado y para
     * obtener los datos del usuario durante el login.
     * 
     * @param username Nombre de usuario a buscar.
     * @return Optional con el usuario si existe, o vacío.
     */
    @Query("SELECT j FROM Employees j WHERE j.username = :username")
    Optional<Employees> findByUsername(@Param("username") String username);

    /**
     * Verifica si ya existe un jugador con el número de camiseta especificado.
     * Utiliza una expresión CASE para retornar true o false directamente sin
     * necesidad de procesar el resultado en el servicio.
     * 
     * @param numeroCamiseta Número de camiseta a verificar.
     * @return true si el número ya está asignado, false si está disponible.
     */
    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM Employees j WHERE j.numeroCamiseta = :numeroCamiseta")
    boolean existsByNumeroCamiseta(int numeroCamiseta);

    /**
     * Obtiene un jugador específico por su número de camiseta con sus resultados
     * cargados. Similar a obtenerPuntajePorCamiseta pero retorna una List en
     * lugar de Optional, adaptándose al método getAll() del servicio que trabaja
     * con listas.
     * 
     * @param idJugador Número de camiseta del jugador.
     * @return Lista con el jugador encontrado (vacía si no existe).
     */
    @Query("SELECT DISTINCT j FROM Employees j LEFT JOIN FETCH j.listResultados WHERE j.numeroCamiseta = :idJugador")
    List<Employees> obtenerPuntaje(@Param("idJugador") int idJugador);

    /**
     * Consulta que intenta obtener los puntajes agregados de todos los jugadores
     * usando una proyección JPQL. Actualmente no se utiliza porque el tipo de
     * retorno (List<Employees>) no coincide con la proyección SELECT. Queda como
     * referencia para una futura implementación con un DTO de proyección.
     */
    /* 
    @Query("SELECT j.nombre AS nombre, j.posicion AS posicion, " +
           "AVG((r.pasesEfectivos * 0.4) + (r.velocidadJugador * 0.3) + (r.potenciaTiro * 0.3)) AS puntajeTotal " +
           "FROM Users j JOIN j.listResultados r " +
           "GROUP BY j.id, j.nombre, j.posicion")
    List<Employees> puntajesDeTodosLosJugadores();
    */
}
