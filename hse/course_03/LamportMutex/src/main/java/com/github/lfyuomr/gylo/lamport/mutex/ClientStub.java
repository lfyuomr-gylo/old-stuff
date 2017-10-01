package com.github.lfyuomr.gylo.lamport.mutex;

import com.github.lfyuomr.gylo.lamport.mutex.internal.proto.Message;
import com.github.lfyuomr.gylo.lamport.mutex.internal.proto.MessageFactory;
import lombok.Value;
import lombok.val;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ClientStub {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Logger log;
    private final Configuration config;
    private final MessageFactory messageFactory;

    ClientStub(Configuration config, MessageFactory messageFactory, Logger logger) {
        this.log = logger;
        this.config = config;
        this.messageFactory = messageFactory;
    }

    /**
     * Broadcast lock request.
     *
     * @return whether the request will be broadcasted or not due to client is stopped.
     */
    synchronized boolean lockRequest() {
        val message = messageFactory.createMessage(Message.Type.LOCK_REQUEST);
        enqueueBroadcast(message);
        return sendToMe(message);
    }

    /**
     * Send acknowledgement to specified node.
     *
     * @param id node to send acknowledgement to.*
     * @return whether the request will be sent or not due to client is stopped.
     */
    boolean acknowledge(int id) {
        final SocketAddress address;
        if (id == config.getMyId()) {
            address = new InetSocketAddress("localhost", config.getMyPort());
        }
        else {
            address = config.getOtherNodes().get(id);
        }

        val message = messageFactory.createMessage(Message.Type.ACKNOWLEDGE);
        return enqueuePackage(new Package(message, address));
    }

    /**
     * Broadcast 'release' message.
     *
     * @return whether the request will be broadcasted or not due to client is stopped.
     */
    synchronized boolean release() {
        val message = messageFactory.createMessage(Message.Type.LOCK_RELEASE);
        return enqueueBroadcast(message);
    }

    void stop() {
        synchronized (executor) {
            if (!executor.isShutdown()) {
                val stopMessage = messageFactory.getStopMessage();
                enqueueBroadcast(stopMessage);
                executor.shutdown();
            }
        }
    }

    private boolean enqueueBroadcast(Message message) {
        synchronized (executor) {
            return config.getOtherNodes()
                         .values()
                         .stream()
                         .map(address -> new Package(message, address))
                         .map(this::enqueuePackage)
                         .allMatch(x -> x);
        }
    }

    private boolean sendToMe(Message message) {
        val pack = new Package(message, new InetSocketAddress("localhost", config.getMyPort()));
        synchronized (executor) {
            return enqueuePackage(pack);
        }
    }

    /**
     * <b>NOTE:</b> this method should be called in {@code synchronized(executor)} block.
     * Otherwise a thread dispatching the package may be blocked, if this method was called concurrently with
     * {@link ClientStub#stop()}.
     *
     * @return whether package was enqueued or not due to executor shutdown.
     */
    private boolean enqueuePackage(Package pack) {
        if (!executor.isShutdown()) {
            executor.execute(() -> {
                while (!tryToSendPackage(pack)) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private boolean tryToSendPackage(Package pack) {
        try (val socket = new Socket()) {
            socket.connect(pack.getAddress());
            new ObjectOutputStream(socket.getOutputStream()).writeObject(pack.getMessage());
            log.debug("client {}: message sent: {} to {}",
                    config.getMyId(), pack.getMessage(), pack.getAddress()
            );
        } catch (IOException e) {
            log.warn("client {}: failed to send message: {} to {}",
                    config.getMyId(), pack.getMessage(), pack.getAddress()
            );
            return false;
        }

        return true;
    }

    @Value
    private static class Package {
        Message message;
        SocketAddress address;
    }
}
