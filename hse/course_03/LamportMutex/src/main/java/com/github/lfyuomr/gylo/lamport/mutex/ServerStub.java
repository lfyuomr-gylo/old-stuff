package com.github.lfyuomr.gylo.lamport.mutex;

import com.github.lfyuomr.gylo.lamport.mutex.internal.proto.Message;
import com.github.lfyuomr.gylo.lamport.mutex.internal.proto.MessageFactory;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static com.github.lfyuomr.gylo.lamport.mutex.ServerStub.Status.ACQUIRED;
import static com.github.lfyuomr.gylo.lamport.mutex.ServerStub.Status.STOPPED;
import static java.lang.Long.max;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

class ServerStub {
    private final Logger log;

    private final ClientStub clientStub;
    private final int myId;
    private final Configuration config;

    @Getter private final Exchanger<Status> acquirementNotifier = new Exchanger<>();

    private final ServerSocket serverSocket;
    private final MessageFactory messageFactory;
    private final Queue<Message> requestMessageQueue = new PriorityQueue<>();
    private final Map<Integer, Long> lastReceivedMessageTimestamp = new HashMap<>(); // excluding this node

    private final Map<Integer, Boolean> stoppedNodes;
    private int stoppedNodesNum;

    /**
     * Create new server stub.
     *
     * @param config       system configuration.
     * @throws IOException if failed to bind listening socket to specified port.
     */
    ServerStub(
            ClientStub clientStub,
            Configuration config,
            MessageFactory messageFactory,
            @Nullable Logger logger)
            throws IOException {
        this.log = logger;
        this.clientStub = clientStub;
        this.config = config;
        this.serverSocket = new ServerSocket(config.getMyPort());
        this.messageFactory = messageFactory;
        this.stoppedNodes = new HashMap<>(config.getOtherNodes().size());
        this.stoppedNodesNum = 0;
        this.myId = config.getMyId();

        for (Integer otherNodeId : config.getOtherNodes().keySet()) {
            lastReceivedMessageTimestamp.put(otherNodeId, Long.MIN_VALUE);
            stoppedNodes.put(otherNodeId, false);
        }
        if (config.initialMutexOwnerId() != config.getMyId()) {
            requestMessageQueue.offer(messageFactory.getInitialLockRequestMessage(config));
        }

        init();

        val executor = Executors.newSingleThreadExecutor();
        executor.execute(this::listenToSocket);
        executor.shutdown();
    }

    /**
     * Wait until get ping message from all other nodes.
     */
    private void init() {
        if (config.initialMutexOwnerId() == myId) {
            clientStub.release();
        }
    }

    private void listenToSocket() {
        while (stoppedNodesNum < config.getOtherNodes().size()) {
            try (val socket = serverSocket.accept()) {
                log.debug("server {}: accepted new connection", myId);

                val message = messageFactory.readMessageFromStream(socket.getInputStream());
                log.debug("server {}: received message: {}", myId, message);

                val senderId = message.getSenderId();
                if (senderId != myId && !config.getOtherNodes().containsKey(senderId)) {
                    log.warn("server {}: unexpected node id: {}. Ignore the message",
                            myId, message.getSenderId()
                    );
                    continue;
                }

                processMessage(message);
                tryToAcquire();
            } catch (IOException e) {
                log.warn("server {}: IOException. Ignore it. {}", e);
            } catch (ClassNotFoundException e) {  // ignore incorrect message
                log.warn("server {}: failed to read message from socket. Ignore it. {}", myId, e);
            }
        }

        notifyOnAcquired(1000);
    }

    private void processMessage(Message message) {
        val senderId = message.getSenderId();

        if (lastReceivedMessageTimestamp.containsKey(senderId)) {
            val lastTime = max(lastReceivedMessageTimestamp.get(senderId), message.getTimestamp());
            lastReceivedMessageTimestamp.put(senderId, lastTime);
        }

        switch (message.getType()) {
            case LOCK_REQUEST:
                requestMessageQueue.offer(message);
                if (message.getSenderId() != myId) {
                    clientStub.acknowledge(message.getSenderId());
                }
                break;
            case LOCK_RELEASE:
                requestMessageQueue.removeIf(mes -> mes.getSenderId() == message.getSenderId());
                break;
            case ACKNOWLEDGE:
                break;
            case STOP:
                clientStub.stop();
                stoppedNodesNum += stoppedNodes.get(senderId) ? 0 : 1;
                stoppedNodes.put(senderId, true);
                break;
        }
    }

    private void tryToAcquire() {
        log.trace("server {}: try to acquire. {}", myId, getStateStringRepresentation());

        if (requestMessageQueue.isEmpty() || requestMessageQueue.peek().getSenderId() != myId) {
            return;
        }

        val requestTimestamp = requestMessageQueue.peek().getTimestamp();
        for (Long cur : lastReceivedMessageTimestamp.values()) {
            if (cur <= requestTimestamp) {
                return;
            }
        }

        log.debug("server {}: acquire lock.", myId);
        requestMessageQueue.remove();

        notifyOnAcquired();
    }

    private void notifyOnAcquired() {
        notifyOnAcquired(Integer.MAX_VALUE);
    }

    private void notifyOnAcquired(int timeoutMillis) {
        try {
            acquirementNotifier.exchange(stoppedNodesNum == 0 ? ACQUIRED : STOPPED, timeoutMillis, MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("Server stub listening thread was interrupted");
            Thread.currentThread().interrupt();
        } catch (TimeoutException ignored) {
        }
    }

    private String getStateStringRepresentation() {
        val requestQueue = requestMessageQueue.stream()
                                              .map(Message::toString)
                                              .reduce((m1, m2) -> m1 + "\n" + m2)
                                              .orElse("EMPTY");
        val timestamps =
                lastReceivedMessageTimestamp.entrySet()
                                            .stream()
                                            .map(e -> e.getKey() + " -> " + e.getValue())
                                            .reduce((r, n) -> r + "\n" + n)
                                            .orElse("THERE SHOULD BE TIMESTAMPS, BUT SOMETHING WENT WHRONG");
        return "REQUEST QUEUE:\n" + requestQueue + "\nTIMESTAMPS:\n" + timestamps;
    }

    enum Status {
        ACQUIRED,
        STOPPED,
        ;
    }
}
