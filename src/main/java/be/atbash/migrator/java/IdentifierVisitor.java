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

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 */

class IdentifierVisitor {

    private Map<String, String> identifierMapping;

    IdentifierVisitor(Map<String, String> identifierMapping) {
        this.identifierMapping = identifierMapping;
    }

    void replaceIdentifiers(Node node) {
        if (node instanceof Name) {
            Name name = (Name) node;
            if (identifierMapping.containsKey(name.getIdentifier())) {
                name.setIdentifier(identifierMapping.get(name.getIdentifier()));
            }
        }

        if (node instanceof SimpleName) {
            SimpleName simpleName = (SimpleName) node;
            if (identifierMapping.containsKey(simpleName.getIdentifier())) {
                simpleName.setIdentifier(identifierMapping.get(simpleName.getIdentifier()));
            }
        }

        new ArrayList<>(node.getChildNodes()).forEach(this::replaceIdentifiers);
    }

}
