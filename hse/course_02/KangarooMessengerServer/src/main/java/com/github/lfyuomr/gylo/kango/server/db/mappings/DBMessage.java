package com.github.lfyuomr.gylo.kango.server.db.mappings;

import javax.persistence.*;

@Entity
@Table(name = "message")
public class DBMessage extends DBMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private DBUser author;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private DBUser receiver;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private DBConversation conversation;

    @Column(name = "messageEncodedText")
    private byte[] encodedText;

    public DBMessage() {
    }

    public DBMessage(DBUser author, DBUser receiver, DBConversation conversation, byte[] encodedText) {
        this.author = author;
        this.receiver = receiver;
        receiver.getIncomeMessages().add(this);
        this.conversation = conversation;
        this.encodedText = encodedText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBUser getAuthor() {
        return author;
    }

    public void setAuthor(DBUser authorId) {
        this.author = authorId;
    }

    public DBUser getReceiver() {
        return receiver;
    }

    public void setReceiver(DBUser receiver) {
        this.receiver.getIncomeMessages().remove(this);
        this.receiver = receiver;
        this.receiver.getIncomeMessages().add(this);
    }

    public DBConversation getConversation() {
        return conversation;
    }

    public void setConversation(DBConversation conversation) {
        this.conversation = conversation;
    }

    public byte[] getEncodedText() {
        return encodedText;
    }

    public void setEncodedText(byte[] text) {
        this.encodedText = text;
    }
}
