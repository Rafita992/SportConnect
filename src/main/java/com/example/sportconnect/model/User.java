package com.example.sportconnect.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "users") // Debe coincidir con el nombre en MariaDB
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    // --- CONSTRUCTORES ---

    public User() {
        // Constructor vacío requerido por Hibernate
    }

    public User(String name, String lastName, String email, String password, boolean isAdmin) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    // --- MÉTODOS ÚTILES ---

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}