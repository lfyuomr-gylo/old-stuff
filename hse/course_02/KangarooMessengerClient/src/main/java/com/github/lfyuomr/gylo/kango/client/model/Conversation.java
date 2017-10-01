package com.github.lfyuomr.gylo.kango.client.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Conversation {
    private final LongProperty id;
    private final StringProperty title;
    private final ObservableList<Message> messages;
    private final ObservableList<Contact> members;

    public Conversation() {
        this(0, null, null, null);
    }

    public Conversation(long id, String title, ObservableList<Message> messages, ObservableList<Contact> members) {
        title = title != null ? title : "";
        messages = messages != null ? messages : FXCollections.observableArrayList();
        members = members != null ? members : FXCollections.observableArrayList();

        this.id = new SimpleLongProperty(id);
        this.title = new SimpleStringProperty(title);
        this.messages = messages;
        this.members = members;
    }

    @XmlElement
    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    @XmlElement
    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    @XmlElement
    public ObservableList<Message> getMessages() {
        return messages;
    }

    @XmlElement
    public ObservableList<Contact> getMembers() {
        return members;
    }
}
