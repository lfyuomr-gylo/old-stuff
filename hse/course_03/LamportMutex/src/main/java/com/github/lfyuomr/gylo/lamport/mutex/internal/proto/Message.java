package com.github.lfyuomr.gylo.lamport.mutex.internal.proto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static java.lang.Long.signum;


/**
 * Lamport Mutex protocol message.
 *
 * <b>NOTE:</b> do not deserialize it through Java Serialization ever since it will break virtual clocks used by the
 * algorithm. {@link MessageFactory#readMessageFromStream} or {@link MessageFactory#messageFromBytes}
 * should always be used instead!
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Message implements Comparable<Message>, Serializable {
    int senderId;
    long timestamp;
    Type type;

    public int compareTo(@NotNull Message o) {
        val timeDiff = timestamp - o.timestamp;
        if (timeDiff != 0) {
            return signum(timeDiff);
        } else {
            return senderId - o.senderId;
        }
    }

    public enum Type {
        LOCK_REQUEST,
        ACKNOWLEDGE,
        LOCK_RELEASE,
        STOP
        ;
    }
}
