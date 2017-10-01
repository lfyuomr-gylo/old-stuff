package com.github.lfyuomr.gylo.kango.client.proto.messages.server2client;

import com.github.lfyuomr.gylo.kango.client.proto.messages.client2server.ClientToServerProtoMessage;

import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
public interface ServerQuestioner {
    Predicate<ClientToServerProtoMessage>
    getResponseRecognizer();
}
