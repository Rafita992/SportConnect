package com.example.sportconnect.service;

import com.example.sportconnect.dao.CourtDAO;
import com.example.sportconnect.model.Court;

import java.util.List;

public class CourtService {

    private final CourtDAO courtDAO = new CourtDAO();

    public List<Court> getAllCourts() {
        return courtDAO.getAllCourts();
    }

    public void save(Court court) {
        courtDAO.save(court);
    }

    public void delete(Court court) {
        courtDAO.delete(court);
    }
}