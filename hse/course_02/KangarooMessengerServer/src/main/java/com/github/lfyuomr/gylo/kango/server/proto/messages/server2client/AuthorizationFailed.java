package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AUTHORIZATION_FAILED")
public class AuthorizationFailed extends ServerToClientProtoMessage {
    @XmlElement
    public String login;

    @XmlElement
    public String explanation;

    public AuthorizationFailed() {
    }

    public AuthorizationFailed(String login, String explanation) {
        this.login = login;
        this.explanation = explanation;
    }
}
