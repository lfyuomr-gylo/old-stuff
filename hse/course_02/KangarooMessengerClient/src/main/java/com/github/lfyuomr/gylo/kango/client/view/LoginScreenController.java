package com.github.lfyuomr.gylo.kango.client.view;

import com.github.lfyuomr.gylo.kango.client.KangoApp;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Login screen interface class.
 */
public class LoginScreenController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    private KangoApp.AuthorizationManager authorizationManager;

    @FXML
    private void handleSignInButtonAction() {
        System.out.println("'Sign in' button clicked");
        authorizationManager.handleSignIn(loginField.getText(), passwordField.getText());
    }

    @FXML
    private void handleSignUpButtonAction() {
        System.out.println("'Sign up' button clicked, 'handleSignUpButtonAction' method " +
                "in 'LoginScreenController'");
        try {
            authorizationManager.showRegistrationScreen();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthorizationManager(KangoApp.AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }
}
