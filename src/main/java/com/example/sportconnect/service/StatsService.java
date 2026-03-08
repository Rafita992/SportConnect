package com.example.sportconnect.service;

import com.example.sportconnect.dao.ReservationDAO;
import com.example.sportconnect.dao.UserDAO;
import com.example.sportconnect.model.Reservation;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final UserDAO        userDAO        = new UserDAO();

    public long getReservasHoy() {
        return reservationDAO.getAllReservations().stream()
                .filter(r -> !r.getCancelled())
                .filter(r -> r.getBookingDate().equals(LocalDate.now()))
                .count();
    }

    public long getTotalUsuarios() {
        return userDAO.getAllUsers().size();
    }

    public String getPistaMasReservada() {
        List<Reservation> reservas = reservationDAO.getAllReservations().stream()
                .filter(r -> !r.getCancelled())
                .collect(Collectors.toList());

        if (reservas.isEmpty()) return "Sin datos";

        return reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getCourt().getNombre(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");
    }

    // Devuelve un mapa con fecha -> nº reservas para los ultimos 7 dias
    public Map<String, Long> getReservasUltimos7Dias() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            String etiqueta = fecha.getDayOfMonth() + "/" + fecha.getMonthValue();
            long count = reservationDAO.getAllReservations().stream()
                    .filter(r -> !r.getCancelled())
                    .filter(r -> r.getBookingDate().equals(fecha))
                    .count();
            resultado.put(etiqueta, count);
        }
        return resultado;
    }
}