package com.github.lfyuomr.gylo.kango.server.db.mappings;

import com.github.lfyuomr.gylo.kango.server.db.HibernateUtil;
import org.hibernate.Session;

public abstract class DBMapping {
    public void saveOrUpdateInDB() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(this);
        session.getTransaction().commit();
        session.close();
    }

    public void deleteFromDB() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(this);
        session.getTransaction().commit();
        session.close();
    }
}
