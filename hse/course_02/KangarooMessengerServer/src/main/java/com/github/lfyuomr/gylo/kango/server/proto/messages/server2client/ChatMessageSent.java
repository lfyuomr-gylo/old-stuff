package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CHAT_MESSAGE_SENT")
public class ChatMessageSent extends ServerToClientProtoMessage {
    @XmlElement
    public long conversationId;

    @XmlElement
    public long messageId;

    public ChatMessageSent() {
    }

    public ChatMessageSent(long conversationId, long messageId) {
        this.conversationId = conversationId;
        this.messageId = messageId;
    }
}
