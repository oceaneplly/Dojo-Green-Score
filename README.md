# Green API Workshop (Java Spring Boot)

Atelier pratique pour réduire l'empreinte carbone des API HTTP en appliquant les règles de l'**API Green Score**.

## 📋 Objectifs

Cet atelier vous guide à travers l'implémentation de bonnes pratiques dans vos API REST.

### Règles Green Score à implémenter

- **DE11** - Pagination : Limiter le nombre de résultats par requête
- **DE08** - Filtrage : Réduire les données retournées selon les critères
- **US01** - Query params : Utiliser les paramètres de requête pour la navigation
- **DE01/USXX** - Compression (Gzip) : Compresser les réponses HTTP
- **DE02/DE03** - HTTP Cache (ETag/304) : Implémenter le caching côté client
- **DE06/US04** - Delta (changes since) : Retourner uniquement les modifications
- **206** - Partial Content (Range) : Supporter les requêtes partielles
- **LO01** - Useful logs : Journaliser efficacement sans excès
- **US07** - Error monitoring : Surveiller les erreurs de manière efficace

## 🗂️ Structure du projet

```
Dojo-Green-Score/
├── green-api-baseline/          # Implémentation naïve (point de référence)
│   ├── src/main/java/           # Code source Java
│   ├── src/main/resources/       # Configuration et ressources
│   └── pom.xml                  # Dépendances Maven
│
├── green-api-optimized/          # Implémentation optimisée
│   ├── src/main/java/           # Code source Java avec optimisations
│   ├── src/main/resources/       # Configuration optimisée
│   └── pom.xml                  # Dépendances Maven
│
├── scripts/                      # Scripts pour tester et mesurer
├── MAPPING.md                   # Mappage des règles Green Score
├── WORKSHOP.md                  # Guide détaillé de l'atelier
└── README.md                    # Ce fichier
```

### Modules principaux

- **`baseline/`**: API basique sans optimisations / sert de point de référence pour mesurer les améliorations
- **`optimized/`**: API de travail / le but est d'avoir version optimisée avec les règles à implémenter
- **`scripts/`**: Contient les scripts `curl` pour comparer les performances **avant/après**

### Prérequis
- Java 21 (JDK)
- Maven 3.9.x
- 3 terminaux distincts (ou 1 avec gestionnaire de multi-fenêtres)

Pour commencer le DOJO, allez dans le dossier `docs/` et suivez les instructions dans le fichier `Exercice0_Installation.md` 🎉