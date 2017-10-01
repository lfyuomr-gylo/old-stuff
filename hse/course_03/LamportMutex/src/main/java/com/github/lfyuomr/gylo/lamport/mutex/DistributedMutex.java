package com.github.lfyuomr.gylo.lamport.mutex;


import com.github.lfyuomr.gylo.lamport.mutex.internal.proto.MessageFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import java.io.Closeable;
import java.io.IOException;

import static com.github.lfyuomr.gylo.lamport.mutex.ServerStub.Status.ACQUIRED;

/**
 * Distributed mutex algorithm.
 * It's required by the algorithm to all nodes be stable -- any node crash will break the whole system work.
 */
public class DistributedMutex implements Closeable {
    @Getter @Setter private static Logger defaultLogger = NOPLogger.NOP_LOGGER;

    private final Logger log;

    private final Configuration config;
    private final ClientStub clientStub;
    private final ServerStub serverStub;

    private State state = State.NOT_ACQUIRED;
    private final Object stateLock = new Object();
    private volatile @Nullable Thread ownerThread = null;

    /**
     * Simple alias for {@code DistributedMutex(configuration, null)}
     */
    public DistributedMutex(Configuration configuration) throws IOException {
        this(configuration, null);
    }

    /**
     * This blocking constructor creates distributed mutex and awaits all the nodes in configuration to connect.
     * @param configuration distributed system configuration.
     * @param logger logger for this mutex instance. If it is null, {@link DistributedMutex#defaultLogger} will be used.
     * @throws IOException if failed to bind server socket to port specified in configuration.
     */
    private DistributedMutex(Configuration configuration, @Nullable Logger logger) throws IOException {
        this.config = configuration;
        logger = logger == null ? defaultLogger : logger;
        this.log = logger;
        val messageFactory = new MessageFactory(configuration.getMyId());
        clientStub = new ClientStub(configuration, messageFactory, logger);
        serverStub = new ServerStub(clientStub, configuration, messageFactory, logger);

        val nodesNum = config.getOtherNodes().entrySet().size() + 1;
        log.info("mutex {}: created new mutex on port {} with {} nodes in configuration.",
                config.getMyId(), config.getMyPort(), nodesNum);
        log.info("mutex {}: configuration: {}", config.getMyId(), configuration);
    }

    /**
     * Lock mutex. This method is synchronous.
     */
    public void lock() throws InterruptedException {
        synchronized (stateLock) {
            switch (state) {
                case LOCK_ACQUIRED:
                    throw new IllegalStateException("Mutex is already locked");
                case PENDING_ACQUIREMENT:
                    throw new IllegalStateException("Already pending lcck acquirement");
                case DESTROYED:
                    throw new MutexDestroyedException();
                case NOT_ACQUIRED:
                    log.info("mutex {}: lock", config.getMyId());
                    if (clientStub.lockRequest()) {
                        state = State.PENDING_ACQUIREMENT;
                    } else {
                        state = State.DESTROYED;
                        throw new MutexDestroyedException();
                    }
                    break;
            }
        }
        val status = serverStub.getAcquirementNotifier().exchange(null);

        synchronized (stateLock) {
            if (status == ACQUIRED) {
                state = State.LOCK_ACQUIRED;
                ownerThread = Thread.currentThread();
            } else {
                state = State.DESTROYED;
                throw new MutexDestroyedException();
            }
        }

        log.info("mutex {}: acquire", config.getMyId());
    }

    /**
     * Release mutex.
     *
     * @throws IllegalStateException if mutex is not locked.
     * @throws MutexDestroyedException if mutex destroyed.
     */
    public void unlock() throws IllegalStateException, MutexDestroyedException {
        synchronized (stateLock) {
            switch (state) {
                case NOT_ACQUIRED:
                case PENDING_ACQUIREMENT:
                    throw new IllegalStateException("Mutex is not locked");
                case DESTROYED:
                    throw new MutexDestroyedException();
                case LOCK_ACQUIRED:
                    if (ownerThread != Thread.currentThread()) {
                        throw new IllegalThreadStateException("Mutex is locked by another thread: " + ownerThread +
                                ". Can not unlock it in other thread: " + Thread.currentThread());
                    }

                    log.info("mutex {}: release", config.getMyId());
                    if (clientStub.release()) {
                        state = State.NOT_ACQUIRED;
                        ownerThread = null;
                    } else {
                        state = State.DESTROYED;
                        throw new MutexDestroyedException();
                    }
            }
        }
    }

    @Override
    public void close() {
        log.info("close");

        synchronized (stateLock) {
            state = State.DESTROYED;
            clientStub.stop();
        }
    }


    private enum State {
        LOCK_ACQUIRED,
        PENDING_ACQUIREMENT,
        NOT_ACQUIRED,
        DESTROYED,
        ;
    }
}
