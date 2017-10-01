package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;


import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.CCConfirmation;
import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.ClientToServerProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Predicate;

@XmlRootElement(name = "CC_CONFIRM")
public class CCConfirm extends ServerToClientProtoMessage implements ServerQuestioner {
    /**
     * Number of {@link CCPowerKey} messages sent
     */
    @XmlElement
    public int iterationsNum;

    @XmlElement
    public int titleHash;

    @XmlElement
    public String title;

    @XmlElement
    public BigInteger value;

    @XmlElement
    public long conversationId;

    //----- participants
    @XmlElement
    public List<Long> ids;

    @XmlElement
    public List<String> logins;

    @XmlElement
    public List<String> firstNames;

    @XmlElement
    public List<String> lastNames;

    public CCConfirm() {
    }

    public CCConfirm(int iterationsNum,
                     int titleHash,
                     String title,
                     BigInteger value,
                     long conversationId,
                     List<Long> ids,
                     List<String> logins,
                     List<String> firstNames,
                     List<String> lastNames) {
        this.iterationsNum = iterationsNum;
        this.titleHash = titleHash;
        this.title = title;
        this.value = value;
        this.conversationId = conversationId;
        this.ids = ids;
        this.logins = logins;
        this.firstNames = firstNames;
        this.lastNames = lastNames;
    }

    @Override
    public Predicate<ClientToServerProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof CCConfirmation) {
                return conversationId == ((CCConfirmation) mes).conversationId &&
                        titleHash == ((CCConfirmation) mes).titleHash;
            }
            return false;
        };
    }
}
