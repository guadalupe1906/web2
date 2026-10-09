package com.example.demo.repository;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favoritos")
public class FavoritoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    private String nota;

    @Column(name = "fecha_agregado", nullable = false)
    private LocalDate fechaAgregado;
    
    @ManyToOne
@JoinColumn(name = "lista_id")
private ListaEntity lista;
}