package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "REGISTRATION_SUCCEED")
public class RegistrationSucceeded extends ServerToClientProtoMessage {
    @XmlElement
    public long id;

    @XmlElement
    public String login;

    @XmlElement
    public String firstName;

    @XmlElement
    public String lastName;

    public RegistrationSucceeded() {
    }

    public RegistrationSucceeded(long id, String login, String firstName, String lastName) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
