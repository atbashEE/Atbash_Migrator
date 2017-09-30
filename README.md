# Atbash_Migrator
Helps in migrating 'JSF Renderer extension', 'Octopus', ... to Atbash 'namespaces'

Program asks for a root directory, the directory where the source code is located.

It recursively searches for all \*.java, pom.xml and \*.xhtml files.

. Within the java files, it converters the import statements
. Within the pom files, it converts the _artifactId_ tag content.
. Within the xhtml files, it converts the xml namespaces.
