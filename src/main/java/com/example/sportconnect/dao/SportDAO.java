package com.example.sportconnect.dao;

import com.example.sportconnect.model.Sport;
import com.example.sportconnect.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;



public class SportDAO {
    public getAllSports(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            Query<Sport> query = session.createQuery("FROM Sport WHERE nombre = nombre", Sport.class);
        }
    }
}
