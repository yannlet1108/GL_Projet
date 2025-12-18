# | Projet JDR - Génie Logiciel INFO5 |

Projet de conception d'un logiciel de gestion de jeu de rôle (JDR) dans le cadre du cours de Génie Logiciel INFO5 Polytech Grenoble.


### Auteurs :
- Yohan Chevrier-Pivot - [@YohanChePi](https://github.com/YohanChePi)
- Yann Letourneur - [@yannlet1108](https://github.com/yannlet1108)


### Prerequis :
- Java 11 ou supérieur (```java -version``` pour vérifier l'installation)
- Maven 3.6 ou supérieur (```mvn -v``` pour vérifier l'installation)
- Git pour cloner le dépôt (```git --version``` pour vérifier l'installation)

### Installation :
1. Cloner le dépôt Git :
   ```bash
    git clone https://github.com/yannlet1108/GL_Projet.git
    cd GL_Projet
    ```
2. Compiler le projet avec Maven (+execution des tests) :
    ```bash
    cd projet
    mvn -q -f pom.xml clean package
    ```

### Exécution :
Pour exécuter l'application, utilisez la commande suivante :
    ```bash
    mvn -f pom.xml exec:java -Dexec.mainClass="polytech.info5.gl.projet.App"
    ```

Pour exécuter les tests unitaires, utilisez la commande suivante :
(l'option -q pour "quiet" réduit la verbosité de la sortie)
    ```bash
    mvn -f pom.xml test
    ```


### Fonctionnalités :
