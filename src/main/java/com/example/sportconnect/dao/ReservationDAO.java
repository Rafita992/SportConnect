package com.example.sportconnect.dao;

import com.example.sportconnect.model.Reservation;
import com.example.sportconnect.model.User;
import com.example.sportconnect.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class ReservationDAO {
    public void save(Reservation reservation) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(reservation);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Reservation reservation) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(reservation);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void cancel(Reservation reservation){
        reservation.setCancelled(true);
        save(reservation);
    }

    public List<Reservation> getAllReservations(){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Query<Reservation> query = session.createQuery("FROM Reservation",Reservation.class);
            return query.list();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}



