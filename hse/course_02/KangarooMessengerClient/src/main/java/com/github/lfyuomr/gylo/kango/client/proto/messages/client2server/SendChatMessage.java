package com.github.lfyuomr.gylo.kango.client.proto.messages.client2server;

import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.ChatMessageSendFailed;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.ChatMessageSent;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.ServerToClientProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
@XmlRootElement(name = "SEND_CHAT_MESSAGE")
public class SendChatMessage extends ClientToServerProtoMessage implements ClientQuestioner {
    @XmlElement
    public long conversationId;

    @XmlElement
    public long messageId;

    @XmlElement
    public byte[] text;

    public SendChatMessage() {
    }

    public SendChatMessage(long conversationId, long messageId, byte[] text) {
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.text = text;
    }

    @Override
    public Predicate<ServerToClientProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof ChatMessageSent) {
                return messageId == ((ChatMessageSent) mes).messageId &&
                        conversationId == ((ChatMessageSent) mes).conversationId;
            }
            else if (mes instanceof ChatMessageSendFailed) {
                return messageId == ((ChatMessageSendFailed) mes).messageId &&
                        conversationId == ((ChatMessageSendFailed) mes).conversationId;
            }

            return false;
        };
    }
}
