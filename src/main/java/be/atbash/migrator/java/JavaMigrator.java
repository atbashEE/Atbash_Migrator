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
package be.atbash.migrator.java;

import be.atbash.migrator.maven.MavenConfigMigrator;
import be.atbash.util.exception.AtbashUnexpectedException;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Name;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 */

public class JavaMigrator {

    private Map<String, String> classMapping;
    private Map<String, String> identifierMapping;

    public JavaMigrator() {
        readMapping();
    }

    private void readMapping() {
        InputStream mapping = MavenConfigMigrator.class.getResourceAsStream("/class.mapping");
        try {
            Properties properties = new Properties();
            properties.load(mapping);
            mapping.close();

            classMapping = new HashMap<>();
            properties.stringPropertyNames().forEach(
                    key -> classMapping.put(key, properties.getProperty(key))
            );

            identifierMapping = new HashMap<>();
            classMapping.forEach(
                    (key, value) -> {
                        String identifierOld = getIdentifier(key);
                        String identifierNew = getIdentifier(value);
                        if (!identifierOld.equals(identifierNew)) {
                            identifierMapping.put(getIdentifier(key), getIdentifier(value));
                        }
                    });

        } catch (IOException e) {
            throw new AtbashUnexpectedException(e);
        }
    }

    private String getIdentifier(String name) {
        return JavaParser.parseName(name).getIdentifier();
    }

    public void transform(Path file) {
        try {
            // Read the File contents
            String content = new String(Files.readAllBytes(file));
            // Parse the file into an AST
            CompilationUnit cu = JavaParser.parse(content);
            // For all class definitions (can be multiple) analyse it
            cu.getTypes().forEach(t -> processType(cu, t));

            Files.write(file, cu.toString().getBytes());

        } catch (ParseProblemException | IOException e) {
            throw new AtbashUnexpectedException(e);
        }

    }

    private void processType(CompilationUnit cu, TypeDeclaration<?> typeDeclaration) {

        //new TestVisitor().visitNode(typeDeclaration, 1);
        changeImportTypes(cu);

        changeIdentifiers(typeDeclaration);
    }

    private void changeIdentifiers(TypeDeclaration<?> typeDeclaration) {
        new IdentifierVisitor(identifierMapping).replaceIdentifiers(typeDeclaration);
    }

    private void changeImportTypes(CompilationUnit cu) {
        List<ImportDeclaration> imports = cu.findAll(ImportDeclaration.class);
        for (ImportDeclaration importItem : imports) {

            String importClassName = ((Name) importItem.getChildNodes().get(0)).asString();
            if (classMapping.containsKey(importClassName)) {
                Name name = JavaParser.parseName(classMapping.get(importClassName));
                importItem.replace(importItem.getChildNodes().get(0), name);

            }
        }
    }
}
