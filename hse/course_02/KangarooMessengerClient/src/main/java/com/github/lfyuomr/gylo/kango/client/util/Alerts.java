package com.github.lfyuomr.gylo.kango.client.util;

import com.github.lfyuomr.gylo.kango.client.proto.exceptions.*;
import javafx.scene.control.Alert;

public class Alerts {
    public static void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static void showErrorAlert(KangoException e) {
        if (e instanceof ConnectionKangoException)
            showConnectionErrorAlert((ConnectionKangoException) e);
        else if (e instanceof InvalidLoginPasswordKangoException)
            showInvalidLoginPasswordErrorAlert((InvalidLoginPasswordKangoException) e);
        else if (e instanceof OfflineUserInConversationCreationKangoException)
            showOfflineUserInConversationErrorAlert((OfflineUserInConversationCreationKangoException) e);
        else if (e instanceof ServerSideKangoException)
            showServerSideErrorAlert((ServerSideKangoException) e);
        else if (e instanceof UnavailableLoginKangoException)
            showUnavailableLoginErrorAlert((UnavailableLoginKangoException) e);
        else
            System.out.println("Some shit just happened");
    }

    private static void showConnectionErrorAlert(ConnectionKangoException e) {
        showErrorAlert(
                "Connection error",
                "Can Not Connect to Server",
                e.getMessage()
        );
    }

    private static void showInvalidLoginPasswordErrorAlert(InvalidLoginPasswordKangoException e) {
        showErrorAlert(
                "Authorization error",
                "Invalid login/password pair",
                e.getMessage()
        );
    }

    private static void showOfflineUserInConversationErrorAlert(OfflineUserInConversationCreationKangoException e) {
        showErrorAlert(
                "Conversation Creation Error",
                "Can Not Create Conversation",
                e.getMessage()
        );
    }

    private static void showServerSideErrorAlert(ServerSideKangoException e) {
        showErrorAlert(
                "Server error",
                "Server error",
                e.getMessage()
        );
    }

    private static void showUnavailableLoginErrorAlert(UnavailableLoginKangoException e) {
        showErrorAlert(
                "Unavailable Login",
                "Specified Login Is Unavailable",
                e.getMessage()
        );
    }
}
