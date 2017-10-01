package com.github.lfyuomr.gylo.kango.client;

import com.github.lfyuomr.gylo.kango.client.model.AuthorizedAccount;
import com.github.lfyuomr.gylo.kango.client.model.Contact;
import com.github.lfyuomr.gylo.kango.client.model.Conversation;
import com.github.lfyuomr.gylo.kango.client.model.Message;
import com.github.lfyuomr.gylo.kango.client.proto.KangoProto;
import com.github.lfyuomr.gylo.kango.client.proto.exceptions.KangoException;
import com.github.lfyuomr.gylo.kango.client.proto.exceptions.OfflineUserInConversationCreationKangoException;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.AuthorizationSucceeded;
import com.github.lfyuomr.gylo.kango.client.proto.messages.server2client.RegistrationSucceeded;
import com.github.lfyuomr.gylo.kango.client.util.Alerts;
import com.github.lfyuomr.gylo.kango.client.util.Config;
import com.github.lfyuomr.gylo.kango.client.util.JaxbParser;
import com.github.lfyuomr.gylo.kango.client.view.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main app
 */
public class KangoApp extends Application {
    private final AuthorizationManager authorizationManager = new AuthorizationManager();
    private final MainWindowManager mainWindowManager = new MainWindowManager();
    private Stage primaryStage;
    private BorderPane rootLayout;
    private AuthorizedAccount authorizedAccount;
    private final KangoProto proto = new KangoProto(
            this::receiveMessage,
            this::receiveConversation,
            authorizationManager::handleConnectionException,
            () -> Alerts.showErrorAlert(new OfflineUserInConversationCreationKangoException())
    );

    public static void
    main(String[] args) {
        launch(args);
    }

    /**
     * Initialize rootLayout and run authorization
     */
    public void
    start(Stage primaryStage_) {
        primaryStage = primaryStage_;
        try {
            initRootLayout();
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        authorizationManager.run();
    }

    /**
     * Save to preferences authorized account XML-backup file name and back up authorized account.
     * @throws Exception
     */
    public void
    stop() throws Exception {
        super.stop();
        if (authorizedAccount != null) {
            authorizedAccount.saveHistory();
        }
        proto.saveSecretKeys();
    }

    /**
     * Instantiate rootLayout if it's not initialized. Do nothing otherwise
     */
    private void
    initRootLayout() throws IOException {
        if (rootLayout != null)
            return;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/RootLayout.fxml"));
        rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
    }

    private void
    receiveMessage(Message message, Long conversationId) {
        if (authorizedAccount == null) {
            System.out.println("Received message when have not been authorized");
            return;
        }
        ObservableList<Conversation> conversations = authorizedAccount.getConversations();
        for (Conversation conversation : conversations) {
            if (conversation.getId() == conversationId) {
                conversation.getMessages().add(message);
                break;
            }
        }
    }

    private void
    receiveConversation(Conversation conversation) {
        if (authorizedAccount != null)
            authorizedAccount.getConversations().add(conversation);
        else
            System.out.println("Conversation created when was not authorized");
    }

    public class AuthorizationManager {
        AnchorPane loginPane;
        LoginScreenController loginScreenController;
        AnchorPane registrationPane;
        RegistrationScreenController registrationScreenController;

        /**
         * Try to authenticate. If authenticated, set {@link KangoApp#authorizedAccount} and run MainMenu
         * otherwise, show loginScreen.
         * If any exception occurs, print stack trace and close app.
         */
        private void
        run() {
            try {
                showLoginScreen();
            }
            catch (IOException e) {
                e.printStackTrace();
                primaryStage.close();
            }
        }

        /**
         * Load and show Login Screen and initialize {@link this#loginPane}
         * and {@link this#loginScreenController}.
         *
         * @throws IOException from {@link FXMLLoader#load()}
         */
        public void
        showLoginScreen() throws IOException {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/LoginScreen.fxml"));
            loginPane = loader.load();
            loginScreenController = loader.getController();
            loginScreenController.setAuthorizationManager(authorizationManager);

            rootLayout.setCenter(loginPane);

            primaryStage.show();
        }

        private void
        initAuthorizedAccount(AuthorizationSucceeded message) {
            if (authorizedAccount != null)
                authorizedAccount.saveHistory();
            proto.saveSecretKeys();

            try {
                File historyFile = Config.getHistoryFile(message.id);
                authorizedAccount = (AuthorizedAccount) JaxbParser.getObject(historyFile, AuthorizedAccount.class);
            } catch (Exception e) {
                authorizedAccount = new AuthorizedAccount(
                        message.id,
                        message.login,
                        message.firstName,
                        message.lastName,
                        FXCollections.observableArrayList(),
                        FXCollections.observableArrayList()
                );
            }

            mainWindowManager.run();
        }

        public void
        handleSignIn(@NotNull String login, @NotNull String password) {
            proto.authorize(
                    login,
                    password,
                    this::initAuthorizedAccount,
                    Alerts::showErrorAlert
            );
        }

        private void
        initRegisteredAccount(RegistrationSucceeded message) {
            if (authorizedAccount != null)
                authorizedAccount.saveHistory();
            proto.saveSecretKeys();

            authorizedAccount = new AuthorizedAccount(
                    message.id,
                    message.login,
                    message.firstName,
                    message.lastName,
                    FXCollections.observableArrayList(),
                    FXCollections.observableArrayList()
            );

            mainWindowManager.run();
        }

        public void
        handleSignUp(@NotNull String login,
                     @NotNull String password,
                     @NotNull String firstName,
                     @NotNull String lastName) {
            proto.register(
                    login,
                    password,
                    firstName,
                    lastName,
                    this::initRegisteredAccount,
                    Alerts::showErrorAlert
            );
        }

        /**
         * Load and show Registration Screen and initialize {@link this#registrationPane}
         * and {@link this#registrationScreenController}
         * @throws IOException from {@link FXMLLoader#load()}
         */
        public void
        showRegistrationScreen() throws IOException {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/RegistrationScreen.fxml"));
            registrationPane = loader.load();
            registrationScreenController = loader.getController();
            registrationScreenController.setAuthorizationManager(authorizationManager);

            rootLayout.setCenter(registrationPane);

            primaryStage.show();
        }

        void
        handleConnectionException(KangoException e) {
            if (authorizedAccount != null)
                authorizedAccount.saveHistory();
            authorizedAccount = null;

            try {
                Alerts.showErrorAlert(e);
                showLoginScreen();
            } catch (IOException e1) {
                e1.printStackTrace();
                close();
            }
        }

        public void
        close() {
            if (authorizedAccount != null) {
                authorizedAccount.saveHistory();
            }
            proto.saveSecretKeys();
            primaryStage.close();
        }
    }

    public class MainWindowManager {
        private AnchorPane mainScreenPane;
        private MainScreenController mainScreenController;
        private MenuBar menuBarPane;
        private MenuBarController menuBarController;
        private Popup contactListPopup;
        private AnchorPane contactListPane;
        private ContactListPopupController contactListPopupController;
        private Popup searchContactPopup;
        private AnchorPane searchContactPane;
        private SearchContactPopupController searchContactPopupController;

        /**
         * Simply create and show main window
         */
        private void
        run() {
            try {
                showMenuBar();
                showMainScreen();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }

            primaryStage.show();
        }

        /**
         * Load MenuBar and initialize {@link this#menuBarPane} and {@link this#menuBarController}.
         * @throws IOException when can not load 'view/MenuBar.fxml'
         */
        private void
        showMenuBar() throws IOException {
            if (menuBarPane == null || menuBarController == null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(KangoApp.class.getResource("/view/MenuBar.fxml"));
                menuBarPane = loader.load();
                menuBarController = loader.getController();
                menuBarController.setMainWindowManager(this);
            }

            rootLayout.setTop(menuBarPane);
        }

        /**
         * Load MainScreen and initialize {@link this#mainScreenPane} and
         * {@link this#mainScreenController}, assigning its conversation list to
         * {@link KangoApp#authorizedAccount}'s conversation list.
         * @throws IOException when can not load 'view/MainScreen.fxml'
         */
        private void
        showMainScreen() throws IOException {
            if (mainScreenPane == null || mainScreenController == null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(KangoApp.class.getResource("/view/MainScreen.fxml"));
                mainScreenPane = loader.load();
                mainScreenController = loader.getController();
                mainScreenController.setMainWindowManager(this);

                mainScreenController.setConversations(authorizedAccount.getConversations());
            }

            rootLayout.setCenter(mainScreenPane);
        }

        /**
         * Try to delete contacts from contact list. If any error occurs, show alert with description.
         *
         * @param selectedContacts
         */
        public void
        handleDeleteContacts(@NotNull ObservableList<Contact> selectedContacts) {
            authorizedAccount.getContacts().removeAll(selectedContacts);
        }

        public void
        handleCreateConversation(@NotNull String title, @NotNull ObservableList<Contact> selectedContacts) {
            final Contact me = new Contact(
                    authorizedAccount.getId(),
                    authorizedAccount.getLogin(),
                    authorizedAccount.getFirstName(),
                    authorizedAccount.getLastName()
            );
            List<Contact> participants = new ArrayList<>(selectedContacts.size() + 1);
            if (!selectedContacts.contains(me)) {
                selectedContacts.forEach(participants::add);
                participants.add(me);
                proto.createConversation(title, participants);
            }
            else {
                proto.createConversation(title, participants);
            }
        }

        public void
        handleMenuButtonViewContacts() {
            try {
                showContactListPopup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void
        showContactListPopup() throws IOException {
            if (contactListPopup == null || contactListPane == null ||
                    contactListPopupController == null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(KangoApp.class.getResource("/view/ContactListPopup.fxml"));
                contactListPane = loader.load();
                contactListPopupController = loader.getController();
                contactListPopupController.setMainWindowManager(this);
                contactListPopupController.setContactListItems(authorizedAccount.getContacts());

                contactListPopup = new Popup();
                contactListPopup.setAutoHide(true);
                contactListPopup.getContent().addAll(contactListPane);
            }
            contactListPopup.show(primaryStage);
        }

        public void
        handleMenuButtonSearchContacts() {
            try {
                showSearchContactPopup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void
        showSearchContactPopup() throws IOException {
            if (searchContactPopup == null || searchContactPane == null ||
                    searchContactPopupController == null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(KangoApp.class.getResource("/view/SearchContactPopup.fxml"));
                searchContactPane = loader.load();
                searchContactPopupController = loader.getController();
                searchContactPopupController.setMainWindowManager(this);

                searchContactPopup = new Popup();
                searchContactPopup.setAutoHide(true);
                searchContactPopup.getContent().addAll(searchContactPane);
            }

            searchContactPopup.show(primaryStage);
        }

        public void
        handleSendMessage(@NotNull Conversation conversation,
                          @NotNull String text,
                          @NotNull Runnable onSucceed) {
            proto.sendMessage(
                    conversation,
                    text,
                    () -> {
                        conversation.getMessages().add(new Message(authorizedAccount.getLogin(), text));
                        onSucceed.run();
                    },
                    Alerts::showErrorAlert
            );
        }

        public void
        handleSearchContacts(@NotNull String searchQuery) {
            proto.searchContact(
                    searchQuery,
                    (res) -> searchContactPopupController.setFoundContactsItems(res),
                    Alerts::showErrorAlert
            );
        }

        public void
        handleAddContacts(@NotNull ObservableList<Contact> contacts) {
            authorizedAccount.getContacts().addAll(contacts);
        }
    }
}
