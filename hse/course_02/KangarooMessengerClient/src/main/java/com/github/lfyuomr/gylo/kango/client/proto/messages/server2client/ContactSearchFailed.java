package com.github.lfyuomr.gylo.kango.client.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("WeakerAccess")
@XmlRootElement(name = "CONTACT_SEARCH_FAILED")
public class ContactSearchFailed extends ServerToClientProtoMessage {
    @XmlElement
    public String searchQuery;

    @XmlElement
    public String explanation;

    public ContactSearchFailed() {
    }

    public ContactSearchFailed(String searchQuery, String explanation) {
        this.searchQuery = searchQuery;
        this.explanation = explanation;
    }
}
