package com.github.lfyuomr.gylo.kango.server.util;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;


public class JaxbParser {

    public static String
    getRootElementName(String buff) throws Exception {
        final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        InputSource source = new InputSource(new StringReader(buff));
        Document document = builderFactory.newDocumentBuilder().parse(source);
        return document.getDocumentElement().getTagName();

    }

    public static Object
    getObject(File file, @NotNull Class c) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(c);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(file);
    }

    public static Object
    getObject(String buff, @NotNull Class c) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(c);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(new StringReader(buff));
    }

    public static void
    saveObject(File file, @NotNull Object o) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(o.getClass());
        final Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(o, file);
    }

    public static void
    saveObject(StringWriter os, @NotNull Object o) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(o.getClass());
        final Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(o, os);
    }

}
