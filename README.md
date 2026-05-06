# 🎫 API Fil Rouge - Plateforme de Gestion d'Événements

[![Java](https://img.shields.io/badge/Java-23-orange.svg)](https://openjdk.org/projects/jdk/23/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)

## 📋 Table des matières

- [Description du projet](#-description-du-projet)
- [Fonctionnalités principales](#-fonctionnalités-principales)
- [Architecture technique](#-architecture-technique)
- [Choix techniques & architecture de l'API](#-choix-techniques--architecture-de-lapi)
- [Prérequis](#-prérequis)
- [Installation et configuration](#-installation-et-configuration)
- [Démarrage rapide](#-démarrage-rapide)
- [Documentation API](#-documentation-api)
- [Authentification et sécurité](#-authentification-et-sécurité)
- [Structure du projet](#-structure-du-projet)
- [Configuration HTTPS](#-configuration-https)
- [Tests](#-tests)
- [Déploiement](#-déploiement)
- [Contribution](#-contribution)

## 🎯 Description du projet

L'**API Dekin** est le moteur d'une application de danse développée avec Spring Boot. Elle permet aux utilisateurs de réaliser des danses et les poster sur l'application tout en recevant une note de réussite de ces danses avec un système d'authentification robuste. La plateforme favorise l'interaction sociale via un flux de vidéos, des commentaires et un système de likes.

### 🎪 Cas d'usage

- **Utilisateurs** : Création de comptes, gestion de profils, publication de vidéos de danse, abonnements, likes et commentaires.
- **Administrateurs** : Gestion complète de la plateforme, modération des contenus

## ✨ Fonctionnalités principales

### 🔐 Authentification et autorisation
- **Authentification JWT** avec tokens sécurisés
- **OAuth2** avec support **Google** et **GitHub**
- **Gestion des rôles** : User, Admin, AuthService
- **Permissions granulaires** par endpoint
- **URLs de redirection** configurées pour les frontends

### 💃 Système de Danse et Vidéo
- **Moteur de Scoring** : Enregistrement des scores de performance associés aux vidéos
- **Social Feed** : Publication de "Social Posts" liant une vidéo, une description et une catégorie musicale
- **Catégories Musicales** : Large choix de styles (Kpop, HipHop, Rock, Jazz, etc.) pour classer les défis

### 📱 Interactions Sociales
- **Likes & Engagement** : Système de likes sur les posts et les commentaires avec mise à jour en temps réel des compteurs
- **Commentaires** : Possibilité de commenter les performances des autres utilisateurs
- **Abonnements** : Système de "Followers" et "Subscriptions" pour créer son réseau

### 👤 Profils Utilisateurs
- Profils personnalisables (pseudo, bio, image de profil, bannière)
- Système de **Slugs** automatiques pour des URLs de profil propres (ex: `/users/slug/mon-pseudo`)
- Calcul de la moyenne des notes reçues par l'utilisateur

### 🛡️ Modération
- Système de signalement intégré (`countReportsReceived`)
- **NameValidator** : Filtrage automatique des pseudos contenant des insultes via un dictionnaire JSON

## 🏗️ Architecture technique

### Stack technologique

| Composant | Version | Description |
|-----------|---------|-------------|
| **Java** | 23 | Langage principal |
| **Spring Boot** | 3.4.2 | Framework principal |
| **Spring Security** | 3.4.2 | Sécurité et authentification |
| **Spring Data JPA** | 3.4.2 | Persistance des données |
| **Spring HATEOAS** | 3.4.2 | API REST hypermédia |
| **PostgreSQL** | 15+ | Base de données |
| **JWT** | 0.12.6 | Tokens d'authentification |
| **Maven** | 3.9+ | Gestion des dépendances |
| **Lombok** | - | Réduction du boilerplate |
| **SpringDoc OpenAPI** | 2.8.5 | Documentation API |

### Architecture des couches

```
┌─────────────────────────────────────────────────────────────┐
│                    Controllers (REST)                       │
├─────────────────────────────────────────────────────────────┤
│                    Services (Business Logic)                │
├─────────────────────────────────────────────────────────────┤
│                    Repositories (Data Access)               │
├─────────────────────────────────────────────────────────────┤
│                    Entities (Domain Models)                 │
├─────────────────────────────────────────────────────────────┤
│                    PostgreSQL Database                      │
└─────────────────────────────────────────────────────────────┘
```

### Modèles de données principaux

- **User** : Utilisateurs avec rôles et permissions
- **Category** : Catégories d'événements
- **SocialPost** : Les posts sociaux liant une vidéo, une description et une catégorie
- **Video** : Stockage de l'URL du média et du score obtenu par l'algorithme
- **Commentary** : Echanges textuels sous les vidéos
- **Like** : Interaction positive sur les posts ou commentaires
- **Subscription** : Relation de suivi entre deux utilisateurs

## 💡 Choix Techniques & Architecture de l'API

Cette section explique les décisions architecturales majeures pour faciliter la reprise du projet.

### ⚡ Automatisation des Endpoints (Spring Data REST)
L'une des particularités de ce projet est l'absence de contrôleurs manuels pour plusieurs entités (`SocialPost`, `Category`, `Like`, `Commentary`, `Video`).
*   **Choix** : Nous utilisons `Spring Data REST` via l'annotation `@RepositoryRestResource` sur les interfaces Repository.
*   **Avantage** : Cela génère automatiquement les endpoints CRUD respectant les standards HATEOAS sans écrire de code boilerplate.
*   **Conséquence** : Si vous cherchez un `SocialPostController` et que vous ne le trouvez pas, c'est normal. Tout est piloté par le repository et configuré dans `RestConfig.java`

### 🎭 Gestion des données exposées (Projections)
Puisque les contrôleurs sont automatisés, nous utilisons des **Projections** (dans le package `DTO`) pour contrôler finement les données JSON sortantes
*   Par exemple, `SocialPostProjection` permet d'inclure l'URL de la vidéo et le pseudo de l'utilisateur sans exposer tout l'objet User
*   Elles sont activées via le paramètre `?projection=nomDeLaProjection` dans les appels API

### 🔄 Logique métier via EventHandlers
Pour compenser l'absence de contrôleurs manuels lors de la création de ressources (ex: incrémenter un compteur de likes), nous utilisons des **RepositoryEventHandlers**
*   Voir `LikeEventHandler.java` : Il intercepte la création d'un "Like" pour mettre à jour automatiquement la somme totale des likes sur le post ou le commentaire concerné avant la sauvegarde en base

## 📋 Prérequis

### Système
- **Java 23** ou supérieur
- **Maven 3.9** ou supérieur
- **PostgreSQL 15** ou supérieur
- **Git** pour le versioning

### Outils recommandés
- **IDE** : IntelliJ IDEA, Eclipse, ou VS Code
- **Client API** : Postman, Insomnia, ou curl
- **Base de données** : pgAdmin ou DBeaver

## 🚀 Installation et configuration

### 1. Cloner le repository

```bash
git clone <repository-url>
cd apifilrouge
```

### 2. Configuration de la base de données

#### Créer la base de données PostgreSQL

```sql
CREATE DATABASE jwt_security;
CREATE USER postgres WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE jwt_security TO postgres;
```

#### Configuration de l'application

L'application utilise deux fichiers de configuration :

#### `application.yml` - Configuration principale

```yaml
server:
  port: 8090
  forward-headers-strategy: framework
  use-forward-headers: true
  tomcat:
    remote-ip-header: X-Forwarded-For
    protocol-header: X-Forwarded-Proto
    protocol-header-https-value: https
    port-header: X-Forwarded-Port
    port-header-https-value: 443
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/jwt_security
    username: postgres
    password: admin
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    database: postgresql
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

#### `application.properties` - Configuration OAuth2 et erreurs

```properties
# Configuration Google OAuth2
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

# URLs de redirection autorisées
app.oauth2.allowed-redirect-uris=http://localhost:3000,http://localhost:3100,https://your-ngrok-url.ngrok-free.app,https://veevent-admin.vercel.app,https://veevent.vercel.app

# Configuration des erreurs
server.error.include-message=always
server.error.include-binding-errors=always
```



### 3. Configuration OAuth2

L'application supporte l'authentification OAuth2 avec **Google** et **GitHub**.

#### Configuration Google OAuth2

Le fichier `application.properties` contient la configuration Google OAuth2 :

```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
```

#### Configuration GitHub OAuth2

Le fichier `application.yml` contient la configuration GitHub OAuth2 :

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: YOUR_GITHUB_CLIENT_ID
            client-secret: YOUR_GITHUB_CLIENT_SECRET
```

#### URLs de redirection autorisées

```properties
app.oauth2.allowed-redirect-uris=http://localhost:3000,http://localhost:3100,https://your-ngrok-url.ngrok-free.app,https://veevent-admin.vercel.app,https://veevent.vercel.app
```

**Note de sécurité** : Remplacez les credentials par défaut par vos propres clés OAuth2 pour la production. Les credentials actuels sont des exemples et ne doivent pas être utilisés en production.

## 🏃 Démarrage rapide

### 1. Compilation et démarrage

```bash
# Compiler le projet
./mvnw clean compile

# Démarrer l'application
./mvnw spring-boot:run
```

### 2. Vérification du démarrage

L'application démarre sur le port **8090** par défaut.

- **API Documentation** : http://localhost:8090/swagger-ui/index.html#/
- **Health Check** : http://localhost:8090/actuator/health
- **Base API** : http://localhost:8090/api/v1/

### 3. Premier test

```bash
# Test de l'API de santé
curl http://localhost:8090/actuator/health

# Test de récupération des posts
curl http://localhost:8090/socialPost
```

## 📚 Documentation API

### Endpoints principaux

| Endpoint | Description                      | Authentification |
|----------|----------------------------------|------------------|
| `/api/v1/auth/**` | Authentification et inscription  | Non              |
| `/api/v1/users/**` | Gestion des utilisateurs         | Partielle        |
| `/api/v1/socialPost/**` | Gestion des posts                | Oui              |
| `/api/v1/videos/**` | Gestion des videos               | Oui              |
| `/api/v1/like/**` | Gestion des likes                | Oui              |
| `/api/v1/categories/**` | Gestion des catégories           | Oui              |
| `/api/v1/commentary/**` | Gestion des commentaires         | Oui              |
| `/api/v1/subscribes/**` | Gestion des abonements           | Oui              |

### Exemples d'utilisation

#### Créer un compte utilisateur

```bash
curl -X POST http://localhost:8090/socialPost \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "pseudo": "johndoe",
    "role": "USER"
  }'
```

#### Se connecter

```bash
curl -X POST http://localhost:8090/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

#### Créer un socialPost (avec token)

```bash
curl -X POST http://localhost:8090/socialPost \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "socialPostDescription": "TestPost!",
    "user": "http://localhost:8090/api/v1/test/1",
    "video": "http://localhost:8090/api/v1/video/1",
    "category": "http://localhost:8090/api/v1/category/1",
    "socialPostLike": "http://localhost:8090/socialPostLike/2"
  }'
```

## 🔐 Authentification et sécurité

### Types d'authentification

1. **JWT (JSON Web Tokens)**
   - Authentification par email/mot de passe
   - Tokens avec expiration configurable
   - Refresh tokens (optionnel)

2. **OAuth2**
   - Authentification **Google** et **GitHub**
   - Intégration avec les réseaux sociaux
   - URLs de redirection configurées pour les frontends

### Rôles et permissions

| Rôle | Permissions | Description |
|------|-------------|-------------|
| **USER** | Lecture publique, gestion profil, participation | Utilisateur standard |
| **ADMIN** | Toutes les permissions | Administrateur système |
| **AUTH_SERVICE** | Authentification et gestion utilisateurs | Service d'authentification |

### Sécurité des endpoints

- **CSRF** : Désactivé (API stateless)
- **CORS** : Configuré pour les origines autorisées
- **Rate Limiting** : Configurable
- **Validation** : Bean Validation sur tous les DTOs

## 📁 Structure du projet

```
apifilrouge/
├── src/
│   ├── main/
│   │   ├── java/com/projetfilrougeapi/apifilrouge/
│   │   │   ├── auth/               # Authentification
│   │   │   ├── config/             # Configuration Spring
│   │   │   ├── DTO/                # Data Transfer Objects
│   │   │   ├── email/              # Service d'emails
│   │   │   ├── endpoint_api/       # Controllers et Services
│   │   │   │   ├── category/       # Gestion des catégories
│   │   │   │   ├── commentary/     # Gestion des commentaires
│   │   │   │   ├── like/           # Gestion des likes
│   │   │   │   ├── socialPost/     # Gestion des posts sociaux
│   │   │   │   ├── subscription/   # Gestion des abonnements
│   │   │   │   ├── user/           # Gestion des utilisateurs
│   │   │   │   └── video/          # Gestion des vidéos et scores
│   │   │   ├── helper/             # Utilitaires
│   │   │   ├── Specification/      # Spécifications JPA
│   │   │   └── validator/          # Validateurs personnalisés
│   │   └── resources/
│   │       ├── application.yml     # Configuration principale
│   │       ├── application-prod.yml # Configuration production
│   │       ├── json/               # Données de test
│   │       └── templates/          # Templates d'emails
│   └── test/                       # Tests unitaires et d'intégration
├── pom.xml                         # Configuration Maven
├── mvnw                           # Wrapper Maven
└── README.md                      # Ce fichier
```

## 🔒 Configuration HTTPS

### Pour le développement avec ngrok

1. **Démarrer l'application** :
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Démarrer ngrok** :
   ```bash
   ngrok http 8090
   ```

3. **Utiliser l'URL HTTPS** fournie par ngrok

### Configuration automatique

L'application détecte automatiquement les en-têtes de forwarding et génère les liens HATEOAS en HTTPS quand nécessaire.

### Configuration des erreurs

Le fichier `application.properties` configure l'affichage détaillé des erreurs :

```properties
server.error.include-message=always
server.error.include-binding-errors=always
```

Cette configuration facilite le débogage en développement en affichant les messages d'erreur complets.

## 🧪 Tests

### Exécution des tests

```bash
# Tests unitaires
./mvnw test

# Tests d'intégration
./mvnw verify

# Tests avec couverture
./mvnw jacoco:report
```

### Types de tests

- **Tests unitaires** : Services et utilitaires
- **Tests d'intégration** : Controllers et repositories
- **Tests de sécurité** : Authentification et autorisation
- **Tests de validation** : DTOs et entités

## 🚀 Déploiement

### Environnement de développement

```bash
./mvnw spring-boot:run
```

### Environnement de production

```bash
# Compilation pour la production
./mvnw clean package -Pprod

# Démarrage avec profil production
java -jar target/apifilrouge-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Configuration Docker (optionnel)

```dockerfile
FROM openjdk:23-jdk-slim
COPY target/apifilrouge-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `SPRING_PROFILES_ACTIVE` | Profil Spring | `dev` |
| `DB_URL` | URL base de données | `jdbc:postgresql://localhost:5432/jwt_security` |
| `DB_USERNAME` | Utilisateur DB | `postgres` |
| `DB_PASSWORD` | Mot de passe DB | `admin` |
| `JWT_SECRET` | Clé secrète JWT | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` |
| `GOOGLE_CLIENT_ID` | Client ID Google OAuth2 | Configuré dans `application.properties` |
| `GOOGLE_CLIENT_SECRET` | Client Secret Google OAuth2 | Configuré dans `application.properties` |
| `GITHUB_CLIENT_ID` | Client ID GitHub OAuth2 | Configuré dans `application.yml` |
| `GITHUB_CLIENT_SECRET` | Client Secret GitHub OAuth2 | Configuré dans `application.yml` |

## 🤝 Contribution

### Comment contribuer

1. **Fork** le projet
2. **Créer** une branche feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** vos changements (`git commit -m 'Add some AmazingFeature'`)
4. **Push** vers la branche (`git push origin feature/AmazingFeature`)
5. **Ouvrir** une Pull Request

### Standards de code

- **Java** : Suivre les conventions Oracle
- **Spring Boot** : Respecter les bonnes pratiques Spring
- **Tests** : Maintenir une couverture > 80%
- **Documentation** : Commenter les méthodes publiques

### Checklist avant contribution

- [ ] Code compilé sans erreurs
- [ ] Tests unitaires passent
- [ ] Tests d'intégration passent
- [ ] Documentation mise à jour
- [ ] Code review effectuée

### Ressources utiles

- [Documentation Spring Boot](https://spring.io/projects/spring-boot)
- [Documentation Spring Security](https://spring.io/projects/spring-security)
- [Documentation PostgreSQL](https://www.postgresql.org/docs/)
- [Guide JWT](https://jwt.io/introduction)

---

**Développé avec ❤️ par l'équipe Dekin**

*Dernière mise à jour : Mai 2026* 
