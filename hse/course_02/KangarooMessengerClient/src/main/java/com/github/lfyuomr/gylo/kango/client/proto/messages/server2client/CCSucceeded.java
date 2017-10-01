package com.github.lfyuomr.gylo.kango.client.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CC_SUCCEED")
public class CCSucceeded extends ServerToClientProtoMessage {
    @XmlElement
    public long conversationId;

    @XmlElement
    public int titleHash;

    public CCSucceeded() {
    }

    public CCSucceeded(long conversationId, int titleHash) {
        this.conversationId = conversationId;
        this.titleHash = titleHash;
    }
}
