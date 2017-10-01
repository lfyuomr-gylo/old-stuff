package com.github.lfyuomr.gylo.lamport.mutex;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.net.SocketAddress;
import java.util.Map;

@Value @Builder
public class Configuration {
    int myId;
    int myPort;

    /**
     * Mapping for otherNodes: nodeId -> nodeAddress
     */
    @Singular Map<Integer, SocketAddress> otherNodes;

    public int initialMutexOwnerId() {
        int minOtherId = otherNodes.keySet().stream().reduce(Integer::min).orElse(myId);
        return Integer.min(minOtherId, myId);
    }
}
