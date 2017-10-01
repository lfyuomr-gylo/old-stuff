package com.github.lfyuomr.gylo.kango.client.model;

import com.github.lfyuomr.gylo.kango.client.util.Config;
import com.github.lfyuomr.gylo.kango.client.util.JaxbParser;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;

@XmlRootElement
public class AuthorizedAccount {
    private final LongProperty id;
    private final StringProperty login;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final ObservableList<Conversation> conversations;
    private final ObservableList<Contact> contacts;

    public AuthorizedAccount() {
        this.id = new SimpleLongProperty();
        this.login = new SimpleStringProperty();
        this.firstName = new SimpleStringProperty();
        this.lastName = new SimpleStringProperty();
        this.conversations = FXCollections.observableArrayList();
        this.contacts = FXCollections.observableArrayList();
    }

    public AuthorizedAccount(long id,
                      String login,
                      String firstName,
                      String lastName,
                      ObservableList<Conversation> conversations,
                      ObservableList<Contact> contacts) {
        this.id = new SimpleLongProperty(id);
        this.login = new SimpleStringProperty(login);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.conversations = conversations;
        this.contacts = contacts;
    }

    public void
    saveHistory() {
        try {
            File file = Config.getHistoryFile(id.get());
            JaxbParser.saveObject(file, this);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("I'm saving account history");
        saveHistory();
    }

    @XmlElement
    public String
    getLogin() {
        return login.get();
    }

    public StringProperty
    loginProperty() {
        return login;
    }

    public void
    setLogin(String login) {
        this.login.set(login);
    }

    @XmlElement
    public long
    getId() {
        return id.get();
    }

    public LongProperty
    idProperty() {
        return id;
    }

    public void
    setId(long id) {
        this.id.set(id);
    }

    @XmlElement
    public String
    getFirstName() {
        return firstName.get();
    }

    public StringProperty
    firstNameProperty() {
        return firstName;
    }

    public void
    setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    @XmlElement
    public String
    getLastName() {
        return lastName.get();
    }

    public StringProperty
    lastNameProperty() {
        return lastName;
    }

    public void
    setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    @XmlElement
    public ObservableList<Conversation>
    getConversations() {
        return conversations;
    }

    @XmlElement
    public ObservableList<Contact>
    getContacts() {
        return contacts;
    }
}