# 🎮 Square Games

API REST de gestion de parties de jeux de plateau développée avec **Java et Spring Boot**.

## 📌 Présentation

**Square Games** est une application backend permettant de créer et gérer des parties de jeux de plateau.

L'API permet notamment de :

* créer une partie ;
* choisir le type de jeu ;
* définir le nombre de joueurs et la taille du plateau ;
* associer des joueurs à une partie ;
* récupérer une partie ;
* récupérer les parties d'un joueur ;
* consulter les coups possibles ;
* jouer un coup ;
* vérifier que seul le joueur dont c'est le tour peut jouer.

L'identification du joueur est actuellement réalisée à l'aide de l'en-tête HTTP :

```text
X-UserId
```

---

## 🛠️ Technologies

* ☕ Java
* 🌱 Spring Boot
* 🌐 Spring Web
* 🗄️ Spring Data JPA
* 🧪 H2
* 🐘 PostgreSQL
* 📦 Maven
* 📖 Swagger / OpenAPI
* 🧪 Bruno
* 🔧 Git / GitHub

---

## 🏗️ Architecture

Le projet utilise une architecture en couches :

```text
Controller
    ↓
Service
    ↓
DAO
    ↓
Database
```

### Controller

La couche Controller expose les endpoints REST.

### Service

La couche Service contient la logique métier :

* création des parties ;
* vérification des utilisateurs ;
* gestion des joueurs ;
* vérification du tour du joueur ;
* validation des déplacements ;
* gestion des erreurs métier.

### DAO

La couche DAO permet de séparer la logique métier de l'accès aux données.

Le projet peut fonctionner avec différents systèmes de stockage selon la configuration utilisée.

---

## 🎲 Jeux

L'application utilise un système de **plugins** permettant de gérer différents types de jeux.

Les jeux pris en charge comprennent notamment :

* **Tic-Tac-Toe**
* **Connect Four**

L'utilisation de `GamePlugin` permet de faciliter l'ajout de nouveaux jeux.

---

# 🚀 Installation

## Prérequis

* Java
* Maven
* Git

Cloner le projet :

```bash
git clone https://github.com/Mouna-MAHMOUDI/Square_Games.git
```

Puis :

```bash
cd Square_Games
```

Construire le projet :

```bash
./mvnw clean install
```

Lancer l'application :

```bash
./mvnw spring-boot:run
```

L'application utilise le port :

```text
8080
```

---

# ⚙️ Configuration

Le projet possède plusieurs configurations :

```text
src/main/resources/

├── application.properties
├── application-h2.properties
└── application-postgres.properties
```

### H2

Le profil H2 permet de travailler avec une base de données en mémoire.

Il est particulièrement pratique pour le développement et les tests.

⚠️ Les données sont perdues lorsque l'application est arrêtée si la base H2 est configurée en mémoire.

### PostgreSQL

Le projet peut également être utilisé avec PostgreSQL grâce au profil PostgreSQL.

Les informations de connexion à la base de données doivent être configurées localement et ne doivent pas être publiées sur GitHub.

---

# 🔌 API REST

L'API est accessible à :

```text
http://localhost:8080
```

## Créer une partie

```http
POST /games
```

Header :

```http
X-UserId: <user-id>
```

Exemple :

```json
{
  "gameType": "tictactoe",
  "playerCount": 2,
  "boardSize": 3,
  "opponentIds": [
    "2f4c8731-208d-4cc2-a5cd-0287e4b4ace2"
  ]
}
```

---

## Récupérer une partie

```http
GET /games/{gameId}
```

---

## Récupérer les parties d'un joueur

```http
GET /games
```

Header :

```http
X-UserId: <user-id>
```

---

## Récupérer les coups possibles

```http
GET /games/{gameId}/moves
```

---

## Jouer un coup

```http
POST /games/{gameId}/moves
```

Header :

```http
X-UserId: <user-id>
```

Exemple :

```json
{
  "tokenName": "X",
  "x": 0,
  "y": 0
}
```

---

# 🔐 Gestion du joueur

L'identifiant du joueur est transmis avec :

```text
X-UserId
```

L'application vérifie notamment que :

* l'utilisateur existe ;
* les joueurs associés à une partie sont valides ;
* le joueur qui joue est bien le joueur dont c'est le tour.

Lorsqu'un joueur tente de jouer alors que ce n'est pas son tour, l'API retourne :

```text
403 Forbidden
```

L'authentification pourra évoluer vers une authentification **JWT** dans une prochaine étape.

---

# 📖 Swagger / OpenAPI

La documentation de l'API est disponible avec Swagger UI :

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger permet de :

* consulter les endpoints ;
* voir les paramètres ;
* consulter les modèles JSON ;
* visualiser les réponses HTTP ;
* tester directement les endpoints.

L'application est également configurée pour pouvoir être accessible depuis le réseau local.

Avec l'adresse IP de la machine qui exécute l'application :

```text
http://<IP_DU_PC>:8080/swagger-ui/index.html
```

---

# 🧪 Tests avec Bruno

Les endpoints de l'API ont été testés avec **Bruno**.

Les tests couvrent notamment :

| Test                                        | Résultat attendu |
| ------------------------------------------- | ---------------: |
| Création sans `X-UserId`                    |            `400` |
| Création valide                             |            `200` |
| Type de jeu inexistant                      |            `404` |
| Nombre de joueurs incohérent                |            `400` |
| Récupération d'une partie existante         |            `200` |
| Partie inexistante                          |            `404` |
| Récupération des parties d'un joueur        |            `200` |
| Récupération des coups possibles            |            `200` |
| Partie inexistante pour les coups possibles |            `404` |
| Jouer avec le bon joueur                    |            `200` |
| Jouer avec un mauvais joueur                |            `403` |

Une collection Bruno permet également de regrouper les requêtes et de mettre en place des tests automatisés avec des variables et du scripting.

---

# 📂 Structure du projet

```text
Square_Games/
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── mouna/
│   │   │           └── square_games/
│   │   │               ├── Controller/
│   │   │               ├── Service/
│   │   │               ├── Dao/
│   │   │               ├── plugin/
│   │   │               ├── GameCreationParams.java
│   │   │               └── MoveRequest.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-h2.properties
│   │       └── application-postgres.properties
│   │
│   └── test/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

# 🎯 Compétences mises en pratique

Ce projet permet de mettre en pratique :

* programmation orientée objet avec Java ;
* architecture en couches ;
* injection de dépendances avec Spring ;
* création d'API REST ;
* gestion des requêtes HTTP ;
* gestion des codes HTTP ;
* gestion des erreurs ;
* DAO et accès aux données ;
* JPA ;
* H2 ;
* PostgreSQL ;
* documentation OpenAPI / Swagger ;
* tests d'API avec Bruno ;
* communication entre applications ;
* gestion de l'identité des joueurs.

---

# 🔮 Évolutions

Les prochaines évolutions possibles du projet sont notamment :

* authentification avec JWT ;
* amélioration de la gestion des utilisateurs ;
* ajout de nouveaux jeux ;
* persistance complète avec PostgreSQL ;
* automatisation complète des tests Bruno ;
* ajout de tests unitaires et d'intégration ;
* amélioration de la validation des données.

---

# 👩‍💻 Auteur

**Mouna Mahmoudi**

Projet réalisé dans le cadre d'une formation en développement d'applications Java / Spring Boot.

### 🔗 GitHub

[Mouna-MAHMOUDI / Square_Games](https://github.com/Mouna-MAHMOUDI/Square_Games?utm_source=chatgpt.com)
