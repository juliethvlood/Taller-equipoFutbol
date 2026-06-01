package com.equipofutbol.equipofutbol_adso.repository;

import com.equipofutbol.equipofutbol_adso.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeesRepository extends JpaRepository<Employees, Long> {


    @Query("SELECT j FROM Employees j WHERE j.numeroCamiseta = :numeroCamiseta")
    Optional<Employees> findByCamiseta(@Param("numeroCamiseta") int numeroCamiseta);

    @Query("SELECT DISTINCT j FROM Employees j LEFT JOIN FETCH j.listResultados WHERE j.numeroCamiseta = :numeroCamiseta")
    Optional<Employees> obtenerPuntajePorCamiseta(@Param("numeroCamiseta") int numeroCamiseta);

    @Query("SELECT j FROM Employees j LEFT JOIN FETCH j.listResultados")
    List<Employees> obtenerPuntajesDeTodosLosJugadores();

    @Query("SELECT j FROM Employees j WHERE j.username = :username")
    Optional<Employees> findByUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM Employees j WHERE j.numeroCamiseta = :numeroCamiseta")
    boolean existsByNumeroCamiseta(int numeroCamiseta);

    @Query("SELECT DISTINCT j FROM Employees j LEFT JOIN FETCH j.listResultados WHERE j.numeroCamiseta = :idJugador")
    List<Employees> obtenerPuntaje(@Param("idJugador") int idJugador);


}
