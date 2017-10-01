package com.github.lfyuomr.gylo.kango.client.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AUTHORIZATION_SUCCEED")
public class AuthorizationSucceeded extends ServerToClientProtoMessage {
    @XmlElement
    public long id;

    @XmlElement
    public String login;

    @XmlElement
    public String firstName;

    @XmlElement
    public String lastName;

    public AuthorizationSucceeded() {
    }

    public AuthorizationSucceeded(long id, String login, String firstName, String lastName) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
