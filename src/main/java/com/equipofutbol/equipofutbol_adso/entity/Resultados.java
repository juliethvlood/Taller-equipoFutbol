package com.equipofutbol.equipofutbol_adso.entity;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Entidad JPA que representa el resultado de un entrenamiento realizado por un jugador.
 * Se anota con @Entity y @Table(name = "resultados") para mapearla a la tabla
 * correspondiente en MySQL. Contiene las métricas de rendimiento de un jugador
 * en una sesión de entrenamiento específica (pases, velocidad, potencia). La
 * relación @ManyToOne con Employees (mapeada por la FK "users_id") vincula cada
 * resultado al jugador que lo realizó, usando carga perezosa (FetchType.LAZY)
 * para evitar consultas innecesarias a la base de datos cuando solo se accede
 * a los datos del resultado sin necesidad del jugador.
 */
@Entity
@Table(name = "resultados")
public class Resultados {

    /** Identificador único autoincremental del resultado. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número del entrenamiento (1, 2 o 3) dentro de la semana. */
    private int numeroEntrenamiento;

    /** Cantidad de pases efectivos realizados durante el entrenamiento. */
    private int pasesEfectivos;

    /** Potencia de tiro alcanzada durante el entrenamiento. */
    private long potenciaTiro;

    /** Velocidad del jugador registrada durante el entrenamiento. */
    private long velocidadJugador;

    /**
     * Relación muchos-a-uno con la entidad Employees.
     * Cada resultado pertenece a un solo jugador. La columna "users_id" es la
     * clave foránea en la tabla resultados que referencia la tabla employees.
     * FetchType.LAZY indica que el jugador se cargará desde la base de datos
     * solo cuando se acceda explícitamente al campo "users", optimizando el
     * rendimiento de las consultas.
     */
    @JoinColumn(name = "users_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Employees users;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNumeroEntrenamiento() { return numeroEntrenamiento; }
    public void setNumeroEntrenamiento(int numeroEntrenamiento) { this.numeroEntrenamiento = numeroEntrenamiento; }

    public int getPasesEfectivos() { return pasesEfectivos; }
    public void setPasesEfectivos(int pasesEfectivos) { this.pasesEfectivos = pasesEfectivos; }

    public long getPotenciaTiro() { return potenciaTiro; }
    public void setPotenciaTiro(long potenciaTiro) { this.potenciaTiro = potenciaTiro; }

    public long getVelocidadJugador() { return velocidadJugador; }
    public void setVelocidadJugador(long velocidadJugador) { this.velocidadJugador = velocidadJugador; }

    public Employees getUsers() { return users; }
    public void setUsers(Employees users) { this.users = users; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resultados that = (Resultados) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Resultados{" +
                "id=" + id +
                ", numeroEntrenamiento=" + numeroEntrenamiento +
                ", pasesEfectivos=" + pasesEfectivos +
                ", potenciaTiro=" + potenciaTiro +
                ", velocidadJugador=" + velocidadJugador +
                '}';
    }
}
