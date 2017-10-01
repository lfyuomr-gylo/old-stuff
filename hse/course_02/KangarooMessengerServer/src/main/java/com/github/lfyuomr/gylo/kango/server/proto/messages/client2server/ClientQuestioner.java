package com.github.lfyuomr.gylo.kango.server.proto.messages.client2server;


import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.ServerToClientProtoMessage;

import java.util.function.Predicate;

public interface ClientQuestioner {
    Predicate<ServerToClientProtoMessage>
    getResponseRecognizer();
}
