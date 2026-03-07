package com.example.sportconnect.service;

import com.example.sportconnect.dao.ReservationDAO;
import com.example.sportconnect.model.Reservation;
import com.example.sportconnect.model.User;

import java.util.List;

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();

    /**
     * Devuelve todas las reservas. Para uso del admin.
     */
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    /**
     * Devuelve las reservas de un usuario concreto.
     */
    public List<Reservation> getReservationsByUser(User user) {
        return reservationDAO.getReservationsByUser(user);
    }

    /**
     * Guarda o actualiza una reserva.
     */
    public void save(Reservation reservation) {
        reservationDAO.save(reservation);
    }

    /**
     * Cancela una reserva (borrado lógico).
     */
    public void cancel(Reservation reservation) {
        reservationDAO.cancel(reservation);
    }

    /**
     * Elimina una reserva permanentemente.
     */
    public void delete(Reservation reservation) {
        reservationDAO.delete(reservation);
    }

    /**
     * Busca una reserva por su ID.
     */
    public Reservation getById(Long id) {
        return reservationDAO.getReservationById(id);
    }
}