package com.github.lfyuomr.gylo.kango.client.proto;

import com.github.lfyuomr.gylo.kango.client.model.Contact;
import com.github.lfyuomr.gylo.kango.client.model.Conversation;
import com.github.lfyuomr.gylo.kango.client.model.Message;
import com.github.lfyuomr.gylo.kango.client.proto.exceptions.*;
import com.github.lfyuomr.gylo.kango.client.proto.messages.client2server.*;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.*;
import com.github.lfyuomr.gylo.kango.client.util.Config;
import com.github.lfyuomr.gylo.kango.client.util.Crypto;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;


class ConnectionHolder {
    private static final String SERVER_ADDR = "127.0.0.1";
    private static final int SERVER_PORT = 1488;
    private static final int SERVER_RESPONSE_TIMEOUT = 2000000;

    private Socket socket;
    private SocketListener listener;
    private ConversationCreationManager conversationCreator;
    private Consumer<KangoException> connectionExceptionHandler;
    private BiConsumer<Message, Long> chatMessageReceiver;
    private Consumer<Conversation> createdConversationReceiver;
    private Runnable onConversationCreationFailed;
    private ObservableList<ResponseWaiter> responseWaiters;
    private long authorizedAccountId;
    private Dictionary<String, BigInteger> secretKeys;
    private State state = State.READY;

    ConnectionHolder(BiConsumer<Message, Long> chatMessageReceiver,
                     Consumer<Conversation> createdConversationReceiver,
                     Consumer<KangoException> connectionExceptionHandler,
            Runnable onConversationCreationFailed) {
        this.chatMessageReceiver = chatMessageReceiver;
        this.createdConversationReceiver = createdConversationReceiver;
        this.onConversationCreationFailed = onConversationCreationFailed;
        this.conversationCreator = new ConversationCreationManager();
        this.connectionExceptionHandler = e -> {disconnect(); connectionExceptionHandler.accept(e);};
        this.responseWaiters = FXCollections.observableArrayList();
    }

    private void
    disconnect() {
        try {
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveSecretKeys();
        state = State.READY;
    }

    void
    saveSecretKeys() {
        if (state == State.AUTHORIZED) {
            Config.saveCryptoKeys(authorizedAccountId, secretKeys);
        }
    }

    public State getState() {
        return state;
    }

    void
    authorize(String login,
              String password,
              Consumer<AuthorizationSucceeded> onSucceeded,
              Consumer<KangoException> onFailed) {
        if (!connect()) {
            return;
        }

        Authorize message = new Authorize(login, password.hashCode());
        ResponseWaiter waiter = new ResponseWaiter(message.getResponseRecognizer(), mes -> {
            if (mes instanceof AuthorizationSucceeded) {
                toAuthorizedState(((AuthorizationSucceeded) mes).id);
                onSucceeded.accept((AuthorizationSucceeded) mes);
            }
            else
                onFailed.accept(new InvalidLoginPasswordKangoException());
        });
        ResponseInspector inspector = new ResponseInspector(waiter);

        responseWaiters.add(waiter);
        sendProtoMessage(message);
        inspector.run();
    }

    /**
     * Try to connect to server. If failed, call {@link #connectionExceptionHandler}.
     *
     * @return true if connection succeeded, false otherwise
     */
    private boolean
    connect() {
        if (state.isConnected()) {
            return true;
        }

        toReadyState();

        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            runListener(
                    event -> protoMessageReceiver((ServerToClientProtoMessage) event.getSource().getValue()),
                    event -> connectionExceptionHandler.accept((KangoException) event.getSource().getException())
            );
        } catch (IOException e) {
            this.connectionExceptionHandler.accept(new ConnectionKangoException());
            return false;
        }
        state = State.CONNECTED;
        return true;
    }

    private void
    toAuthorizedState(long accountId) {
        if (state == State.AUTHORIZED) {
            saveSecretKeys();
        }

        authorizedAccountId = accountId;
        loadSecretKeys();
        state = State.AUTHORIZED;
    }

    private void
    sendProtoMessage(ClientToServerProtoMessage message) {
        try {
            final BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            os.write(message.toXML());
            os.write("\n\0\n");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void
    toReadyState() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (listener != null) {
            listener.cancel();
        }

        socket = null;
        listener = null;
        responseWaiters.clear();
        state = State.READY;
    }

    private void
    runListener(EventHandler<WorkerStateEvent> onSucceeded, EventHandler<WorkerStateEvent> onFailed)
            throws IOException {
        if (socket == null) {
            return;
        }
        if (listener != null) {
            listener.cancel();
        }

        listener = new SocketListener();
        listener.setIs(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        listener.setPeriod(Duration.seconds(0.01));
        listener.setOnSucceeded(onSucceeded);
        listener.setOnFailed(onFailed);
        listener.start();
    }

    private void
    protoMessageReceiver(ServerToClientProtoMessage message) {
        System.out.println("Received new message from server!");
        System.out.println("Type: " + message.getClass());
        try {
            System.out.println("Message: " + message.toXML());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        //------------------------------
        if (message instanceof InputChatMessage) {
            final InputChatMessage mes = (InputChatMessage) message;
            final BigInteger key = secretKeys.get(String.valueOf(mes.conversationId));
            if (key == null) {
                throw new RuntimeException("Unexpected conversation id " + mes.conversationId +
                        "in input chat message. No secret key for this chat. Message was ignored");
            }
            final String text = Crypto.decode(mes.text, key);
            final Message result = new Message(mes.author, text);
            chatMessageReceiver.accept(result, mes.conversationId);
            return;
        }

        for (ResponseWaiter cur : responseWaiters) {
            if (cur.recognize(message)) {
                cur.handleResponse(message);
                responseWaiters.removeAll(cur);
                return;
            }
        }

        if (message instanceof CCPowerKey) {
            conversationCreator.proceedProtoMessage(message, false);
        }
        else if (!(message instanceof CCFailed)) {
            throw new RuntimeException("Unexpected message: " + message);
        }
    }

    private void
    loadSecretKeys() {
        secretKeys = Config.getAllConversationsCryptoKeys(authorizedAccountId);
    }

    void
    register(
            String login,
            String password,
            String firstName,
            String lastName,
            Consumer<RegistrationSucceeded> onSucceeded,
            Consumer<KangoException> onFailed) {
        if (!connect()) {
            return;
        }

        Register message = new Register(login, password.hashCode(), firstName, lastName);
        ResponseWaiter waiter = new ResponseWaiter(
                message.getResponseRecognizer(),
                response -> {
                    if (response instanceof RegistrationSucceeded) {
                        toAuthorizedState(((RegistrationSucceeded) response).id);
                        onSucceeded.accept((RegistrationSucceeded) response);
                    }
                    else {
                        onFailed.accept(new UnavailableLoginKangoException());
                    }
                }
        );
        ResponseInspector inspector = new ResponseInspector(waiter);

        responseWaiters.add(waiter);
        sendProtoMessage(message);
        inspector.run();
    }

    void
    sendChatMessage(Conversation conv, String text, Runnable onSucceeded, Consumer<KangoException> onFailed) {

        byte[] cipher = Crypto.encode(text, secretKeys.get(String.valueOf(conv.getId())));
        SendChatMessage message = new SendChatMessage(conv.getId(), new Random().nextInt(), cipher);
        ResponseWaiter waiter = new ResponseWaiter(
                message.getResponseRecognizer(),
                response -> {
                    if (response instanceof ChatMessageSent) {
                        onSucceeded.run();
                    }
                    else {
                        onFailed.accept(new ConnectionKangoException());
                    }
                }
        );
        ResponseInspector inspector = new ResponseInspector(waiter);

        responseWaiters.add(waiter);
        sendProtoMessage(message);
        inspector.run();
    }

    void
    searchContact(String query, Consumer<ContactSearchResult> onSucceeded, Consumer<KangoException> onFailed) {
        SearchContact message = new SearchContact(query);
        ResponseWaiter waiter = new ResponseWaiter(
                message.getResponseRecognizer(),
                response -> {
                    if (response instanceof ContactSearchResult)
                        onSucceeded.accept((ContactSearchResult) response);
                    else
                        onFailed.accept(new ConnectionKangoException());
                }
        );
        ResponseInspector inspector = new ResponseInspector(waiter);

        responseWaiters.addAll(waiter);
        sendProtoMessage(message);
        inspector.run();
    }

    void
    createConversation(String title, List<Contact> participants) {

        final List<Long> ids = participants.stream().map(Contact::getId).collect(Collectors.toList());
        final CreateConversation message = new CreateConversation(title, ids);
        final ResponseWaiter waiter = new ResponseWaiter(
                message.getResponseRecognizer(),
                m -> conversationCreator.proceedProtoMessage(m, true));
        final ResponseInspector inspector = new ResponseInspector(waiter);

        responseWaiters.add(waiter);
        sendProtoMessage(message);
        // TODO: create new ConversationCreator
        inspector.run();
    }

    private enum State {
        READY(false),
        CONNECTED(true),
        AUTHORIZED(true);

        private boolean connected;

        State(boolean connected) {
            this.connected = connected;
        }

        public boolean isConnected() {
            return connected;
        }
    }

    private static class SocketListener extends ScheduledService<ServerToClientProtoMessage> {
        final private ObjectProperty<BufferedReader> is = new SimpleObjectProperty<>();

        public BufferedReader getIs() {
            return is.get();
        }

        void setIs(BufferedReader is) {
            this.is.set(is);
        }

        public ObjectProperty<BufferedReader> isProperty() {
            return is;
        }

        @Override
        protected Task<ServerToClientProtoMessage> createTask() {
            return new Task<ServerToClientProtoMessage>() {
                @Override
                protected ServerToClientProtoMessage call() throws Exception {
                    StringBuilder builder = new StringBuilder();
                    while(true) {
                        String cur = is.get().readLine();
                        if (cur == null)
                            throw new ConnectionKangoException();

                        if (!cur.equals("\0")) {
                            builder.append(cur);
                        }
                        else
                            return ServerToClientProtoMessage.fromXML(builder.toString());
                    }
                }
            };
        }
    }

    private static class ResponseWaiter {
        private Predicate<ServerToClientProtoMessage> recognizer;
        private Consumer<ServerToClientProtoMessage> responseHandler;

        ResponseWaiter(
                Predicate<ServerToClientProtoMessage> recognizer,
                Consumer<ServerToClientProtoMessage> responseHandler) {
            this.recognizer = recognizer;
            this.responseHandler = responseHandler;
        }

        public Predicate<ServerToClientProtoMessage> getRecognizer() {
            return recognizer;
        }

        public void setRecognizer(Predicate<ServerToClientProtoMessage> recognizer) {
            this.recognizer = recognizer;
        }

        public Consumer<ServerToClientProtoMessage> getResponseHandler() {
            return responseHandler;
        }

        public void setResponseHandler(Consumer<ServerToClientProtoMessage> responseHandler) {
            this.responseHandler = responseHandler;
        }

        boolean recognize(ServerToClientProtoMessage message) {
            return recognizer.test(message);
        }

        void handleResponse(ServerToClientProtoMessage message) {
            responseHandler.accept(message);
        }
    }

    private class ResponseInspector {
        private Service<Void> service;

        ResponseInspector(ResponseWaiter target) {
            service = new Service<Void>() {
                final IntegerProperty timeout = new SimpleIntegerProperty(SERVER_RESPONSE_TIMEOUT);

                @Override
                protected void succeeded() {
                    super.succeeded();
                    for (ResponseWaiter cur : responseWaiters) {
                        if (cur == target) {
                            responseWaiters.removeAll(cur);
                            connectionExceptionHandler.accept(new ResponseTimeoutKangoException());
                            return;
                        }
                    }
                }

                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Thread.sleep(timeout.get());
                            return null;
                        }
                    };
                }
            };
        }

        void run() {
            service.start();
        }
    }

    private class ConversationCreationManager {
        private List<CreationState> constructioningConversations = new ArrayList<>();

        /**
         * Default server messages about conversation creation processor.
         *
         * @param message     input message from server
         * @param wasExpected indicates the message is response or query
         */
        void proceedProtoMessage(ServerToClientProtoMessage message, boolean wasExpected) {
            if (state != State.AUTHORIZED) {
                throw new RuntimeException("Conversation creation is not allowed when not authorized!");
            }

            if (message instanceof CCPowerKey) {
                final CCPowerKey mes = (CCPowerKey) message;
                final CreationState thisState = findState(mes.titleHash, mes.modulo);
                //TODO: this is something strange
                if (thisState.iteration == 0) {
                    thisState.notifyUser = wasExpected;
                }
                thisState.iteration = mes.iteration;

                final CCPoweredKey response = new CCPoweredKey(
                        mes.iteration,
                        mes.titleHash,
                        mes.value.modPow(thisState.primaryKey, mes.modulo)
                );
                ResponseWaiter waiter = new ResponseWaiter(
                        response.getResponseRecognizer(),
                        m -> proceedProtoMessage(m, true)
                );
                ResponseInspector inspector = new ResponseInspector(waiter);

                responseWaiters.add(waiter);
                sendProtoMessage(response);
                inspector.run();
            }
            else if (message instanceof CCConfirm) {
                final CCConfirm mes = (CCConfirm) message;
                final CreationState thisState = findState(mes.titleHash, null);
                ObservableList<Contact> participants = FXCollections.observableArrayList();
                for (int i = 0; i < mes.ids.size(); i++) {
                    participants.add(new Contact(
                            mes.ids.get(i),
                            mes.logins.get(i),
                            mes.firstNames.get(i),
                            mes.lastNames.get(i)
                    ));
                }

                thisState.conversation = new Conversation(
                        mes.conversationId,
                        mes.title,
                        FXCollections.observableArrayList(),
                        participants
                );
                thisState.resultKey = mes.value.modPow(thisState.primaryKey, thisState.modulo);

                CCConfirmation response = new CCConfirmation(mes.conversationId, mes.titleHash);
                ResponseWaiter waiter = new ResponseWaiter(
                        response.getResponseRecognizer(),
                        m -> proceedProtoMessage(m, true));
                ResponseInspector inspector = new ResponseInspector(waiter);

                responseWaiters.add(waiter);
                sendProtoMessage(response);
                inspector.run();
            }
            else if (message instanceof CCSucceeded) {
                CreationState thisState = findState(((CCSucceeded) message).titleHash, null);
                Conversation conversation = thisState.conversation;
                BigInteger secretKey = thisState.resultKey;
                constructioningConversations.remove(thisState);

                secretKeys.put(String.valueOf(conversation.getId()), secretKey);
                createdConversationReceiver.accept(conversation);
            }
            else if (message instanceof CCFailed) {
                CreationState thisState = findState(((CCFailed) message).titleHash, null);
                if (thisState == null || thisState.notifyUser) {
                    onConversationCreationFailed.run();
                }
                if (thisState != null) {
                    constructioningConversations.remove(thisState);
                }
            }
            else
                throw new RuntimeException("Invalid message type: " + message.getClass());
        }

        /**
         * Returns the state of conversation creation. There're two options available if no state found:
         * 1. if modulo != {@code null}, create new state and return it,
         * 2. otherwise, return {@code null}
         *
         * @param titleHash
         * @param modulo
         * @return
         */
        CreationState findState(int titleHash, BigInteger modulo) {
            for (CreationState creationState : constructioningConversations) {
                if (creationState.titleHash == titleHash)
                    return creationState;
            }
            if (modulo != null) {
                final CreationState result = new CreationState(titleHash, modulo);
                constructioningConversations.add(result);
                return result;
            }
            else {
                return null;
            }
        }

        private class CreationState {
            /**
             * notify user about fail in conversation creation.
             */
            boolean notifyUser = false;
            int iteration;
            int titleHash;
            BigInteger primaryKey;
            BigInteger modulo;
            BigInteger resultKey;
            Conversation conversation;

            CreationState(int titleHash, BigInteger modulo) {
                iteration = 0;
                this.titleHash = titleHash;
                primaryKey = Crypto.generateBigInteger();
                this.modulo = modulo;
            }
        }
    }
}
