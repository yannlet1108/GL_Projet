# Bilan des outils de modélisation UML

Afin de modéliser nos diagrammes UML, nous avons choisi d’utiliser l’outil **PlantUML**.

## Pourquoi PlantUML ?

- **Familiarité** : PlantUML nous avait déjà été recommandé lors des cours de bases de données de l'année dernière, donc nous disposions déjà d’une certaine expérience avec cet outil. De  plus, Yann a également utilisé PlantUML durant son stage.
- **Documentation** : PlantUML propose une documentation complète, claire et facile à prendre en main, ce qui facilite l’apprentissage et l’utilisation de ses fonctionnalités.
- **Popularité** : PlantUML est largement utilisé, ce qui permet de trouver facilement des exemples en ligne ainsi que des réponses à des problématiques similaires rencontrées par la communauté.
- **Intégration** : VS Code, notre éditeur de code principal, dispose d’une extension PlantUML permettant de créer et de visualiser les diagrammes UML directement dans l’éditeur, améliorant ainsi le confort et la rapidité de travail.
- **Format textuel** : PlantUML utilise un format textuel pour décrire les diagrammes, ce qui facilite la gestion des versions avec des systèmes de contrôle de version comme Git. Cela permet également de modifier rapidement les diagrammes sans avoir à manipuler une interface graphique.

## Inconvénients de PlantUML

- **Disposition des éléments** : La disposition automatique des éléments générée par PlantUML n’est pas toujours optimale (par exemple : flèches qui se croisent alors qu'il existe une disposition des éléments pour laquelle il n'y a aucun croisement). Les possibilités de personnalisation sont limitées et nécessitent parfois l’utilisation de nombreux ajustements pour obtenir le rendu souhaité. Il n’existe pas de moyen simple permettant de positionner manuellement ou précisément les éléments du diagramme.
- **Dimensions des diagrammes complexes** : Pour les diagrammes comportant un grand nombre d’éléments, PlantUML tend à produire des diagrammes très larges ou très longs, ce qui complique leur lisibilité ainsi que leur mise en forme dans le rapport en A4.
- **Performance** : La génération de diagrammes très complexes peut être relativement lente, surtout couplée à l’utilisation de Live Share (qui nous permettait de collaborer en temps réel sur les diagrammes).
- **Tolérance aux erreurs** : PlantUML est strict concernant sa syntaxe, la moindre erreur empêche la génération complète du diagramme. Cela rend difficile la visualisation progressive lors de l'écriture de diagrammes complexes.
- **Stylisation limitée** : Bien que PlantUML offre des options de personnalisation, elles restent limitées et très fonctionnelles.
- **Description textuelle** : La nature textuelle de PlantUML oblige à avoir un visualisateur / générateur de rendu pour pouvoir avoir une idée du rendu final et rend difficile la bonne comprehension du diagramme avec seulement le code source.

## Adaptations et solutions
- **Modularisation des diagrammes** : Diviser les diagrammes complexes en plusieurs sous-diagrammes (un par page grâce à au mot-clé `newpage`) permet d’améliorer la lisibilité et de réduire les problèmes de mise en page.
- **Validation incrémentale** :  le "live preview" de l’extension VS Code permet de visualiser les modifications en temps réel, facilitant la detection des erreurs de syntaxe immediatement.

Pour conclure, PlantUML s’est révélé être un outil efficace et adapté à nos besoins de modélisation UML. Grâce à sa simplicité d’utilisation et à son intégration avec notre environnement de développement, nous n’avons pas rencontré de problèmes majeurs ni de limitations insurmontables lors de son utilisation, et nous avons pu mener le projet à bien sans avoir besoin d’utiliser d’autres outils de modélisation en complément.

