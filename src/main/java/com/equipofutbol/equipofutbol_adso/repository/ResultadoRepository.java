package com.equipofutbol.equipofutbol_adso.repository;

import com.equipofutbol.equipofutbol_adso.entity.Resultados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResultadoRepository extends JpaRepository<Resultados, Long> {

    @Query("SELECT COUNT(r.numeroEntrenamiento) FROM Resultados r ")
    long countNumeroEntrenamiento();
}
