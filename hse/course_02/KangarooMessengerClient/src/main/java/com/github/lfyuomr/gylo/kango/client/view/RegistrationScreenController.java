package com.github.lfyuomr.gylo.kango.client.view;

import com.github.lfyuomr.gylo.kango.client.KangoApp;
import com.github.lfyuomr.gylo.kango.client.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

import static com.github.lfyuomr.gylo.kango.client.util.DataLimitations.*;

/**
 * Simple registration screen interface
 */
public class RegistrationScreenController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmationPasswordField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;

    private KangoApp.AuthorizationManager authorizationManager;

    @FXML
    private void handleSignUpButtonAction() {
        System.out.println("'Sign up' button clicked, handleSignUpButtonAction method, " +
                "in RegistrationScreenController");

        if (!passwordField.getText().equals(confirmationPasswordField.getText())) {
            Alerts.showErrorAlert(
                    "Incorrect Input",
                    "Passwords Will Be Equal",
                    "Your password and password confirmation are not equal"
            );
        }
        else if (!LOGIN.isAppropriate(loginField.getText())) {
            Alerts.showErrorAlert(
                    "Incorrect Input",
                    "Login Format Is Invalid",
                    LOGIN.getDescription()
            );
        }
        else if (!PASSWORD.isAppropriate(passwordField.getText())) {
            Alerts.showErrorAlert(
                    "Incorrect Input",
                    "Password Format Is Invalid",
                    PASSWORD.getDescription()
            );
        }
        else if (!NAME.isAppropriate(firstNameField.getText())) {
            Alerts.showErrorAlert(
                    "Incorrect Input",
                    "First Name Format Is Invalid",
                    NAME.getDescription()
            );
        }
        else if (!NAME.isAppropriate(lastNameField.getText())) {
            Alerts.showErrorAlert(
                    "Incorrect Input",
                    "Last Name Format Is Invalid",
                    NAME.getDescription()
            );
        }
        else {
            authorizationManager.handleSignUp(
                    loginField.getText(),
                    passwordField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText()
            );
        }
    }

    @FXML
    private void handleCancelButtonAction() {
        try {
            authorizationManager.showLoginScreen();
        }
        catch (IOException e) {
            authorizationManager.close();
            e.printStackTrace();
        }
    }

    public void setAuthorizationManager(KangoApp.AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }
}
