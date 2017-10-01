package com.github.lfyuomr.gylo.kango.client.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("WeakerAccess")
@XmlRootElement(name = "CC_FAILED")
public class CCFailed extends ServerToClientProtoMessage {
    @XmlElement
    public int titleHash;

    @XmlElement
    public String explanation;

    public CCFailed() {
    }

    public CCFailed(int titleHash, String explanation) {
        this.titleHash = titleHash;
        this.explanation = explanation;
    }
}
