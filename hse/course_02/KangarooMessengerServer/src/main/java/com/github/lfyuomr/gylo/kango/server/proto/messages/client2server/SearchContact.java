package com.github.lfyuomr.gylo.kango.server.proto.messages.client2server;


import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.ContactSearchFailed;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.ContactSearchResult;
import com.github.lfyuomr.gylo.kango.server.proto.messages.server2client.ServerToClientProtoMessage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.function.Predicate;

@XmlRootElement(name = "SEARCH_CONTACT")
public class SearchContact extends ClientToServerProtoMessage implements ClientQuestioner {
    @XmlElement
    public String searchQuery;

    public SearchContact() {
    }

    public SearchContact(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public Predicate<ServerToClientProtoMessage> getResponseRecognizer() {
        return mes -> {
            if (mes instanceof ContactSearchResult) {
                return searchQuery.equals(((ContactSearchResult) mes).searchQuery);
            }
            else if (mes instanceof ContactSearchFailed) {
                return searchQuery.equals(((ContactSearchFailed) mes).searchQuery);
            }
            return false;
        };
    }
}
