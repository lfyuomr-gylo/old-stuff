package com.github.lfyuomr.gylo.kango.server.db.mappings;

import com.github.lfyuomr.gylo.kango.server.db.HibernateUtil;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity()
@Table(name = "conversation")
public class DBConversation extends DBMapping {
    private static final HashMap<Long, DBConversation> loadedConversations = new HashMap<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToMany(mappedBy = "conversations", cascade = CascadeType.ALL)
    private Set<DBUser> participants = new HashSet<>(0);

    public DBConversation() {
    }

    public DBConversation(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<DBUser> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<DBUser> participants) {
        this.participants = participants;
    }

    public void addParticipant(DBUser user) {
        participants.add(user);
        user.getConversations().add(this);
    }


    public static DBConversation getById(Long id) {
        synchronized (loadedConversations) {
            if (loadedConversations.containsKey(id)) {
                return loadedConversations.get(id);
            }
            Session session = HibernateUtil.getSessionFactory().openSession();
            final DBConversation result = session.load(DBConversation.class, id);
            session.close();

            loadedConversations.put(id, result);
            return result;
        }
    }

    public static List<DBConversation> getAllFromDB() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        final List<DBConversation> result = session.createCriteria(DBConversation.class).list();
        session.close();
        return result;
    }

    @Override
    public void deleteFromDB() {
        for (DBUser participant : participants) {
            participant.getConversations().remove(this);
            participant.saveOrUpdateInDB();
        }
        participants.clear();
        saveOrUpdateInDB();
        synchronized (loadedConversations) {
            loadedConversations.remove(id);
        }
        super.deleteFromDB();
    }
}
