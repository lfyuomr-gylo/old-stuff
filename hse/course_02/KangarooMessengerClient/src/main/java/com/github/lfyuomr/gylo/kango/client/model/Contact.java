package com.github.lfyuomr.gylo.kango.client.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Contact {
    private final LongProperty id;
    private final StringProperty login;
    private final StringProperty firstName;
    private final StringProperty lastName;

    public Contact() {
        this(0, null, null, null);
    }

    public Contact(long id, String login, String firstName, String lastName) {
        login = (login != null) ? login : "";
        firstName = (firstName != null) ? firstName : "";
        lastName = (lastName != null) ? lastName : "";

        this.id = new SimpleLongProperty(id);
        this.login = new SimpleStringProperty(login);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
    }

    public String toString() {
        return firstName.get() + " aka " + login.get() + " " + lastName.get();
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public String getLogin() {
        return login.get();
    }

    public StringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }
}
