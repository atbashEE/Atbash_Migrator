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
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.w3c.dom.Node.ELEMENT_NODE;

/**
 *
 */

public class MavenProperties {

    private Element xmlElement;
    private Map<String, String> propertyValues;

    public MavenProperties(Element xmlElement) {
        this.xmlElement = xmlElement;

        propertyValues = new HashMap<>();
        readPropertyValues();
    }

    private void readPropertyValues() {
        NodeList childNodes = xmlElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (ELEMENT_NODE == childNodes.item(i).getNodeType()) {
                propertyValues.put(childNodes.item(i).getNodeName(), childNodes.item(i).getTextContent());
            }
        }
    }

    public void setProperty(String key, String value) {
        propertyValues.put(key, value);
    }

    public void redefineNode() {
        Set<String> propertiesUpdated = new HashSet<>();

        NodeList childNodes = xmlElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (ELEMENT_NODE == childNodes.item(i).getNodeType()) {
                String key = childNodes.item(i).getNodeName();
                if (propertyValues.containsKey(key)) {
                    childNodes.item(i).setTextContent(propertyValues.get(key));
                    propertiesUpdated.add(key);
                } else {
                    xmlElement.removeChild(childNodes.item(i)); // TODO Test if this works
                }
            }
        }

        // FIXME Those PropertyValues which are not in propertiesUpdated -> Add Tag
    }
}
