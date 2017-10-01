package com.github.lfyuomr.gylo.kango.client.proto.messages.client2server;

import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.AuthorizationFailed;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.AuthorizationSucceeded;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.ServerToClientProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
@XmlRootElement(name = "AUTHORIZE")
public class Authorize extends ClientToServerProtoMessage implements ClientQuestioner {
    @XmlElement
    public String login;

    @XmlElement
    public int passwordHash;

    public Authorize() {
    }

    public Authorize(String login, int passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    @Override
    public Predicate<ServerToClientProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof AuthorizationSucceeded) {
                return login.equals(((AuthorizationSucceeded) mes).login);
            }
            else if (mes instanceof AuthorizationFailed) {
                return login.equals(((AuthorizationFailed) mes).login);
            }
            return false;
        };
    }
}
