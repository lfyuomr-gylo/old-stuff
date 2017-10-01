package com.github.lfyuomr.gylo.kango.client.proto;

import com.github.lfyuomr.gylo.kango.client.model.Contact;
import com.github.lfyuomr.gylo.kango.client.model.Conversation;
import com.github.lfyuomr.gylo.kango.client.model.Message;
import com.github.lfyuomr.gylo.kango.client.proto.exceptions.KangoException;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.AuthorizationSucceeded;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.RegistrationSucceeded;
import com.github.lfyuomr.gylo.kango.client.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KangoProto {
    private ConnectionHolder connectionHolder;

    public KangoProto(@NotNull BiConsumer<Message, Long> onInputMessage,
                      @NotNull Consumer<Conversation> onCreatedConversation,
                      @NotNull Consumer<KangoException> connectionExceptionHandler,
            @NotNull Runnable onConversationCreationFailed) {
        connectionHolder = new ConnectionHolder(onInputMessage, onCreatedConversation,
                connectionExceptionHandler, onConversationCreationFailed);
    }

    public void
    saveSecretKeys() {
        connectionHolder.saveSecretKeys();
    }

    /**
     * Try to authorize. If succeeded, {@code onSucceeded} will be called with
     * received authorization message passed as an argument. Otherwise, {@code onFailed} will
     * be called with exception occurred passed as an argument.
     * @param login
     * @param password
     * @param onSucceeded authorization success handler
     * @param onFailed authorization fail handler
     */
    public void
    authorize(@NotNull String login,
              @NotNull String password,
              @NotNull Consumer<AuthorizationSucceeded> onSucceeded,
              @NotNull Consumer<KangoException> onFailed) {
        connectionHolder.authorize(
                login,
                password,
                onSucceeded,
                onFailed
        );
    }

    /**
     * Try to register new user. If succeeded, {@code onSucceeded} will be calles with received
     * registration message passed as an argument. Otherwise, {@code onFailed} will be called
     * with exception occurred passed as an argument.
     * @param login
     * @param password
     * @param onSucceeded registration success handler
     * @param onFailed registration fail handler
     */
    public void
    register(@NotNull String login,
             @NotNull String password,
             @NotNull String firstName,
             @NotNull String lastName,
             @NotNull Consumer<RegistrationSucceeded> onSucceeded,
             @NotNull Consumer<KangoException> onFailed) {
        connectionHolder.register(
                login,
                password,
                firstName,
                lastName,
                onSucceeded,
                onFailed
        );
    }

    /**
     * Try to send message {@code text} to conversation {@code conv}. If succeeded,
     * {@code onSucceeded} will be called with sent message and suitable conversation
     * passed as an argument. Otherwise, {@code onFailed} will be called with exception
     * occurred passed as an argument.
     * @param conv
     * @param text
     * @param onSucceeded send success handler
     * @param onFailed send fail handler
     */
    public void
    sendMessage(@NotNull Conversation conv,
                @NotNull String text,
                @NotNull Runnable onSucceeded,
                @NotNull Consumer<KangoException> onFailed) {
        connectionHolder.sendChatMessage(conv, text, onSucceeded, onFailed);
    }

    /**
     * Try to search contacts in server data base. If any contact found, call {@code onSucceeded}
     * with results passed as an argument. Otherwise, call {@code onFailed} with occurred exception
     * passed as an argument.
     * @param query
     * @param onSucceeded
     * @param onFailed
     */
    public void
    searchContact(@NotNull String query,
                  @NotNull Consumer<ObservableList<Contact>> onSucceeded,
                  @NotNull Consumer<KangoException> onFailed) {
        connectionHolder.searchContact(
                query,
                response -> {
                    ObservableList<Contact> result = FXCollections.observableArrayList();
                    for (int i = 0; i < response.ids.size(); i++) {
                        result.add(new Contact(
                                response.ids.get(i),
                                response.logins.get(i),
                                response.firstNames.get(i),
                                response.lastNames.get(i)
                        ));
                    }
                    onSucceeded.accept(result);
                },
                e -> Alerts.showErrorAlert(
                        "No search results",
                        "Can not find any user for your query",
                        "Try again with another query"
                )
        );
    }

    /**
     * Try to create conversation. If succeeded, call {@code onSucceeded}
     * with created conversation passed as an argument. Otherwise, call {@code onFailed} with occurred exception
     * passed as an argument.
     * @param title
     * @param participants
     */
    public void
    createConversation(@NotNull String title,
            @NotNull List<Contact> participants) {
        connectionHolder.createConversation(title, participants);
    }

}
