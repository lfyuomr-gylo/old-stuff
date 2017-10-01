package com.github.lfyuomr.gylo.kango.client.test;

import com.github.lfyuomr.gylo.kango.client.model.AuthorizedAccount;
import com.github.lfyuomr.gylo.kango.client.model.Contact;
import com.github.lfyuomr.gylo.kango.client.model.Conversation;
import com.github.lfyuomr.gylo.kango.client.model.Message;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class AuthorizedAccountGenerator {
    private static Random randomGenerator = new Random();
    public static class ContactGenerator {
        static Contact getRandomContact() {
            return new Contact(
                    randomGenerator.nextLong(),
                    Login.getRandomLogin().toString(),
                    ConversationGenerator.getRandomText(15),
                    ConversationGenerator.getRandomText(15)
            );
        }

        static ObservableList<Contact> getRandomContactList() {
            ObservableList<Contact> result = FXCollections.observableArrayList();
            for (int i = randomGenerator.nextInt(30); i > -1; i--)
                result.add(getRandomContact());

            return result;
        }
    }

    public static class ConversationGenerator {
        static String getRandomText(int max_size) {
            final int size = randomGenerator.nextInt(max_size);
            final StringBuilder result = new StringBuilder();
            randomGenerator.ints('a', 'z').limit(size).mapToObj(num -> (char) num).forEach(result::append);
            return result.toString() + "\ngobbles";
        }

        public static Conversation getRandomConversation() {
            final String title = getRandomText(20);
            final ObservableList<Message> messages = FXCollections.observableArrayList();
            final ObservableList<Contact> contacts = ContactGenerator.getRandomContactList();
            for (int i = 0; i < 10; i++)
                messages.add(new Message(Login.getRandomLogin().toString(), getRandomText(200)));

            return new Conversation(randomGenerator.nextLong(), title, messages, contacts);
        }

        public static ObservableList<Conversation> getRandomConversationList() {
            final ObservableList<Conversation> result = FXCollections.observableArrayList();
            for (int i = randomGenerator.nextInt(15); i > -1; i--)
                result.add(getRandomConversation());
            return result;
        }
    }

    public enum Login {
        GYLO("gylo"),
        ANYKUZYA("anykuzya"),
        ZERO("Zero"),
        KOSMACH("Kosmach"),
        TIMMI("Timmy"),
        JIMMY("Jimmy"),
        GOBBLES("Gobbles");

        Login(String login) {
            this.login = new SimpleStringProperty(login);
        }

        static Login getRandomLogin() {
            return Login.values()[randomGenerator.nextInt(7)];
        }

        public String toString() {
            return login.getValue();
        }

        private StringProperty login;
    }

    public static AuthorizedAccount getRandomAuthorizedAccount() {
        return new AuthorizedAccount(
                randomGenerator.nextLong(),
                Login.getRandomLogin().toString(),
                ConversationGenerator.getRandomText(15),
                ConversationGenerator.getRandomText(15),
                ConversationGenerator.getRandomConversationList(),
                ContactGenerator.getRandomContactList()
        );
    }
}
