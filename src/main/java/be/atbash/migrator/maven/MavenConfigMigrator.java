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

import be.atbash.util.exception.AtbashUnexpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 *
 */
public class MavenConfigMigrator {

    private static final List<String> GROUP_ID_CANDIDATES;

    static {
        GROUP_ID_CANDIDATES = new ArrayList<>();
        GROUP_ID_CANDIDATES.add("be.c4j.ee.security");
        GROUP_ID_CANDIDATES.add("be.rubus");
    }

    private Map<String, String> dependencyMapping;

    public MavenConfigMigrator() {
        readMapping();
    }

    private void readMapping() {
        InputStream mapping = MavenConfigMigrator.class.getResourceAsStream("/dependency.mapping");
        try {
            Properties properties = new Properties();
            properties.load(mapping);
            mapping.close();

            dependencyMapping = new HashMap<>();
            properties.stringPropertyNames().forEach(
                    key -> dependencyMapping.put(key, properties.getProperty(key))
            );

        } catch (IOException e) {
            throw new AtbashUnexpectedException(e);
        }
    }

    public void transform(Path file) {

        Document doc = readMavenPomFile(file);

        List<MavenDependency> mavenDependencies = new ArrayList<>();

        NodeList dependencies = doc.getElementsByTagName("dependency");
        for (int i = 0; i < dependencies.getLength(); i++) {
            Element dependency = (Element) dependencies.item(i);
            mavenDependencies.add(new MavenDependency(dependency));
        }

        final MavenProperties mavenProperties = defineMavenProperties(doc);

        mavenDependencies.forEach(dep -> transformDependency(dep, mavenProperties));

        if (mavenProperties != null) {
            mavenProperties.redefineNode();
        }
        writeFile(doc, file);
    }

    private void writeFile(Document doc, Path file) {
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(Files.newOutputStream(file));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
        } catch (TransformerException | IOException e) {
            throw new AtbashUnexpectedException(e);
        }
    }

    private void transformDependency(MavenDependency mavenDependency, MavenProperties mavenProperties) {
        if (candidate(mavenDependency)) {
            String newDependency = dependencyMapping.get(mavenDependency.getGroupArtifactId());
            String[] parts = newDependency.split(":");
            mavenDependency.setGroupId(parts[0]);
            mavenDependency.setArtifactId(parts[1]);
            if (mavenDependency.isVersionDefined()) {
                if (mavenDependency.isVersionHardCoded()) {
                    // FIXME
                } else {
                    mavenProperties.setProperty(mavenDependency.getVersionProperty(), parts[2]);
                }
            }
        }

    }

    private boolean candidate(MavenDependency mavenDependency) {
        Optional<String> candidateType = GROUP_ID_CANDIDATES.stream().filter(f -> mavenDependency.getGroupId().startsWith(f)).findAny();
        return candidateType.isPresent();
    }

    private MavenProperties defineMavenProperties(Document doc) {
        MavenProperties mavenProperties = null;
        NodeList properties = doc.getElementsByTagName("properties");
        if (properties.getLength() > 0) {
            mavenProperties = new MavenProperties((Element) properties.item(0));
        }
        return mavenProperties;
    }

    private Document readMavenPomFile(Path file) {
        Document result;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            result = dBuilder.parse(Files.newInputStream(file));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new AtbashUnexpectedException(e);
        }
        return result;
    }
}
