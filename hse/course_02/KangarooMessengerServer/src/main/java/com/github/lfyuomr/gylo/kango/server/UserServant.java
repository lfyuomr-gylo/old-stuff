package com.github.lfyuomr.gylo.kango.server;

import com.github.lfyuomr.gylo.kango.server.db.mappings.DBConversation;
import com.github.lfyuomr.gylo.kango.server.db.mappings.DBMessage;
import com.github.lfyuomr.gylo.kango.server.db.mappings.DBUser;
import com.github.lfyuomr.gylo.kango.server.proto.messages.client2server.*;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class UserServant {
    //TODO: calculate adequate timeout(that guarantees that reader reads it)
    private static final int EXCHANGE_TIMEOUT = 10000;
    private static final TimeUnit EXCHANGE_TIMEOUT_TIME_UNIT = TimeUnit.MILLISECONDS;

    private static final ConcurrentLinkedQueue<UserServant> servants = new ConcurrentLinkedQueue<>();

    private Socket socket;
    private BufferedReader is;
    private BufferedWriter os;
    private DBUser authorizedUser;
    private ScheduledExecutorService socketListener;

    private UserServant(Socket socket) throws IOException {
        this.socket = socket;
        this.is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.authorizedUser = null;
        this.socketListener = Executors.newSingleThreadScheduledExecutor();
    }

    public static void startNewServant(Socket socket) throws IOException {
        final UserServant servant = new UserServant(socket);
        servants.add(servant);
        servant.runSocketListener();
        System.out.println("new servant created and started");
    }

    private void runSocketListener() {
        final Runnable listener = () -> {
            final StringBuilder buf = new StringBuilder();
            String cur;
            for (;;) {
                try {
                    cur = is.readLine();
                    if (cur == null) {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    disconnect();
                    throw new NotImplementedException();
                }

                if (!cur.equals("\0")) {
                    buf.append(cur);
                }
                else {
                    try {
                        processProtoMessage(ClientToServerProtoMessage.fromXML(buf.toString()));
                        buf.setLength(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Unexpected proto message from client or something else", e);
                    }
                }
            }
        };

        socketListener.scheduleWithFixedDelay(listener, 0, 1, TimeUnit.NANOSECONDS);
    }

    public void disconnect() {
        System.out.println("Disconnect from user " + authorizedUser.getLogin()
                + " socket " + socket + " thread " + Thread.currentThread());

        servants.remove(this);
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socketListener.shutdownNow();
    }

    private void processProtoMessage(ClientToServerProtoMessage message) throws Exception {
        System.out.println("From user " + ((authorizedUser == null) ? "" : authorizedUser.getLogin()) + " received new message: " + message.toXML());
        if (message instanceof Authorize) {
            authorize((Authorize) message);
        }
        else if (message instanceof Register) {
            register((Register) message);
        }
        else if (message instanceof SearchContact) {
            searchContact((SearchContact) message);
        }
        else if (message instanceof SendChatMessage) {
            sendChatMessage((SendChatMessage) message);
        }
        else if (message instanceof CreateConversation) {
            synchronized (Object.class) {
                System.out.println("\n\n");
                System.out.println("Trying to create conversation in Servant with socket" + this.socket);
                System.out.println("My authorized user is " + authorizedUser.getLogin());
                System.out.println("\n\n");
            }
            KangoServer.tryToCreate((CreateConversation) message, this);
        }
        else if (message instanceof CCPoweredKey) {
            Exchanger<ClientToServerProtoMessage> exchanger =
                    KangoServer.getExchanger(((CCPoweredKey) message).titleHash, authorizedUser.getId());
            if (exchanger != null) {
                exchanger.exchange(message, EXCHANGE_TIMEOUT, EXCHANGE_TIMEOUT_TIME_UNIT);
            }
        }
        else if (message instanceof CCConfirmation) {
            Exchanger<ClientToServerProtoMessage> exchanger =
                    KangoServer.getExchanger(((CCConfirmation) message).titleHash, authorizedUser.getId());
            if (exchanger != null) {
                exchanger.exchange(message, EXCHANGE_TIMEOUT, EXCHANGE_TIMEOUT_TIME_UNIT);
            }
        }
    }

    /**
     * Authorize user and send him all input chat messages
     * @param message
     * @throws Exception
     */
    private void authorize(Authorize message) throws Exception {
        DBUser user = DBUser.getByLogin(message.login);
        if (user == null) {
            final AuthorizationFailed response = new AuthorizationFailed(message.login, "Invalid login");
            sendProtoMessage(response);
            return;
        }
        else if (user.getPasswordHash() != message.passwordHash) {
            final AuthorizationFailed response = new AuthorizationFailed(message.login, "Invalid password");
            sendProtoMessage(response);
            return;
        }
        else {
            final AuthorizationSucceeded response =
                    new AuthorizationSucceeded(user.getId(), user.getLogin(), user.getFirstName(), user.getLastName());
            sendProtoMessage(response);
            authorizedUser = user;
            user.goOnline(this);

            for (DBMessage mes : user.getIncomeMessages()) {
                final InputChatMessage chatMessage = new InputChatMessage(
                        mes.getConversation().getId(),
                        mes.getAuthor().getLogin(),
                        mes.getEncodedText()
                );
                sendProtoMessage(chatMessage);
                mes.deleteFromDB();
            }
            user.getIncomeMessages().clear();
            return;
        }
    }

    /**
     * Register user
     * @param message
     * @throws Exception
     */
    private void register(Register message) throws Exception {
        System.out.println("I'm in register function");
        if (DBUser.getByLogin(message.login) != null) {
            final RegistrationFailed response = new RegistrationFailed(
                    message.login,
                    message.firstName,
                    message.lastName,
                    "Specified user name is already occupied"
            );
            sendProtoMessage(response);
            return;
        }
        else {
            final DBUser user = new DBUser(message.login, message.paasswordHash, message.firstName, message.lastName);
            user.saveOrUpdateInDB();
            System.out.println("Created new user");
            System.out.println(
                    "id: " + user.getId() +
                    "\nlogin: " + user.getLogin() +
                    "\nfirst name: " + user.getFirstName() +
                    "\nlast name: " + user.getLastName()
            );
            final RegistrationSucceeded response = new RegistrationSucceeded(
                    user.getId(),
                    user.getLogin(),
                    user.getFirstName(),
                    user.getLastName()
            );
            sendProtoMessage(response);
            user.goOnline(this);
            authorizedUser = user;

            return;
        }
    }

    /**
     * Search contact with specified login and send to user results
     * @param message
     * @throws Exception
     */
    private void searchContact(SearchContact message) throws Exception {
        // TODO: implement more versatile search
        final DBUser result = DBUser.getByLogin(message.searchQuery);
        if (result == null) {
            final ContactSearchFailed response =
                    new ContactSearchFailed(message.searchQuery, "No user with specified login");
            sendProtoMessage(response);
            return;
        }
        else  {
            final List<Long> ids = new ArrayList<>(1);
            ids.add(result.getId());
            final List<String> logins = new ArrayList<>(1);
            logins.add(result.getLogin());
            final List<String> fnames = new ArrayList<>(1);
            fnames.add(result.getFirstName());
            final List<String> lnames = new ArrayList<>(1);
            lnames.add(result.getLastName());

            final ContactSearchResult response =
                    new ContactSearchResult(message.searchQuery, ids, logins, fnames, lnames);
            sendProtoMessage(response);
            return;
        }
    }

    /**
     * Send message to all conversation participants.
     * @param message
     * @throws Exception
     */
    private void sendChatMessage(SendChatMessage message) throws Exception {
        //--------- download conversation and participants. If any error occurs, send failure response to user
        final DBConversation conversation;
        final Set<DBUser> participants;
        try {
            conversation = DBConversation.getById(message.conversationId);
            participants = conversation.getParticipants();
            if (participants == null)
                throw new Exception();
        } catch (Exception e) {
            final ChatMessageSendFailed response = new ChatMessageSendFailed(message.conversationId, message.messageId);
            sendProtoMessage(response);
            return;
        }
        //--------notify dispatcher about message shipment
        final ChatMessageSent response = new ChatMessageSent(message.conversationId, message.messageId);
        sendProtoMessage(response);

        //-------- send message for all participants(except dispatcher)
        final InputChatMessage chatMessage =
                new InputChatMessage(message.conversationId, authorizedUser.getLogin(), message.text);
        for (DBUser user : participants) {
            if (user.getId().equals(authorizedUser.getId()))
                continue;
            if (user.isOnline()) {
                try {
                    user.getServant().sendProtoMessage(chatMessage);
                } catch (Exception e) {
                    final DBMessage undeliveredMessage = new DBMessage(authorizedUser, user, conversation, message.text);
                    user.getIncomeMessages().add(undeliveredMessage);
                    user.saveOrUpdateInDB();
                }
            }
        }
    }

    public synchronized void sendProtoMessage(ServerToClientProtoMessage mes) throws Exception {
        System.out.println("Send " + ((authorizedUser == null) ? "" : "to " + authorizedUser.getLogin() + " message: ") + mes
                .toXML());
        os.write(mes.toXML());
        os.write("\n\0\n");
        os.flush();
    }

    public boolean isAuthorized() {
        return authorizedUser != null && socket.isConnected();
    }
}
