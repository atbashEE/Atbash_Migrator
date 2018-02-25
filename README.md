# Atbash_Migrator
Helps in migrating 'JSF Renderer extension', 'Octopus', ... to Atbash 'namespaces'

Program asks for a root directory, the directory where the maven pom.xml is located.

It recursively searches for all \*.java, pom.xml and \*.xhtml files.

. Within the java files, it converts the import statements
. Within the pom files, it converts the _groupId_, _artifactId_, _version_, and _properties_ tag content.
. Within the xhtml files, it converts the xml namespaces.


It is never a goal to be able to convert all possible projects flawless. Just an aid to make it easier for you.
So please make a copy of your code (if your code isn't under source-control) before you run this program. :)

## Under construction

For testing, set the directory containing the Maven project within the method


    be.atbash.migrator.AtbashMigrator#main

## Feedback

Let me know the issues or the improvements that you have with/for the Migrator!! https://github.com/atbashEE/Atbash_Migrator/issues