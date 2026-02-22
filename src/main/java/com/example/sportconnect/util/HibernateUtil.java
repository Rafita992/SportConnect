package com.example.sportconnect.util; // Ajusta a tu paquete

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration; // <--- ASEGÚRATE DE QUE SEA ESTE

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Se usa la configuración de Hibernate explícitamente
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Fallo al crear la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}