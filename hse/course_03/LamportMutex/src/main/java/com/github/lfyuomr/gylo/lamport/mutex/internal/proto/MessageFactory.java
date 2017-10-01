package com.github.lfyuomr.gylo.lamport.mutex.internal.proto;

import com.github.lfyuomr.gylo.lamport.mutex.Configuration;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Long.max;

/**
 * An instance of this class manages creation of all outcoming messages and reading of all incoming messages in a single
 * Lamport Mutex protocol. Internally, it maintains Lamport logical clock and updates it whenever an outcoming
 * message created or incoming one read, so it is guaranteed that
 * <ul>
 *     <li>If message A is created before message B created, than A.timestamp < B.timestamp</li>
 *     <li>If message A is received before message B created, than A.timestamp > B.timestamp</li>
 * </ul>
 */
public class MessageFactory {
    private boolean getInitialLockRequestMessageCalled = false;
    private boolean getInitialPingMessageCalled = false;
    private final int ownerId;
    private final AtomicLong clock = new AtomicLong(0);

    private final Object stopMessageLock = new Object();
    private Message stopMessage;

    public MessageFactory(int ownerId) {
        this.ownerId = ownerId;
    }

    public Message readMessageFromStream(InputStream outputStream) throws IOException, ClassNotFoundException {
        val message = (Message) new ObjectInputStream(outputStream).readObject();
        clock.accumulateAndGet(message.getTimestamp(), (cur, received) -> max(cur, received + 1));
        return message;
    }

    /**
     * This is just an alias of {@code readMessageFromStream(new ByteArrayInputStream(bytes))}
     */
    public Message messageFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        return readMessageFromStream(new ByteArrayInputStream(bytes));
    }

    public @NotNull Message createMessage(@NotNull Message.Type type) {
        return new Message(
                ownerId,
                clock.getAndIncrement(),
                type
        );
    }

    public Message getStopMessage() {
        if (stopMessage == null) {
            synchronized (stopMessageLock) {
                if (stopMessage == null) {
                    stopMessage = new Message(ownerId, clock.getAndIncrement(), Message.Type.STOP);
                }
            }
        }
        return stopMessage;
    }

    /**
     * Create initial lock request message for message queue initialization.
     * The message's timestamp is less than any other created message's timestamp.
     * This method creates identical message on each node in passed configuration.
     *
     * <b>NOTE:</b> this method should be called only once. Otherwise, it will throw {@link IllegalStateException}
     */
    public Message getInitialLockRequestMessage(Configuration config) {
        if (getInitialLockRequestMessageCalled) {
            throw new IllegalStateException("this method should not be called twice");
        }
        getInitialLockRequestMessageCalled = true;

        return new Message(
                config.initialMutexOwnerId(),
                -1,
                Message.Type.LOCK_REQUEST
        );
    }

    /**
     * Create ping message for system initialization process.
     *
     * <b>NOTE:</b> this method should be called only once. Otherwise, it will throw {@link IllegalStateException}
     */
    public Message getInitialPingMessage() {
        if (getInitialPingMessageCalled) {
            throw new IllegalStateException("this method should not be called twice");
        }
        getInitialPingMessageCalled = true;

        return new Message(ownerId, -1, Message.Type.ACKNOWLEDGE);
    }
}
