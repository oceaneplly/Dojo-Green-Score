# Green API Workshop (Java Spring Boot)

Atelier pratique pour réduire l'empreinte carbone des API HTTP en appliquant les règles de l'**API Green Score**.

## 📋 Objectifs

Cet atelier vous guide à travers l'implémentation de bonnes pratiques dans vos API REST.

### Règles de l'API Green Score à implémenter

- **DE11** - Pagination : Limiter le nombre de résultats par requête
- **DE08** - Filtrage : Réduire les données retournées selon les champs renseignés
- **DE01/USXX** - Compression (Gzip) : Compresser les réponses HTTP
- **DE02/DE03** - HTTP Cache (ETag/304) : Implémenter le caching côté client
- **DE06/US04** - Delta (changes since) : Retourner uniquement les dernières données modifiées 

## 🗂️ Structure du projet

```
Dojo-Green-Score/
├── docs/                    # Documentation des 5 exercices et de l'installations + règles de l'API Green Score
├── green-api-baseline/      # API naïve (point de référence)
├── green-api-optimized/     # API optimisée avec implémentations (à la fin de l'atelier 😉)
├── scripts/                 # Scripts bash pour tester et mesurer
└── README.md                # Ce fichier
```

### Modules principaux

- **`green-api-baseline/`**: API basique sans optimisations / sert de point de référence pour mesurer les améliorations
- **`green-api-optimized/`**: API de travail / le but est d'avoir une version optimisée avec les règles à implémenter
- **`scripts/`**: Contient les scripts pour comparer les performances **avant/après** (entre les 2 modules ci-dessus)

### Prérequis
- Java 21 (JDK)
- Maven 3.9.x
- 3 terminaux distincts (ou 1 avec gestionnaire de multi-fenêtres)

Pour commencer le DOJO, allez dans le dossier `docs/` et suivez les instructions dans le fichier `Exercice0_Installation.md` 🎉