# 🎟️ API REST — Billetterie de Spectacle & Gestion Événementielle

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon_Cloud-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://neon.tech/)
[![Render](https://img.shields.io/badge/Render-Hosted-46E3B7?style=for-the-badge&logo=render&logoColor=black)](https://render.com/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

---

## 📖 Introduction & Présentation du Projet

L'application **Billetterie** est une API REST d'entreprise développée avec **Spring Boot (Java 21)**, **Spring Data JPA**, **PostgreSQL**, **Flyway** et documentée avec **SpringDoc OpenAPI 3 (Swagger UI)**.

Elle propose une solution complète pour la gestion événementielle et la réservation de places :
- 🏛️ **Gestion des salles et configurations de places** (catégories Platine, Or, Argent, Standard, rangs et numéros).
- 📅 **Programmation des événements** avec contrôles temporels stricts (recherche par date, contrôle d'antériorité).
- 🎫 **Cycle de vie des réservations** : Statut initial `PROVISOIRE`, verrouillage d'intégrité anti-surréservation, confirmation de paiement `PAYEE`, libération planifiée des expirées et annulation avec barème dégressif de remboursement.
- 📊 **Restitution & Décisionnel** : Calcul en temps réel des places disponibles et du taux de remplissage par catégorie via des vues SQL haute performance, ainsi qu'export direct au format **CSV**.

---

## 📌 Accès Rapide & Liens Swagger UI

L'application est **entièrement déployée en continu sur Render** et connectée à une base de données cloud **Neon PostgreSQL**. Elle est également conçue pour s'exécuter et se tester immédiatement en environnement local.

| Environnement | URL de Base API | Documentation Interactive Swagger UI | Spécification OpenAPI JSON |
| :--- | :--- | :--- | :--- |
| **🌐 En Ligne (Render + Neon)** | `https://billetterie-f33g.onrender.com` | [Swagger UI En Ligne](https://billetterie-f33g.onrender.com/swagger-ui/index.html) | [`/v3/api-docs`](https://billetterie-f33g.onrender.com/v3/api-docs) |
| **💻 Local (PostgreSQL)** | `http://localhost:8080` | [Swagger UI Local](http://localhost:8080/swagger-ui/index.html) | [`/v3/api-docs`](http://localhost:8080/v3/api-docs) |

> 💡 **Remarque** : Les deux interfaces Swagger UI vous permettent d'explorer interactivement l'ensemble des contrôleurs, de consulter les schémas DTO (Request / Response) et d'exécuter des tests directement depuis votre navigateur.

---

## 🗄️ Architecture des Bases de Données & Migrations (Flyway)

La persistance des données repose sur une base relationnelle PostgreSQL, disponible sous deux modes parfaitement alignés :

1. **Base de Données Cloud (Neon PostgreSQL)** :
   - Hébergement serverless managé sur **Neon** avec connectivité SSL chiffrée.
   - Haute disponibilité, sauvegardes automatiques et redémarrage instantané.
2. **Base de Données Locale** :
   - Instance PostgreSQL locale accessible sur `jdbc:postgresql://localhost:5432/billetterie`.
3. **Migrations Automatisées par Flyway** :
   - [`V1.0.1__DDL.sql`](file:///home/yokan/TAF/FORMATION/TESTO/billetterie/src/main/resources/db/migration/V1.0.1__DDL.sql) : 
     - Création de 11 tables relationnelles : `statut`, `categorie`, `salle`, `evenement`, `place`, `client`, `tarif_evenement`, `reservation`, `paiement`, `remboursement`, `billet`.
     - Contraintes d'intégrité (clés uniques composite `uk_place_salle_rang_numero`, `uk_salle_nom_ville`, clés étrangères indexées).
     - **4 Vues SQL analytiques** pour les requêtes complexes :
       - `v_evenement_details` : Jointure optimisée événement / salle.
       - `v_client_stats` : Agrégats d'activité par client (CA brut, CA net, volume de réservations).
       - `vue_places_libres` (F17) : Décompte dynamique des places disponibles par catégorie.
       - `v_evenement_remplissage_categorie` (F18) : Calcul du taux d'occupation par catégorie et statut.
   - [`V1.0.2__DML.sql`](file:///home/yokan/TAF/FORMATION/TESTO/billetterie/src/main/resources/db/migration/V1.0.2__DML.sql) :
     - Initialisation des paramètres de référence (Statuts : `PROVISOIRE`, `PAYEE`, `ANNULEE`, `REMBOURSE` ; Catégories : `PLATINE`, `OR`, `ARGENT`, `STANDARD`).
     - Injection d'un jeu de données de test complet (500 salles, événements et réservations) et synchronisation des séquences `IDENTITY`.

---

## 📬 Pièce Jointe : Collection Postman Complète

Pour simplifier vos campagnes de tests et vos démonstrations, une collection Postman prête à l'emploi est mise à disposition à la racine du projet :

📄 **[`postman.json`](file:///home/yokan/TAF/FORMATION/TESTO/billetterie/postman.json)** *(également disponible sous `ZKANTAPI.postman_collection.json`)*

### 🚀 Importation et bascule en 1 clic :
1. Ouvrez votre client **Postman**.
2. Cliquez sur le bouton **Import** (en haut à gauche) et sélectionnez ou glissez-déposez le fichier **`postman.json`**.
3. **Bascule instantanée En Ligne / Local** grâce à la variable de collection `baseUrl` (ou `loc`) :
   - **Pour tester l'API déployée en ligne (Render)** :
     - Valeur de `baseUrl` : `https://billetterie-f33g.onrender.com`
   - **Pour tester l'API en local** :
     - Valeur de `baseUrl` : `http://localhost:8080`
4. L'arborescence des dossiers Postman couvre l'intégralité des 19 fonctionnalités :
   - **NIVEAU 1 — LE CRUD (F1 à F4)** : Création, lecture, mise à jour et suppression des Salles, Places, Événements et Réservations.
   - **NIVEAU 2 — CONSULTATION ET RECHERCHE (F5 à F10)** : Liste des places par salle, filtrage par catégorie, programmation de salle, recherche d'événements par plage horaire, pagination des réservations et recherche insensible à la casse par email.
   - **NIVEAU 3 — RÈGLES MÉTIER (F11 à F16)** : Rejet de double réservation (409 Conflict), cohérence salle/place (400), confirmation de paiement, annulation avec calcul du barème de remboursement et libération des réservations expirées.
   - **NIVEAU 4 — LA RESTITUTION (F17 à F19)** : Places libres par événement/catégorie, calcul du pourcentage de remplissage et export direct CSV.

---

## 🧪 Tests par Requêtes cURL (Ligne & Local)

Vous pouvez exécuter les commandes cURL ci-dessous indifféremment sur l'environnement distant ou en local en adaptant la variable `$BASE` :

```bash
# Cible En Ligne (Par défaut)
export BASE="https://billetterie-f33g.onrender.com"

# Cible Locale
# export BASE="http://localhost:8080"
```

### 1️⃣ Niveau 1 : Le CRUD

* **Lister les salles paginées (F1)** :
```bash
curl -X GET "$BASE/salles?page=0&size=5"
```

* **Créer une nouvelle salle (F1)** :
```bash
curl -X POST "$BASE/salles" \
     -H "Content-Type: application/json" \
     -d '{
       "nom": "Palais des Congrès",
       "ville": "Abidjan",
       "adresse": "Plateau",
       "capacite": 1200
     }'
```

* **Créer un événement dans le futur (F3)** :
```bash
curl -X POST "$BASE/evenements" \
     -H "Content-Type: application/json" \
     -d '{
       "titre": "Festival Jazz 2026",
       "description": "Grand festival international de Jazz",
       "horaire": "2026-12-20 20:30:00",
       "placementLibre": false,
       "salleId": 1
     }'
```

---

### 2️⃣ Niveau 2 : Consultation & Recherche

* **Lister les places d'une salle filtrées par catégorie OR (F5 & F6)** :
```bash
curl -X GET "$BASE/salles/1/places?categorie=OR"
```

* **Consulter la programmation des événements d'une salle (F7)** :
```bash
curl -X GET "$BASE/salles/1/evenements"
```

* **Rechercher des événements sur une période donnée (F8)** :
```bash
curl -G "$BASE/evenements/recherche" \
     --data-urlencode "du=01-01-2026 00:00:00" \
     --data-urlencode "au=31-12-2026 23:59:59" \
     --data-urlencode "page=0" \
     --data-urlencode "size=5"
```

* **Rechercher les réservations d'un client par email (F10)** :
```bash
curl -X GET "$BASE/reservations?email=client@exemple.ci"
```

---

### 3️⃣ Niveau 3 : Règles Métier

* **Test de la règle anti-double réservation (F11 - Attendu: 409 Conflict)** :
```bash
curl -i -X POST "$BASE/reservations" \
     -H "Content-Type: application/json" \
     -d '{
       "clientNom": "Kante",
       "clientPrenom": "Dev",
       "clientEmail": "test@synelia.ci",
       "clientTelephone": "0102030405",
       "evenementId": 1,
       "placeId": 1,
       "montantTotal": 25000.00
     }'
```

* **Confirmer le paiement d'une réservation (F14 - PROVISOIRE ➔ PAYEE)** :
```bash
curl -X POST "$BASE/reservations/1/paiement"
```

* **Annuler une réservation avec barème de remboursement automatique (F15)** :
```bash
curl -X POST "$BASE/reservations/1/annulation"
```

---

### 4️⃣ Niveau 4 : Restitution & Décisionnel

* **Consulter les places disponibles groupées par catégorie (F17)** :
```bash
curl -X GET "$BASE/evenements/1/places-libres"
```

* **Calculer le taux de remplissage global et détaillé par catégorie (F18)** :
```bash
curl -X GET "$BASE/evenements/1/remplissage"
```

* **Exporter les réservations d'un événement au format CSV (F19)** :
```bash
# Téléchargement direct du fichier CSV
curl -X GET "$BASE/evenements/1/reservations/export" -o reservations_evenement_1.csv

# Téléchargement en incluant les réservations annulées
curl -X GET "$BASE/evenements/1/reservations/export?inclureAnnulees=true" -o reservations_completes.csv
```

---

## 📁 Dossier de Scripts & Tests Dédié (`test-docs/`)

Conformément aux bonnes pratiques de versionnement, l'ensemble des fichiers markdown détaillés et scripts shell exécutables de test ont été regroupés dans un dossier isolé :

📂 **`test-docs/`**
- `curl_billetterie_niveaux.sh` : Script shell exécutable avec coloration syntaxique permettant de dérouler tous les tests d'intégration en continu.
- `Tests_CURL_Billetterie.md` : Guide exhaustif au format markdown des requêtes, structures JSON et codes HTTP.

> 🔒 **Configuration `.gitignore`** :  
> Le dossier `test-docs/` est expressément exclu du suivi Git via le fichier [`.gitignore`](file:///home/yokan/TAF/FORMATION/TESTO/billetterie/.gitignore) afin de garantir la propreté du référentiel source tout en restant disponible pour vos tests locaux.

---

## 🛠️ Installation & Démarrage du Projet

### Prérequis
- **Java JDK 21**
- **Maven 3.9+** (ou utilisation du wrapper `./mvnw`)
- **PostgreSQL 15+** (ou compte Neon Cloud)
- **Docker** (optionnel)

### Configuration Locale (`application.properties`)
```properties
server.port=${PORT:8080}
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/billetterie}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.flyway.enabled=true
```

### Démarrage avec le Wrapper Maven
```bash
# Compilation et lancement de l'application Spring Boot
./mvnw clean spring-boot:run
```

### Déploiement Conteneurisé avec Docker
Un [`Dockerfile`](file:///home/yokan/TAF/FORMATION/TESTO/billetterie/Dockerfile) multi-étape basé sur OpenJDK 21 est inclus :
```bash
# Construction de l'image Docker
docker build -t billetterie-app .

# Lancement du conteneur avec variables de connexion
docker run -p 8080:8080 \
  -e PORT=8080 \
  -e DB_URL="jdbc:postgresql://ep-...neon.tech/billetterie?sslmode=require" \
  -e DB_USERNAME="your_username" \
  -e DB_PASSWORD="your_password" \
  billetterie-app
```

---

## 🏛️ Structure du Projet

```
billetterie/
├── Dockerfile                        # Définition conteneur Docker OpenJDK 21
├── pom.xml                           # Dépendances Maven (Spring Boot 3, Flyway, SpringDoc)
├── postman.json                      # 📬 Collection Postman complète importable (pièce jointe)
├── ZKANTAPI.postman_collection.json  # Collection Postman multi-projets
├── test-docs/                        # 🧪 Fichiers de test cURL et documentation (ignoré par Git)
│   ├── curl_billetterie_niveaux.sh
│   └── Tests_CURL_Billetterie.md
└── src/
    └── main/
        ├── java/com/billetterie/billetterie/
        │   ├── controller/           # Contrôleurs REST (Salles, Places, Evenements, Reservations)
        │   ├── dto/                  # DTOs Request & Response
        │   ├── entity/               # Entités JPA & Vues SQL @Immutable
        │   ├── exception/            # Gestion globale des exceptions (GlobalException)
        │   ├── repository/           # Interfaces Spring Data JPA
        │   └── service/              # Couche Métier & Règles de gestion
        └── resources/
            ├── application.properties
            └── db/migration/         # Scripts Flyway (V1.0.1__DDL.sql, V1.0.2__DML.sql)
```
