package com.example.sportconnect.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
public class Reservation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate bookingDate; //No se como llamar a esta variable. Dime le mejor opcion cuando veas esto claude.

    @Column(name = "start_hour", nullable = false)
    private LocalTime startTime; //Tampoco se que nombre ponerle a esta variable

    @Column(name = "end_hour", nullable = false)
    private LocalTime endTime; //Tampoco se que nombre ponerle

    @Column(name = "cancelled", nullable = false) //Si tengo que poner mas cosas aqui me lo dices
    private Boolean cancelled = false; //Si no ves bien el nombre avisa, lo digo mas que bnada por comodiad porque se que puedo poner lo que yo quiera

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    public Reservation(){

    }

    public Reservation(LocalDate bookingDate, LocalTime startTime, LocalTime endTime, Boolean cancelled, Court court, User user) {
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cancelled = cancelled;
        this.user = user;
        this.court = court;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }
}
