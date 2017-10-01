package com.github.lfyuomr.gylo.kango.client.view;

import com.github.lfyuomr.gylo.kango.client.KangoApp;
import com.github.lfyuomr.gylo.kango.client.model.Conversation;
import com.github.lfyuomr.gylo.kango.client.model.Message;
import com.github.lfyuomr.gylo.kango.client.util.Alerts;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

/**
 * Main window simple manipulation interface
 */
public class MainScreenController {
    @FXML
    private TableView<Conversation> conversationTable;
    @FXML
    private TableColumn<Conversation, String> conversationTableColumn;
    @FXML
    private ListView<Message> messageListView;
    @FXML
    private TextField messageTextField;

    private KangoApp.MainWindowManager mainWindowManager;

    @FXML
    private void
    initialize() {
        conversationTableColumn.setCellValueFactory(conv -> conv.getValue().titleProperty());

        conversationTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> messageListView.setItems(newValue.getMessages())
        );
        messageListView.setCellFactory(list -> new ListCell<Message>() {
            @Override
            public void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                }
                else {
                    Text text = new Text(item.toString());
                    text.setWrappingWidth(messageListView.getPrefWidth() - 40);
                    setGraphic(text);
                }
            }
        });
    }

    @FXML
    private void
    handleSend() {
        final Conversation conversation = conversationTable.getSelectionModel().getSelectedItem();
        if (conversation == null) {
            Alerts.showErrorAlert(
                    "No Selection",
                    "No Conversation Selected",
                    "Please, select conversation to send message"
            );
            return;
        }
        final String text = messageTextField.getText();
        if (text.isEmpty()) {
            Alerts.showErrorAlert(
                    "No Input",
                    "Empty Message Text",
                    "Please, type something to send message"
            );
            return;
        }

        mainWindowManager.handleSendMessage(conversation, text, messageTextField::clear);
    }

    public void
    setMainWindowManager(KangoApp.MainWindowManager mainWindowManager) {
        this.mainWindowManager = mainWindowManager;
    }

    public void
    setConversations(ObservableList<Conversation> conversations) {
        conversationTable.setItems(conversations);
    }

}
