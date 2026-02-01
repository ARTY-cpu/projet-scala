# Graphes de Markov en Scala

Projet de programmation fonctionnelle implémentant une bibliothèque pour manipuler des **graphes de Markov** (chaînes de Markov) en Scala 3.

## Description du Projet

Ce projet fournit une bibliothèque complète pour créer, manipuler et valider des graphes de Markov. Les chaînes de Markov sont des modèles mathématiques représentant des systèmes évoluant de manière probabiliste, avec des applications en météorologie, finance, intelligence artificielle et biologie.

### Fonctionnalités

#### Partie 1 : Structures de données et validation

- **Deux représentations de graphes** :
  - Matrice d'adjacence (graphes denses)
  - Liste d'adjacence (graphes creux)
  
- **Validation mathématique** : Vérification que la somme des probabilités sortantes de chaque sommet égale 1

- **Chargement depuis fichiers** : Import de graphes au format texte

- **Export Mermaid** : Génération de diagrammes au format Mermaid pour visualisation

- **Architecture polymorphe** : Manipulation des graphes indépendamment de leur représentation interne

#### Partie 2 : Calculs matriciels et distributions

- **Opérations matricielles** :
  - Multiplication de matrices
  - Puissance de matrice (M^n)
  - Calcul de différence entre matrices
  
- **Distributions de probabilités** :
  - Création de distributions initiales
  - Évolution temporelle : Π(n) = Π(0) × M^n
  - Recherche de distribution stationnaire
  
- **Prévisions météorologiques** : Application pratique pour prédire l'évolution de la météo

- **Analyse de convergence** : Détection automatique de la convergence vers un état stable

- **Composantes fortement connexes (CFC)** :
  - Algorithme de Tarjan pour détecter les classes
  - Classification : classes transitoires vs persistantes
  - Extraction de sous-matrices par classe
  - Distribution stationnaire par classe
  - Analyse complète des graphes irréductibles et non-irréductibles

### Technologies

- **Langage** : Scala 3.7.4
- **Build** : SBT (Simple Build Tool)
- **Paradigme** : Programmation fonctionnelle (immutabilité, HOF, récursion terminale)

## Structure du Projet

```
projet-scala/
├── src/                           # Code source
│   ├── Partie 1 : Structures de base
│   │   ├── Graphe.scala          # Trait abstrait pour graphes
│   │   ├── Matrice.scala         # Trait générique pour matrices
│   │   ├── MatriceAdjacence.scala
│   │   ├── ListeAdjacence.scala
│   │   ├── Chargeur.scala        # Chargement depuis fichiers
│   │   ├── Validation.scala      # Validation des graphes de Markov
│   │   └── Mermaid.scala         # Export au format Mermaid
│   │
│   ├── Partie 2 : Calculs avancés
│   │   ├── CalculsMatriciels.scala # Opérations matricielles
│   │   ├── Distribution.scala      # Distributions de probabilités
│   │   ├── ComposantesFortementConnexes.scala # Algorithme de Tarjan, CFC
│   │   ├── TestPartie2.scala       # Tests calculs et distributions
│   │   └── TestComposantes.scala   # Tests CFC et distributions par classe
│   │
│   ├── Tests et programmes principaux
│   │   ├── Main.scala            # Programme principal Partie 1
│   │   ├── TestPartie2.scala     # Programme de test Partie 2
│   │   ├── TestMermaid.scala     # Tests export Mermaid
│   │   └── TestValidation.scala  # Tests validation
│   │
│   └── project.scala             # Configuration scala-cli
│
├── exemples/                     # Fichiers d'exemple
├── build.sbt                     # Configuration SBT
└── README                        # Ce fichier
```

## Installation et Prérequis

### Prérequis

- **Scala** 3.7.4 ou supérieur
- **SBT** (Simple Build Tool)
- **JDK** 11 ou supérieur

### Installation

1. Cloner ou télécharger ce projet
2. S'assurer que SBT est installé sur votre système

## Exécution du Projet

### Option 1 : Avec Scala-CLI (Recommandé)

```bash
# Programme principal Partie 1
scala-cli run src --main-class Main

# Tests Partie 2 (calculs matriciels et distributions)
scala-cli run src --main-class TestPartie2

# Tests composantes fortement connexes (Tarjan)
scala-cli run src --main-class TestComposantes

# Tests export Mermaid
scala-cli run src --main-class TestMermaid

# Tests validation
scala-cli run src --main-class testValidation
```

### Option 2 : Avec SBT

```bash
# Depuis le répertoire racine du projet
sbt run
```

Cette commande compile automatiquement le projet et exécute le programme principal.

### Option 3 : Avec SBT Console

```bash
# Lancer le console interactif Scala
sbt console

# Puis utiliser les classes du projet
scala> val graphe = Chargeur.charger("exemples/exemple1.txt")
scala> graphe.foreach(_.afficher())
```

### Option 4 : Compilation et Exécution Séparées

```bash
# Compilation
sbt compile

# Exécution
sbt run
```

### Génération de la Documentation

```bash
# Générer la Scaladoc
sbt doc

# La documentation sera disponible dans target/scala-3.7.4/api/
```

## Format des Fichiers d'Entrée

Les fichiers d'exemple dans `exemples/` suivent ce format :

```
n
i j p
i j p
...
```

Où :

- `n` : nombre de sommets
- `i j p` : transition du sommet `i` vers le sommet `j` avec la probabilité `p`

**Exemple** (`exemple1.txt`) :

```
3
1 2 0.5
1 3 0.5
2 1 1.0
3 2 1.0
```

## Exemples d'Utilisation

### Partie 1 : Programme Principal

Le programme principal charge et affiche un graphe depuis `exemples/exemple1.txt`. Pour utiliser un autre fichier, modifiez la variable `fichier` dans [src/Main.scala](src/Main.scala).

**Sortie attendue :**

```
==================================================
GRAPHES DE MARKOV
==================================================

Chargement: exemples/exemple1.txt
--------------------------------------------------
ok 3 sommets

[Affichage du graphe]

>>> Successeurs:
--------------------------------------------------
Sommet 1 → 2(0.50), 3(0.50)
Sommet 2 → 1(1.00)
Sommet 3 → 2(1.00)

==================================================
```

### Partie 2 : Calculs Matriciels et Distributions

Le programme de test [src/TestPartie2.scala](src/TestPartie2.scala) effectue :

1. **Chargement du graphe météo** (`exemple_meteo.txt`)
2. **Calcul de M³** : prévisions à 3 jours
3. **Calcul de M⁷** : prévisions à 7 jours
4. **Évolution de distributions** : réponses aux questions météorologiques
5. **Recherche de distribution stationnaire**
6. **Test de convergence** sur tous les exemples

**Questions résolues :**

- *"Quelle probabilité que le temps soit nuageux dans 3 jours s'il fait beau aujourd'hui ?"*
  → **37.4%**

- *"Quelles probabilités dans 7 jours s'il pleut aujourd'hui ?"*
  → Distribution complète affichée

- *"Atteint-on une distribution stationnaire ?"*
  → **OUI, convergence en 9 étapes**

**Résultats de convergence (ε = 0.01) :**

| Exemple | Convergence | Itérations |
|---------|-------------|------------|
| exemple1.txt | ✓ | 29 |
| exemple2.txt | ✓ | 48 |
| exemple3.txt | ✗ | - |
| exemple_meteo.txt | ✓ | 9 |
| exemple_valid_step3.txt | ✓ | 11 |

**Note :** `exemple3.txt` ne converge pas car il contient plusieurs composantes fortement connexes distinctes.

## Tests

Des exemples de validation sont fournis dans le répertoire `exemples/` :

- `exemple1.txt`, `exemple2.txt`, `exemple3.txt` : graphes valides
- `exemple_meteo.txt` : modèle météorologique
- `exemple_valid_step3.txt` : exemple avec export Mermaid

## Documentation Complète

Pour plus de détails sur l'architecture, les choix de conception et l'analyse fonctionnelle, consultez :

- [RAPPORT.md](RAPPORT.md) : Rapport technique complet
- [inventaire_des_notions.md](inventaire_des_notions.md) : Concepts Scala utilisés
- `scaladoc/` : Documentation API générée
