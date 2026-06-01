package com.equipofutbol.equipofutbol_adso.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "resultados")
public class Resultados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numeroEntrenamiento;
    private int pasesEfectivos;
    private long potenciaTiro;
    private long velocidadJugador;
    

    @JoinColumn(name = "users_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Employees users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumeroEntrenamiento() {
        return numeroEntrenamiento;
    }

    public void setNumeroEntrenamiento(int numeroEntrenamiento) {
        this.numeroEntrenamiento = numeroEntrenamiento;
    }

    public int getPasesEfectivos() {
        return pasesEfectivos;
    }

    public void setPasesEfectivos(int pasesEfectivos) {
        this.pasesEfectivos = pasesEfectivos;
    }

    public long getPotenciaTiro() {
        return potenciaTiro;
    }

    public void setPotenciaTiro(long potenciaTiro) {
        this.potenciaTiro = potenciaTiro;
    }

    public long getVelocidadJugador() {
        return velocidadJugador;
    }

    public void setVelocidadJugador(long velocidadJugador) {
        this.velocidadJugador = velocidadJugador;
    }

    public Employees getUsers() {
        return users;
    }

    public void setUsers(Employees users) {
        this.users = users;
    }

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
