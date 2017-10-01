package com.github.lfyuomr.gylo.kango.server.proto.messages.client2server;


import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.CCFailed;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.CCPowerKey;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.ServerToClientProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.function.Predicate;

@XmlRootElement(name = "CREATE_CONVERSATION")
public class CreateConversation extends ClientToServerProtoMessage implements ClientQuestioner {
    @XmlElement
    public int titleHash;

    @XmlElement
    public String title;

    @XmlElement
    public List<Long> ids;

    public CreateConversation() {
    }

    public CreateConversation(String title, List<Long> ids) {
        this.titleHash = title.hashCode();
        this.title = title;
        this.ids = ids;
    }

    @Override
    public Predicate<ServerToClientProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof CCPowerKey) {
                return titleHash == ((CCPowerKey) mes).titleHash;
            }
            else if (mes instanceof CCFailed) {
                return titleHash == ((CCFailed) mes).titleHash;
            }
            return false;
        };
    }
}
