package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "INPUT_CHAT_MESSAGE")
public class InputChatMessage extends ServerToClientProtoMessage {
    @XmlElement
    public long conversationId;
    @XmlElement
    public String author;
    @XmlElement
    public byte[] text;

    public InputChatMessage() {
    }

    public InputChatMessage(long conversationId, String author, byte[] text) {
        this.conversationId = conversationId;
        this.author = author;
        this.text = text;
    }
}
