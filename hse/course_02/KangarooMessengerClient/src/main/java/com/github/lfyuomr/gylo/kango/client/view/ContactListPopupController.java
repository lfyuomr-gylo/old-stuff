package com.github.lfyuomr.gylo.kango.client.view;

import com.github.lfyuomr.gylo.kango.client.KangoApp;
import com.github.lfyuomr.gylo.kango.client.model.Contact;
import com.github.lfyuomr.gylo.kango.client.util.Alerts;
import com.github.lfyuomr.gylo.kango.client.util.DataLimitations;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ContactListPopupController {
    private KangoApp.MainWindowManager mainWindowManager;

    @FXML
    private ListView<Contact> contactListView;

    @FXML
    private TextField titleField;

    @FXML
    private void handleDelete() {
        ObservableList<Contact> selectedContacts = contactListView.getSelectionModel().getSelectedItems();
        if (selectedContacts.size() <= 0) {
            Alerts.showErrorAlert(
                    "No Selection",
                    "No Contact Selected",
                    "Please, select contact to delete."
            );
        }
        else {
            mainWindowManager.handleDeleteContacts(selectedContacts);
        }
    }

    @FXML
    private void handleCreateConversation() {
        System.out.println("'Create conversation' button clicked");

        final String title = titleField.getText();
        if (!DataLimitations.CONVERSATION_TITLE.isAppropriate(title)) {
            Alerts.showErrorAlert(
                    "Invalid Title",
                    "Invalid Conversation Title",
                    DataLimitations.CONVERSATION_TITLE.getDescription()
            );
            return;
        }

        final ObservableList<Contact> selectedContacts = contactListView.getSelectionModel().getSelectedItems();
        if (selectedContacts.isEmpty()) {
            Alerts.showErrorAlert(
                    "No Selection",
                    "No Contacts selected",
                    "Can not create conversation with no members."
            );
            return;
        }

        mainWindowManager.handleCreateConversation(
                title,
                contactListView.getSelectionModel().getSelectedItems()
        );
    }

    @FXML
    private void initialize() {
        contactListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        contactListView.setCellFactory(contact -> new ListCell<Contact>() {
                @Override
                public void updateItem(Contact contact, boolean empty) {
                    super.updateItem(contact, empty);

                    if (empty || contact == null) {
                        setGraphic(null);
                        setText(null);
                    }
                    else {
                        final Text text = new Text(contact.toString());
                        text.setWrappingWidth(contactListView.getPrefWidth() - 40);
                        graphicProperty().setValue(text);
                    }
                }
            }
        );
    }

    public void setMainWindowManager(KangoApp.MainWindowManager mainWindowManager) {
        this.mainWindowManager = mainWindowManager;
    }

    public void setContactListItems(ObservableList<Contact> contacts) {
        contactListView.setItems(contacts);
    }
}
