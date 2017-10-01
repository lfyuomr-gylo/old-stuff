package com.github.lfyuomr.gylo.kango.client.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message {
    private final StringProperty author;
    private final StringProperty text;

    public Message() {
        this(null, null);
    }

    public Message(String author, String text) {
        author = author != null ? author : "";
        text = text != null ? text : "";

        this.author = new SimpleStringProperty(author);
        this.text = new SimpleStringProperty(text);
    }

    public String toString() {
        return author.get() + ":\n" + text.getValue();
    }

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }
}
