package com.github.lfyuomr.gylo.kango.client.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("WeakerAccess")
@XmlRootElement(name = "REGISTRATION_FAILED")
public class RegistrationFailed extends ServerToClientProtoMessage {
    @XmlElement
    public String login;

    @XmlElement
    public String firstName;

    @XmlElement
    public String lastName;

    @XmlElement
    public String explanation;

    public RegistrationFailed() {
    }

    public RegistrationFailed(String login, String firstName, String lastName, String explanation) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.explanation = explanation;
    }
}
