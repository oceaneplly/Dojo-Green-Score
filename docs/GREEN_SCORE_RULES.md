# Guide Détaillé des Règles API Green Score

Ce document explique en détail chaque règle de l'API Green Score utile dans cet atelier.
Il y a également des pistes pour les implémenter afin de réduire l'empreinte carbone de vos API REST 😉

## Table des matières

1. [DE11 - Pagination](#de11---pagination)
2. [DE08 - Filtrage des champs](#de08---filtrage-des-champs)
3. [DE01/USXX - Compression Gzip](#de01usxx---compression-gzip)
4. [DE02/DE03 - HTTP Cache (ETag/304)](#de02de03---http-cache-etag304)
5. [DE06/US04 - Delta (changes since)](#de06us04---delta-changes-since)
6. [206 - Partial Content (Range)](#206---partial-content-range)


## Autres règles (non présentes dans cet atelier)
1. [US01 - Paramètres de requête GET](#us01---paramètres-de-requête-get)
2. [LO01 - Journalisation utile](#lo01---journalisation-utile)
3. [US07 - Surveillance des erreurs](#us07---surveillance-des-erreurs)
4. [AR02 - Proximité et efficacité](#ar02---proximité-et-efficacité)
---

## DE11 - Pagination

### 🎯 Objectif
Réduire le volume de données transféré en limitant le nombre de résultats retournés par requête.

### 📊 Impact environnemental
- **Réduction de la bande passante** : Au lieu de retourner 500,000 résultats, retourner un nombre limité (ex : 20-50)
- **Réduction de la RAM serveur** : Traitement de petites portions plutôt que l'intégralité des données
- **Réduction du temps de réponse** : Les données sont retournées plus rapidement
- **Réduction de la consommation réseau** : Moins de transfert = moins d'énergie

### 📋 Implémentation

**Paramètres recommandés :**
```
GET /books?page=1&size=20
GET /books?offset=0&limit=20
```

**Bonnes pratiques :**
- Limiter la taille par défaut (exemple : 20-50 éléments)
- Définir une taille maximale (exemple : size ≤ 100)
- Inclure les métadonnées de pagination dans la réponse

---

## DE08 - Filtrage des champs

### 🎯 Objectif
Retourner uniquement les champs nécessaires au client, en évitant de transférer des données inutiles.

### 📊 Impact environnemental
- Réduction du poids des données selon les champs sélectionnés
- Économie d'énergie due au transfert réseau réduit
- Amélioration de la latence** : moins de données = transmission plus rapide

### 📋 Implémentation

**Pattern recommandé :**
```
GET /books?fields=id,title,author
GET /books?select=id,title
GET /books?exclude=content,largeBinary
```

**Principe du whitelist :**
- Inclure par défaut les champs "légers" (id, titre, date)
- Exclure par défaut les champs "lourds" (description, contenu, image)
- Permettre au client de demander explicitement les champs additionnels

---

## DE01/USXX - Compression Gzip

### 🎯 Objectif
Compresser les réponses HTTP pour réduire le volume de données transféré sur le réseau.

### 📊 Impact environnemental
- Réduction du poids des données en transit
- Réduction de la latence réseau : Transmission plus rapide
- Économie d'énergie : Moins d'énergie pour les transferts réseau

### 📋 Implémentation (un petit indice au cas où)

**Configuration Spring Boot (application.yml) :**
```yaml
server:
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
    min-response-size: 1024  # Compresser si > 1KB
```

---

## DE02/DE03 - HTTP Cache (ETag/304)

### 🎯 Objectif
Implémenter le caching HTTP côté client pour éviter les retransmissions inutiles de contenu inchangé.

### 📊 Impact environnemental
- Élimination des transferts redondants : Réponses 304 (Not Modified) sans données
- Réduction du traffic pour le contenu statique
- Réduction majeure de la consommation d'énergie : Moins de transferts réseau
- Réduction de la charge serveur : Moins de requêtes à traiter

### 📋 Implémentation

**ETag - Entity Tag :**
```
Response (première requête) :
ETag: "abc123def456"
Cache-Control: max-age=3600, public

Request (requête suivante) :
If-None-Match: "abc123def456"

Response (si non modifié) :
304 Not Modified
(aucun corps de réponse)
```

**Fonctionnement :**
1. Le client reçoit une réponse avec un ETag
2. Le client stocke le contenu et l'ETag
3. Requête suivante : le client envoie l'ETag
4. Serveur compare : si identique → 304, pas de données transférées
5. Si modifié : 200 avec nouveau contenu et nouvel ETag

**Cache-Control directives :**
```
public        - Cacheable par proxies et clients
private       - Cacheable par clients uniquement
max-age=3600  - Valide pendant 3600 secondes
no-cache      - Valide, mais revalider auprès du serveur
no-store      - Ne pas cacher
must-revalidate - Revalider quand expiré
```

---

## DE06/US04 - Delta (changes since)

### 🎯 Objectif
Retourner uniquement les données modifiées depuis une date donnée, au lieu de tout l'historique.

### 📊 Impact environnemental
- Réduction drastique du volume : Transférer 10 changements au lieu de 10,000 enregistrements par exemple
- Réduction de la charge serveur : Requêtes plus rapides
- Réduction de la latence : Moins de données à traiter
- Économie de bande passante : Seulement ce qui a changé

### 📋 Implémentation

**Pattern de requête :**
```
GET /books/changes?since=2024-01-01T00:00:00Z
GET /books/delta?from=2024-01-01&to=2024-01-31
GET /books?modifiedSince=2024-01-01T00:00:00Z
```

**Réponse exemple :**
```json
{
  "changes": [
    {
      "id": 123,
      "action": "created",
      "timestamp": "2024-01-15T10:30:00Z",
      "book": { "id": 123, "title": "New Book" }
    },
    {
      "id": 456,
      "action": "updated",
      "timestamp": "2024-01-15T11:45:00Z",
      "book": { "id": 456, "title": "Updated Title" }
    },
    {
      "id": 789,
      "action": "deleted",
      "timestamp": "2024-01-15T12:00:00Z"
    }
  ],
  "lastModified": "2024-01-15T12:00:00Z"
}
```

---

## 206 - Partial Content (Range)

### 🎯 Objectif
Permettre aux clients de télécharger uniquement une partie du contenu en utilisant l'en-tête `Range`.

### 📊 Impact environnemental
- Réduction de la bande passante : Télécharger par morceaux
- Reprise de téléchargement : Redémarrer à partir du dernier octet téléchargé
- Économie sur mobile : Réseaux mobiles instables bénéficient des téléchargements partiels
- Parallélisation : Plusieurs connexions simultanées

### 📋 Implémentation

**Pattern de requête :**
```
Request:
GET /documents/large-file.pdf HTTP/1.1
Range: bytes=0-1023

Response (206 Partial Content):
HTTP/1.1 206 Partial Content
Content-Range: bytes 0-1023/10485760
Content-Length: 1024
Accept-Ranges: bytes

[données du fichier]
```

**Requête suivante :**
```
Request:
Range: bytes=1024-2047

Response:
HTTP/1.1 206 Partial Content
Content-Range: bytes 1024-2047/10485760
Content-Length: 1024
```
---

## 📚 Règles supplémentaires

## US01 - Paramètres de requête GET

### 🎯 Objectif
Utiliser les paramètres de requête (query strings) plutôt que les corps de requête pour les opérations de lecture, afin d'optimiser le caching.

### 📊 Impact environnemental
- **Réduction des transferts redondants** : Les caches évitent les requêtes identiques
- **Économie d'énergie** : Moins de requêtes au serveur = moins d'énergie consommée
- **Amélioration du caching** : Les proxies et CDN peuvent cacher les réponses GET 🤯

### 📋 Implémentation

**À faire ✅ :**
```
GET /books?title=Spring&author=John
GET /books?sortBy=date&order=desc
GET /books?minPrice=10&maxPrice=50
```

**À ne pas faire ❌ :**
```
POST /books/search
Content-Type: application/json

{
  "title": "Spring",
  "author": "John"
}
```

**Avantages des paramètres GET :**
- Cacheable par les proxies HTTP
- Partageables en URL
- Plus simples à déboguer

---
## LO01 - Journalisation utile

### 🎯 Objectif
Implémenter une journalisation efficace sans générer d'excès de logs qui consommeraient ressources et stockage.

### 📊 Impact environnemental
- **Réduction de l'I/O disque** : Moins de logs = moins d'écritures
- **Réduction du stockage** : Moins d'espace consommé
- **Réduction de la latence** : I/O disque réduit améliore la performance
- **Réduction de la consommation mémoire** : Moins de buffers de logs

### 📋 Implémentation

**Bonnes pratiques :**

1. **Niveaux de log appropriés :**
    - `ERROR` : Erreurs critiques uniquement
    - `WARN` : Conditions inhabituelles
    - `INFO` : Événements importants
    - `DEBUG` : Informations de débogage
    - `TRACE` : Très détaillé (rarement en production)

2. **Ne pas logger :**
    - Les mots de passe ou données sensibles
    - Tous les paramètres de requête (risque de PII)
    - Les corps entiers des requêtes
    - Chaque ligne d'exécution

3. **À logger :**
    - Les erreurs métier
    - Les appels APIs externes
    - Les performance metrics critiques
    - Les changements de données sensibles

---

## US07 - Surveillance des erreurs

### 🎯 Objectif
Implémenter une surveillance efficace des erreurs sans surcharger les systèmes avec des événements inutiles.

### 📊 Impact environnemental
- **Optimisation du monitoring** : Collecter uniquement les erreurs critiques
- **Réduction du stockage** : Moins d'événements = moins de données
- **Réduction de la latence** : Moins de traitements d'erreurs
- **Réduction de la bande passante** : Moins d'envois vers les services externes

### 📋 Implémentation

**Types d'erreurs à surveiller :**

1. **Erreurs système (500+)** : À surveiller impérativement
2. **Erreurs client (4xx)** : Pattern anomaux uniquement
3. **Timeouts** : Toujours surveiller
4. **Erreurs métier** : Selon la criticité

**Pattern recommandé :**
```
- Erreurs 500+ : Alerter immédiatement
- Erreurs 429 (Too Many Requests) : Alerter après seuil
- Erreurs 4xx : Logger en INFO, pas en ERROR
- Erreurs de validation : Ne pas signaler comme erreurs
```

---

## AR02 - Proximité et efficacité

### 🎯 Objectif
Optimiser la proximité des données et l'efficacité des requêtes pour réduire les temps de traitement.

### 📊 Impact environnemental
- **Réduction du temps de traitement** : Données plus proches
- **Réduction de la latence réseau** : Moins de sauts
- **Réduction de la consommation CPU** : Moins de jointures
- **Optimisation globale** : Énergie réduite pour le traitement

### 📋 Implémentation

**Stratégies :**

1. **Batch Processing** : Traiter les données par lots au lieu de requête par requête
2. **Keyset Pagination** : Utiliser des clés au lieu des offsets
3. **Connection Pooling** : Réutiliser les connexions BD
4. **Caching en mémoire** : Cache les données fréquemment accédées

---
