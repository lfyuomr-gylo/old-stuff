package com.github.lfyuomr.gylo.kango.server.proto.messages.client2server;


import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.CCFailed;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.CCSucceeded;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.ServerToClientProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.function.Predicate;

@XmlRootElement(name = "CC_CONFIRMATION")
public class CCConfirmation extends ClientToServerProtoMessage implements ClientQuestioner {
    @XmlElement
    public long conversationId;

    @XmlElement
    public int titleHash;

    public CCConfirmation() {
    }

    public CCConfirmation(long conversationId, int titleHash) {
        this.conversationId = conversationId;
        this.titleHash = titleHash;
    }

    @Override
    public Predicate<ServerToClientProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof CCSucceeded) {
                return conversationId == ((CCSucceeded) mes).conversationId &&
                        titleHash == ((CCSucceeded) mes).titleHash;
            }
            else if (mes instanceof CCFailed) {
                return titleHash == ((CCFailed) mes).titleHash;
            }
            return false;
        };
    }
}
