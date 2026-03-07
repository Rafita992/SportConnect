package com.example.sportconnect.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "sports")
public class Sport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String nombre;

    @OneToMany(mappedBy = "sport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Court> courts;

    public Sport(){

    }

    public Sport(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Court> getCourts() {
        return courts;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
