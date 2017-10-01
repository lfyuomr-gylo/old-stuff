package com.github.lfyuomr.gylo.kango.server.db.mappings;

import com.github.lfyuomr.gylo.kango.server.UserServant;
import com.github.lfyuomr.gylo.kango.server.db.HibernateUtil;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "users")
public class DBUser extends DBMapping {
    private static final ConcurrentHashMap<Long, OnlineInfo> onlineUsers = new ConcurrentHashMap<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "passwordHash")
    private Integer passwordHash;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receiver", orphanRemoval = true)
    @OrderBy("id")
    private List<DBMessage> incomeMessages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_mtm_conversation",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "conversation_id")})
    private Set<DBConversation> conversations = new HashSet<>(0);

    public DBUser() {
    }

    public DBUser(String login, Integer passwordHash, String firstName, String lastName) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * @param login
     * @return user with specified login if found, null otherwise.
     */
    public static DBUser getByLogin(String login) {
        for (OnlineInfo info : onlineUsers.values()) {
            if (info.user.getLogin().equals(login)) {
                return info.user;
            }
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        List result_list = session.createQuery("from DBUser u where u.login = '" + login + "'").list();
        session.close();
        if (result_list.isEmpty()) {
            return null;
        }
        return (DBUser) result_list.get(0);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public static DBUser getById(Long id) {
        if (isOnline(id)) {
            return onlineUsers.get(id).user;
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        final DBUser result = session.load(DBUser.class, id);
        session.close();
        return result;
    }

    public static boolean isOnline(Long id) {
        if (onlineUsers.containsKey(id)) {
            if (onlineUsers.get(id).servant.isAuthorized()) {
                return true;
            }
            else {
                onlineUsers.remove(id);
                return false;
            }
        }
        return false;
    }

    public static List<DBUser> getAllFromDB() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        final List<DBUser> result = session.createCriteria(DBUser.class).list();
        session.close();
        return result;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        goOffline();
    }

    public void goOffline() {
        onlineUsers.remove(id);
    }

    public Integer getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(Integer passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<DBMessage> getIncomeMessages() {
        return incomeMessages;
    }

    public void setIncomeMessages(List<DBMessage> incomeMessages) {
        this.incomeMessages = incomeMessages;
    }

    public Set<DBConversation> getConversations() {
        return conversations;
    }

    public void setConversations(Set<DBConversation> conversations) {
        this.conversations = conversations;
    }

    public void addConversation(DBConversation conversation) {
        conversations.add(conversation);
        conversation.getParticipants().add(this);
    }

    /**
     * Add the user to online users list
     * @return false if this user is already online, true otherwise
     */
    public void goOnline(UserServant servant) {
        goOnline(servant, this);
    }


    /**
     * Add the user to online users list
     * @return false if this user is already online, true otherwise
     */
    public static void goOnline(UserServant servant, DBUser user) {
        synchronized (onlineUsers) {
            final OnlineInfo info = new OnlineInfo(user, servant);
            onlineUsers.put(user.getId(), info);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isOnline() {
        return isOnline(id);
    }

    public UserServant getServant() {
        OnlineInfo info = onlineUsers.get(id);
        if (info == null)
            return null;
        return info.servant;
    }

    private static class OnlineInfo {
        public DBUser user;
        public UserServant servant;

        public OnlineInfo(DBUser user, UserServant servant) {
            this.user = user;
            this.servant = servant;
        }
    }
}
