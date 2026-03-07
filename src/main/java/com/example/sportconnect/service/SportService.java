package com.example.sportconnect.service;

import com.example.sportconnect.dao.SportDAO;
import com.example.sportconnect.model.Sport;

import java.util.List;

public class SportService {

    private final SportDAO sportDAO = new SportDAO();

    public List<Sport> getAllSports() {
        return sportDAO.getAllSports();
    }

    public void save(Sport sport) {
        sportDAO.save(sport);
    }

    public void delete(Sport sport) {
        sportDAO.delete(sport);
    }
}