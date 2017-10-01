package com.github.lfyuomr.gylo.kango.client.proto.messages.client2server;

import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.ServerToClientProtoMessage;

import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
public interface ClientQuestioner {
    Predicate<ServerToClientProtoMessage>
    getResponseRecognizer();
}
