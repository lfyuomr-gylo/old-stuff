package com.github.lfyuomr.gylo.kango.client.view;

import com.github.lfyuomr.gylo.kango.client.KangoApp;
import com.github.lfyuomr.gylo.kango.client.model.Contact;
import com.github.lfyuomr.gylo.kango.client.util.Alerts;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

@SuppressWarnings("WeakerAccess")
public class SearchContactPopupController {
    @FXML
    TextField textField;
    @FXML
    ListView<Contact> foundContacts;

    KangoApp.MainWindowManager mainWindowManager;

    @FXML
    private void initialize() {
        foundContacts.setCellFactory(
                contact -> new ListCell<Contact>() {
                    @Override
                    public void updateItem(Contact contact, boolean empty) {
                        super.updateItem(contact, empty);

                        if (empty || contact == null) {
                            setGraphic(null);
                            setText(null);
                        }
                        else {
                            final Text text = new Text(contact.toString());
                            text.setWrappingWidth(foundContacts.getPrefWidth() - 40);
                            graphicProperty().setValue(text);
                        }
                    }
                }
        );
    }

    @FXML
    private void handleSearch() {
        final String query = textField.getText();
        if (query.isEmpty()) {
            Alerts.showErrorAlert(
                    "No Input",
                    "Search Query Is Empty",
                    "Please, type something to search contacts"
            );
            return;
        }

        mainWindowManager.handleSearchContacts(query);
    }

    @FXML
    private void handleAddToContacts() {
        final ObservableList<Contact> selected = foundContacts.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            Alerts.showErrorAlert(
                    "No Selection",
                    "No Contact Selected",
                    "Please, select contacts to add them to contact list"
            );
            return;
        }

        mainWindowManager.handleAddContacts(selected);
    }

    public void
    setMainWindowManager(KangoApp.MainWindowManager mainWindowManager) {
        this.mainWindowManager = mainWindowManager;
    }

    public void
    setFoundContactsItems(ObservableList<Contact> contacts) {
        for (Contact contact : contacts) {
            System.out.println("Setting contacts in contact popup");
            System.out.println(contact);
        }
        foundContacts.setItems(contacts);
    }
}
