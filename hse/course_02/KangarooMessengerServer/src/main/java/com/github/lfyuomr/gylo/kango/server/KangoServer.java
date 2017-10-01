package com.github.lfyuomr.gylo.kango.server;

import com.github.lfyuomr.gylo.kango.server.db.mappings.DBConversation;
import com.github.lfyuomr.gylo.kango.server.db.mappings.DBUser;
import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.CCConfirmation;
import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.CCPoweredKey;
import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.ClientToServerProtoMessage;
import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.CreateConversation;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.CCConfirm;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.CCFailed;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.CCPowerKey;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.CCSucceeded;
import com.github.lfyuomr.gylo.kango.server.util.PublicKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KangoServer {
    private static final int SERVER_PORT = 1488;
    private static final ConcurrentHashMap<Integer, ConversationCreator> creators = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        for (;;) {
            Socket socket = serverSocket.accept();
            UserServant.startNewServant(socket);
        }
    }

    public static void tryToCreate(CreateConversation message, UserServant servant) throws Exception {
        synchronized (creators) {
            if (creators.containsKey(message.titleHash)) {
                CCFailed failed = new CCFailed(message.titleHash, "Try again later");
                try {
                    servant.sendProtoMessage(failed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            ConversationCreator creator = null;
            try {
                creator = new ConversationCreator(message);
            } catch (Exception e) {
                CCFailed failed = new CCFailed(message.titleHash, "Because fuck you!");
                servant.sendProtoMessage(failed);
                return;
            }
            creators.put(message.titleHash, creator);
            creator.create();
        }
    }

    public static Exchanger<ClientToServerProtoMessage> getExchanger(Integer titleHash, Long userId) {
        ConversationCreator creator = creators.get(titleHash);
        if (creator == null)
            return null;
        return creator.getExchanger(userId);
    }

    private static class ConversationCreator {
        private static final int ITERATION_TIMEOUT = 1000000;
        private static final TimeUnit ITERATION_TIMEOUT_TIME_UNIT = TimeUnit.MILLISECONDS;
        private static final int KEY_LENGTH = 128;
        private static final int MODULO_LENGTH = 128;

        private final int titleHash;
        private final String title;
        private final HashMap<Long, Exchanger<ClientToServerProtoMessage>> exchangers;
        private final List<DBUser> participants;
        private final ExecutorService creatorService;
        private List<BigInteger> keys;
        private BigInteger modulo;
        private DBConversation createdConversation;

        /**
         * Starts to
         * @param message
         */
        private ConversationCreator(CreateConversation message) throws Exception {
            participants = new ArrayList<>(message.ids.size());
            for (Long id : message.ids) {
                if (DBUser.isOnline(id)) {
                    participants.add(DBUser.getById(id));
                }
                else {
                    //TODO: handle this exception
                    throw new Exception("User with id " + id + " is offline. Can't create createdConversation");
                }
            }
            exchangers = new HashMap<>(participants.size());
            message.ids.forEach(id -> exchangers.put(id, new Exchanger<>()));
            title = message.title;
            titleHash = message.titleHash;
            creatorService = Executors.newSingleThreadExecutor();
        }

        /**
         * Initiate new conversation creation process.
         */
        private void create() {
            creatorService.execute(this::createConversation);
        }

        /**
         * get exchanger for user with specified id
         * @param id
         * @return
         */
        private Exchanger<ClientToServerProtoMessage> getExchanger(@NotNull Long id) {
            return exchangers.get(id);
        }

        private void createConversation() {
            System.out.println("Creating conversation " + title + " in thread " + Thread.currentThread());
            try {
                diffieHellman();
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;

                creators.remove(titleHash);
                if (createdConversation != null) {
                    createdConversation.deleteFromDB();
                }

                final CCFailed failure = new CCFailed(titleHash, "some shit happened");
                for (DBUser cur : participants) {
                    try {
                        cur.getServant().sendProtoMessage(failure);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            creatorService.shutdownNow();
        }

        private void diffieHellman() throws Exception {
            generateKeys();
            diffieHellmanKeyExchange();

            createdConversation = new DBConversation(title);
            participants.forEach(createdConversation::addParticipant);
            createdConversation.saveOrUpdateInDB();

            confirmCreation();

            final CCSucceeded succeeded = new CCSucceeded(createdConversation.getId(), titleHash);
            for (DBUser cur : participants) {
                try {
                    cur.getServant().sendProtoMessage(succeeded);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void generateKeys() {
            modulo = PublicKeys.generateModulo(MODULO_LENGTH);
            final BigInteger value = PublicKeys.generateModulo(KEY_LENGTH).modPow(BigInteger.ONE, modulo);
            keys = Stream.generate(() -> value).limit(participants.size()).collect(Collectors.toList());
        }

        private void diffieHellmanKeyExchange() throws Exception {
            final ExecutorService executor = Executors.newSingleThreadExecutor();

            for (int i = 0; i < keys.size() - 1; i++) {
                final Future<List<BigInteger>> futureResponses = executor.submit(createPoweredKeysWaiter(i));
                // send current keys to users
                for (int k = 0; k < keys.size(); k++) {
                    CCPowerKey query = new CCPowerKey(i, titleHash, keys.get((k + i + 1) % keys.size()), modulo);
                    if (participants.get(k).isOnline())
                        participants.get(k).getServant().sendProtoMessage(query);
                    else {
                        throw new Exception();
                    }
                }

                // await for responses
                final List<BigInteger> responses = awaitResponses(futureResponses);
                if (responses == null) {
                    throw new Exception();
                }
                for (int k = 0; k < keys.size(); k++) {
                    keys.set((k + i + 1) % keys.size(), responses.get(k));
                }
            }
        }

        private void confirmCreation() throws Exception {
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Boolean> futureWaitResult = executor.submit(createConfirmationWaiter());

            final CCConfirm notification = new CCConfirm(
                    participants.size() - 1,
                    titleHash,
                    title,
                    null,
                    createdConversation.getId(),
                    participants.stream().map(DBUser::getId).collect(Collectors.toList()),
                    participants.stream().map(DBUser::getLogin).collect(Collectors.toList()),
                    participants.stream().map(DBUser::getFirstName).collect(Collectors.toList()),
                    participants.stream().map(DBUser::getLastName).collect(Collectors.toList())
            );
            for (int i = 0; i < participants.size(); i++) {
                notification.value = keys.get(i);
                participants.get(i)
                            .getServant()
                            .sendProtoMessage(notification);
            }

            if (!futureWaitResult.get(ITERATION_TIMEOUT, ITERATION_TIMEOUT_TIME_UNIT)) {
                throw new Exception();
            }
        }

        /**
         * Awaiter traverses exchangers waits for all responses.
         * @param iteration
         * @return
         */
        private Callable<List<BigInteger>> createPoweredKeysWaiter(int iteration) {
            return new Callable<List<BigInteger>>() {
                @Override
                public List<BigInteger> call() throws Exception {
                    final List<BigInteger> result = new ArrayList<>(participants.size());
                    for (int i = 0; i < participants.size(); i++) result.add(null);

                    final int waitTime = ITERATION_TIMEOUT / participants.size(); // in microseconds

                    boolean flag = true;
                    while (flag) {
                        flag = false;
                        for (int i = 0; i < participants.size(); i++) {
                            body: {
                                if (result.get(i) != null)
                                    continue;

                                Long id = participants.get(i).getId();
                                BigInteger cur;
                                try {
                                    CCPoweredKey mes = (CCPoweredKey)
                                            exchangers.get(id).exchange(null, waitTime * 100, TimeUnit.MICROSECONDS);
                                    if (mes.iteration != iteration || mes.titleHash != titleHash || mes.value == null) {
                                        break body;
                                    }

                                    result.set(i, mes.value);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    return null;
                                } catch (ClassCastException | TimeoutException e) {
                                    break body;
                                }
                            }
                            flag = true;
                        }
                    }

                    for (BigInteger cur : result) {
                        if (cur == null)
                            (new RuntimeException("result contains unexpected null value")).printStackTrace();
                    }
                    return result;
                }
            };
        }

        private List<BigInteger> awaitResponses(Future<List<BigInteger>> future) {
            try {
                return future.get(ITERATION_TIMEOUT, ITERATION_TIMEOUT_TIME_UNIT);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return null;
            }
        }

        private Callable<Boolean> createConfirmationWaiter() {
            return new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    boolean flag = true;
                    final int waitTime = ITERATION_TIMEOUT / participants.size(); // in microseconds
                    final List<Boolean> confirmed = new ArrayList<>(participants.size());
                    for (int i = 0; i < participants.size(); i++) confirmed.add(false);

                    while(flag) {
                        flag = false;
                        for (int i = 0; i < participants.size(); i++) {
                            Long id = participants.get(i).getId();
                            CCConfirmation mes;
                            try {
                                mes = (CCConfirmation)
                                        exchangers.get(id).exchange(null, waitTime, TimeUnit.MICROSECONDS);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return false;
                            } catch (ClassCastException | TimeoutException e) {
                                flag = true;
                                continue;
                            }
                            confirmed.set(i, true);
                        }
                    }

                    return !confirmed.stream().anyMatch(el -> !el);
                }
            };
        }
    }
}
