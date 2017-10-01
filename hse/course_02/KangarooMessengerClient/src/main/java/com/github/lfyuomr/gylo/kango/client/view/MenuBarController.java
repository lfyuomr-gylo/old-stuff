package com.github.lfyuomr.gylo.kango.client.view;

import com.github.lfyuomr.gylo.kango.client.KangoApp;
import javafx.fxml.FXML;

public class MenuBarController {
    private KangoApp.MainWindowManager mainWindowManager;

    @FXML
    private void handleNew() {
        handleView();
    }

    @FXML
    private void handleShowMembers() {
//        mainWindowManager.
    }

    @FXML
    private void handleView() {
        System.out.println("'View' button clicked");
        mainWindowManager.handleMenuButtonViewContacts();
    }

    @FXML
    private void handleFind() {
        System.out.println("'Find' button clicked");
        mainWindowManager.handleMenuButtonSearchContacts();
    }

    @FXML
    private void handleAbout() {
        System.out.println("'About button clicked'");
    }

    public void setMainWindowManager(KangoApp.MainWindowManager mainWindowManager) {
        this.mainWindowManager = mainWindowManager;
    }
}
