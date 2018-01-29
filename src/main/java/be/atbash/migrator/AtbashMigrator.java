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

import be.atbash.migrator.java.JavaMigrator;
import be.atbash.migrator.maven.MavenConfigMigrator;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */

public class AtbashMigrator {

    private MavenConfigMigrator mavenConfigMigrator;
    private JavaMigrator javaMigrator;
    private FaceletsMigrator faceletsMigrator;

    private AtbashMigrator() {
        mavenConfigMigrator = new MavenConfigMigrator();
        javaMigrator = new JavaMigrator();
        faceletsMigrator = new FaceletsMigrator();
    }

    public void transformFiles(Path startPath) throws IOException {
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.getFileName().toString().equals("pom.xml")) {
                    mavenConfigMigrator.transform(file);
                }
                if (file.toString().endsWith(".java")) {
                    javaMigrator.transform(file);
                }
                if (file.toString().endsWith(".xhtml")) {
                    faceletsMigrator.transform(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException e) {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Atbash Migrator");
        System.out.println("===============");

        Path startPath = Paths.get("/Users/rubus/temp/securedComponent");
        new AtbashMigrator().transformFiles(startPath);
    }
}
