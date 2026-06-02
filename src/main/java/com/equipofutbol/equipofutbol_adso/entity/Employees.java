package com.equipofutbol.equipofutbol_adso.entity;

import com.equipofutbol.equipofutbol_adso.enums.UserRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entidad JPA que representa a un usuario o jugador del sistema.
 * Se anota con @Entity para que Hibernate la reconozca como una entidad
 * persistente y @Table(name = "employees") para mapearla a la tabla
 * correspondiente en MySQL. Cada instancia de esta clase es una fila en la
 * base de datos. La clase no usa Lombok para los getters y setters porque
 * requiere implementaciones personalizadas de equals() y hashCode() basadas
 * en el ID para evitar problemas con las colecciones de Hibernate. La relación
 * @OneToMany con Resultados permite navegar desde un jugador a todos sus
 * entrenamientos, y cascade = CascadeType.REMOVE asegura que al eliminar un
 * jugador se eliminen también sus resultados asociados.
 */
@Entity
@Table(name = "employees")
public class Employees {

    /** Identificador único autoincremental generado por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de usuario utilizado para la autenticación en el sistema. */
    private String username;

    /** Contraseña cifrada con BCrypt. Nunca se almacena en texto plano. */
    private String password;

    /** Rol del usuario almacenado como String en la base de datos (ADMINISTRATOR o JUGADOR). */
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /** Posición del jugador en el campo de juego (ej: Delantero, Defensa, Portero). */
    private String posicion;

    /** Número de camiseta del jugador, único dentro del equipo. */
    private int numeroCamiseta;

    /** Puntaje total promedio calculado a partir de los resultados de entrenamiento. */
    private double puntajeTotal;

    /** Indica si el usuario está activo en el sistema (true = activo, false = desactivado). */
    private boolean active;

    /** Fecha y hora de creación del registro en la base de datos. */
    private LocalDateTime createdAt;

    /**
     * Lista de resultados de entrenamiento asociados a este jugador.
     * La relación es OneToMany y se mapea por el campo "users" en la entidad Resultados.
     * CascadeType.REMOVE propaga la eliminación: al borrar un jugador, se borran sus entrenamientos.
     */
    @OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
    private List<Resultados> listResultados;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }

    public int getNumeroCamiseta() { return numeroCamiseta; }
    public void setNumeroCamiseta(int numeroCamiseta) { this.numeroCamiseta = numeroCamiseta; }

    public double getPuntajeTotal() { return puntajeTotal; }
    public void setPuntajeTotal(double puntajeTotal) { this.puntajeTotal = puntajeTotal; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Resultados> getListResultados() { return listResultados; }
    public void setListResultados(List<Resultados> listResultados) { this.listResultados = listResultados; }

    /**
     * Compara dos entidades Employees por su ID para determinar igualdad.
     * Hibernate requiere que equals() compare solo el ID una vez que la entidad
     * ha sido persistida, evitando comparaciones por referencia de objeto.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employees employees = (Employees) o;
        return id != null && Objects.equals(id, employees.id);
    }

    /**
     * Genera el hashcode basado en la clase para mantener consistencia con equals().
     * Hibernate recomienda usar un hashcode constante para evitar problemas con
     * colecciones cuando las entidades aún no tienen ID asignado.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Employees{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", posicion='" + posicion + '\'' +
                ", numeroCamiseta=" + numeroCamiseta +
                ", puntajeTotal=" + puntajeTotal +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
