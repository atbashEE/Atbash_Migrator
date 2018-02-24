/*
 * Copyright 2018 Rudy De Busscher (https://www.atbash.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.migrator;

import be.atbash.util.exception.AtbashUnexpectedException;

import javax.xml.stream.*;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 *
 */

public class FaceletsMigrator {

    private Map<String, String> namespaceMapping;

    public FaceletsMigrator() {
        defineMapping();
    }

    private void defineMapping() {
        namespaceMapping = new HashMap<>();
        namespaceMapping.put("http://www.c4j.be/secure", "http://www.atbash.be/secure/octopus");
        namespaceMapping.put("http://www.rubus.be/web/validation/valerie", "http://www.atbash.be/ee/validation/valerie");
    }

    public void transform(Path file) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream in = Files.newInputStream(file)) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLOutputFactory xof = XMLOutputFactory.newInstance();
            XMLEventFactory ef = XMLEventFactory.newInstance();

            XMLEventReader reader = factory.createXMLEventReader(file.toString(), in);
            XMLEventWriter writer = xof.createXMLEventWriter(out);

            while (reader.hasNext()) {
                XMLEvent event = (XMLEvent) reader.next();
                if (event.isStartElement()) {
                    handleTagElement(ef, writer, event);
                } else {
                    writer.add(event);
                }
            }
            writer.flush();
        } catch (Exception e) {
            throw new AtbashUnexpectedException(e);
        }

        try {
            Files.write(file, out.toByteArray());
        } catch (IOException e) {
            throw new AtbashUnexpectedException(e);
        }
    }

    private void handleTagElement(XMLEventFactory ef, XMLEventWriter writer, XMLEvent event) throws XMLStreamException {
        StartElement s = event.asStartElement();

        boolean changeNamespaces = false;
        List<Object> newNamespaces = new ArrayList<>();
        Iterator<Namespace> iterator = s.getNamespaces();
        while (iterator.hasNext()) {
            Namespace namespace = iterator.next();

            String newNamespace = defineNewNameSpace(namespace.getNamespaceURI());

            if (newNamespace != null) {
                newNamespaces.add(ef.createNamespace(namespace.getPrefix(), newNamespace));
                changeNamespaces = true;
            } else {
                newNamespaces.add(ef.createNamespace(namespace.getPrefix(), namespace.getNamespaceURI()));
            }
        }

        if (changeNamespaces) {

            StartElement updatedEvent = ef.createStartElement(
                    s.getName(),
                    s.getAttributes(),
                    newNamespaces.iterator());
            writer.add(updatedEvent);
        } else {
            writer.add(event);
        }
    }

    private String defineNewNameSpace(String namespaceURI) {
        return namespaceMapping.get(namespaceURI);
    }
}

