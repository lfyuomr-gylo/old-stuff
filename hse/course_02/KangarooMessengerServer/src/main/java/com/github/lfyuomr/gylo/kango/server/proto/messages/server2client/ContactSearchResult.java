package com.github.lfyuomr.gylo.kango.server.proto.messages.server2client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "CONTACT_SEARCH_RESULT")
public class ContactSearchResult extends ServerToClientProtoMessage {
    @XmlElement
    public String searchQuery;

    @XmlElement
    public List<Long> ids;

    @XmlElement
    public List<String> logins;

    @XmlElement
    public List<String> firstNames;

    @XmlElement
    public List<String> lastNames;

    public ContactSearchResult() {
    }

    public ContactSearchResult(String searchQuery,
                               List<Long> ids,
                               List<String> logins,
                               List<String> firstNames,
                               List<String> lastNames) {
        this.searchQuery = searchQuery;
        this.ids = ids;
        this.logins = logins;
        this.firstNames = firstNames;
        this.lastNames = lastNames;
    }
}
