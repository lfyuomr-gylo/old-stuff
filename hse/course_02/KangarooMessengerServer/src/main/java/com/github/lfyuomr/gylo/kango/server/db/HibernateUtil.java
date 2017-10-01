package com.github.lfyuomr.gylo.kango.server.db;

import com.github.lfyuomr.gylo.kango.server.db.mappings.DBMapping;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void saveOrUpdateMappings(DBMapping... data) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        for (DBMapping cur : data) {
            session.saveOrUpdate(cur);
        }
        session.getTransaction().commit();
        session.close();
    }
}