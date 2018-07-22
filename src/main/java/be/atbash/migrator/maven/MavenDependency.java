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

import static org.w3c.dom.Node.ELEMENT_NODE;

/**
 *
 */

public class MavenDependency {

    private Element xmlElement;

    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
    private String classifier;
    private String type;

    public MavenDependency(Element xmlElement) {
        this.xmlElement = xmlElement;
        defineSubTagValues();
    }

    private void defineSubTagValues() {
        NodeList childNodes = xmlElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (ELEMENT_NODE == childNodes.item(i).getNodeType()) {
                switch (childNodes.item(i).getNodeName()) {
                    case "groupId":
                        groupId = childNodes.item(i).getTextContent();
                        break;
                    case "artifactId":
                        artifactId = childNodes.item(i).getTextContent();
                        break;
                    case "version":
                        version = childNodes.item(i).getTextContent();
                        break;
                    case "scope":
                        scope = childNodes.item(i).getTextContent();
                        break;
                    case "classifier":
                        classifier = childNodes.item(i).getTextContent();
                        break;
                    case "type":
                        type = childNodes.item(i).getTextContent();
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Unsupported value for tag name %s", childNodes.item(i).getNodeName()));
                }
            }
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
        xmlElement.getElementsByTagName("groupId").item(0).setTextContent(groupId);
    }

    public String getGroupArtifactId() {
        return groupId + ":" + artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
        xmlElement.getElementsByTagName("artifactId").item(0).setTextContent(artifactId);
    }

    public void setVersion(String version) {
        this.version = version;
        xmlElement.getElementsByTagName("version").item(0).setTextContent(version);
    }

    public boolean isVersionDefined() {
        return version != null;
    }

    public boolean isVersionHardCoded() {
        return version != null && !version.startsWith("$");
    }

    public String getVersionProperty() {
        // We assume '${property}' with no spaces.
        return version.substring(2, version.length() - 1);
    }

    public String getScope() {
        return scope;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getType() {
        return type;
    }
}
