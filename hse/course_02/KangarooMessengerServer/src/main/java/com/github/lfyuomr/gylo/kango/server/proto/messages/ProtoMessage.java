package com.github.lfyuomr.gylo.kango.server.proto.messages;


import com.github.lfyuomr.gylo.kango.server.util.JaxbParser;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;

public abstract class ProtoMessage {
    public String
    toXML() throws JAXBException {
        final StringWriter sw = new StringWriter();
        JaxbParser.saveObject(sw, this);
        return sw.toString();
    }

    public static <T extends ProtoMessage> T
    fromXML(String xml) throws Exception {
        Class type = MessageAlias.valueOf(JaxbParser.getRootElementName(xml)).getType();
        return (T) JaxbParser.getObject(xml, type);
    }

}
