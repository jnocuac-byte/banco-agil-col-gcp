package com.bancoagil.auth_service.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // Importamos ToString
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad que mapea la tabla 'persona_natural'.
 * Contiene información específica de clientes que son personas físicas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persona_natural")
@ToString(exclude = "cliente") // Excluimos la relación para evitar bucles de Lombok
public class PersonaNatural {

    // Clave Primaria Autoincremental (PK de PersonaNatural)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación One-to-One: Una persona natural tiene un cliente asociado.
    // Esta entidad es la dueña de la FK (la columna 'id_cliente')
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", unique = true, nullable = false)
    private Cliente cliente; // Clave Foránea a la tabla 'clientes'

    // Columna: nombre (varchar(100) NN)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // Columna: apellido (varchar(100) NN)
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    // Columna: identificacion (varchar(50) NN)
    @Column(name = "identificacion", nullable = false, length = 50, unique = true)
    private String identificacion;

    // Columna: fecha_nacimiento (date)
    @Column(name = "fecha_nacimiento")
    private java.sql.Date fechaNacimiento;
}