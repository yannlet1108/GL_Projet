Compilation et exécution (Maven)

Pour compiler le projet depuis le répertoire `projet`:

    mvn -q -f pom.xml clean package

Pour exécuter l'application (classe `polytech.info5.gl.projet.App`):

    mvn -q -f pom.xml exec:java -Dexec.mainClass="polytech.info5.gl.projet.App"

Remarque: les contrôleurs et vues sont des stubs minimaux générés à partir des diagrammes UML.
