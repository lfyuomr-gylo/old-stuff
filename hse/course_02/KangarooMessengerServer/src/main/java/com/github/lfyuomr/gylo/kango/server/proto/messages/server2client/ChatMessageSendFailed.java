package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CHAT_MESSAGE_SEND_FAILED")
public class ChatMessageSendFailed extends ServerToClientProtoMessage {
    @XmlElement
    public long conversationId;

    @XmlElement
    public long messageId;

    public ChatMessageSendFailed() {
    }

    public ChatMessageSendFailed(long conversationId, long messageId) {
        this.conversationId = conversationId;
        this.messageId = messageId;
    }
}
