package com.github.lfyuomr.gylo.kango.client.proto.messages.client2server;

import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.RegistrationFailed;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.RegistrationSucceeded;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.ServerToClientProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
@XmlRootElement(name = "REGISTER")
public class Register extends ClientToServerProtoMessage implements ClientQuestioner {
    @XmlElement
    public String login;

    @XmlElement
    public int paasswordHash;

    @XmlElement
    public String firstName;

    @XmlElement
    public String lastName;

    public Register() {
    }

    public Register(String login, int paasswordHash, String firstName, String lastName) {
        this.login = login;
        this.paasswordHash = paasswordHash;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public Predicate<ServerToClientProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof RegistrationSucceeded) {
                return login.equals(((RegistrationSucceeded) mes).login) &&
                        firstName.equals(((RegistrationSucceeded) mes).firstName) &&
                        lastName.equals(((RegistrationSucceeded) mes).lastName);
            } else if (mes instanceof RegistrationFailed) {
                return login.equals(((RegistrationFailed) mes).login) &&
                        firstName.equals(((RegistrationFailed) mes).firstName) &&
                        lastName.equals(((RegistrationFailed) mes).lastName);
            }
            return false;
        };
    }
}
