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

import java.util.ArrayList;

/**
 *
 */

public class TestVisitor {


    public void visitNode(Node node, int level) {
        System.out.println("level "+level);
        System.out.println(node.getClass().getName());
        System.out.println(node.getRange());
        System.out.println(node.toString());
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXX");

        int nextLevel = level+1;
        new ArrayList<>(node.getChildNodes()).forEach(n -> this.visitNode(n, nextLevel));
    }
}
