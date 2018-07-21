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
package be.atbash.migrator.maven;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.stream.Collectors;

import static org.w3c.dom.Node.ELEMENT_NODE;

/**
 *
 */

public class MavenProperties {

    private Map<PropertyIdentification, String> propertyValues = new HashMap<>();

    public void addNode(Element xmlElement) {
        readPropertyValues(xmlElement);
    }

    private void readPropertyValues(Element xmlElement) {
        NodeList childNodes = xmlElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (ELEMENT_NODE == childNodes.item(i).getNodeType()) {
                String nodeName = childNodes.item(i).getNodeName();
                PropertyIdentification identification = new PropertyIdentification(childNodes.item(i), nodeName);
                propertyValues.put(identification, childNodes.item(i).getTextContent());
            }
        }
    }

    public boolean hasPropertyName(String key) {
        return !getMatchingProperties(key).isEmpty();
    }

    private List<PropertyIdentification> getMatchingProperties(String key) {
        return propertyValues.keySet().stream().filter(pi -> pi.getName().equals(key))
                .collect(Collectors.toList());
    }

    public void setProperty(String key, String value) {
        // FIXME support for adding property ??
        getMatchingProperties(key).forEach(pi -> propertyValues.put(pi, value));
    }

    public void redefineNode() {
        propertyValues.entrySet().forEach(this::updateValue);
    }

    private void updateValue(Map.Entry<PropertyIdentification, String> entry) {
        Node node = entry.getKey().getNode();
        if (entry.getValue() == null) {
            // remove ??
            // Does this work?

            node.getParentNode().removeChild(node);
        } else {
            node.setTextContent(entry.getValue());
        }
    }

    private static class PropertyIdentification {
        private Node node;
        private String name;

        public PropertyIdentification(Node node, String name) {
            this.node = node;
            this.name = name;
        }

        public Node getNode() {
            return node;
        }

        public String getName() {
            return name;
        }
    }
}
