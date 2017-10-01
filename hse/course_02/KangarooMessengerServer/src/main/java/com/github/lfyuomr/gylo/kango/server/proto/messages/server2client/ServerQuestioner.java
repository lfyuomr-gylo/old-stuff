package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;


import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.ClientToServerProtoMessage;

import java.util.function.Predicate;

public interface ServerQuestioner {
    public Predicate<ClientToServerProtoMessage>
    getResponseRecognizer();
}
