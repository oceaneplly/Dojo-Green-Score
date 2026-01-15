#  DE06/US04 Synchronisation Delta

---

## 📚 Contexte

Actuellement, l'endpoint `GET /books` de l'API optimisée retourne **toujours l'ensemble des données**, même si une grande partie n'a pas changé depuis la dernière synchronisation du client. Cela peut être problématique si :
- Le client récupère régulièrement l'intégralité des données
- Seule une petite partie a changé depuis la dernière requête
- Les données transférées contiennent surtout du contenu inchangé

Votre mission : **Implémenter la synchronisation Delta** pour retourner uniquement les modifications (ajouts, suppressions, mises à jour) depuis la dernière synchronisation.

---

## 🔍 Étape 1 : Comprendre le besoin

### ❓ Questions de réflexion

1. **Qu'est-ce qu'une synchronisation Delta ?**
   - Au lieu de retourner toutes les données, retourner uniquement les **modifications** depuis la dernière requête
   - Permet au client de maintenir un état local à jour sans retélécharger les données inchangées

2. **Comment tracker les modifications ?**
   - Ajouter un **timestamp** à chaque livre dans le champ `lastModified`
   - Le serveur compare le `lastModified` des livres avec le timestamp de la dernière synchronisation du client
   - Le serveur retourne uniquement les livres modifiés après cette date
   - **Formule** : `livres retournés = livres avec lastModified > timestamp du client`

3. **Quelles opérations doivent être synchronisées ?**
   - **Additions** : nouveaux livres ajoutés (lastModified = moment de création)
   - **Updates** : livres modifiés (lastModified = moment de la modification)
   - **Suppressions** : non gérées dans cet exercice (optionnel avancé)

4. **Cas d'usage pratique**
   - **Premier appel** : `/books?page=0&size=20` → client reçoit tous les livres avec leurs timestamps
   - **Appels suivants** : `/books/delta?timestamp=1705329600000` → client reçoit UNIQUEMENT les livres modifiés après ce timestamp
   
---

## 🛠️ Étape 2 : Implémenter la synchronisation Delta

### ⚠️ À FAIRE : Créer les endpoints PUT et GET /books/delta

**Les deux endpoints suivants doivent être implémentés dans `BookController.java` :**

### 1️⃣ Créer un endpoint PUT pour modifier les livres


Créez une nouvelle route `PUT /books/{id}` pour :
- Récupérer le livre avec l'ID spécifié
- Permettre la modification de **TOUS les champs** du livre (title, author, published_date, pages, summary)
- **Mettre à jour automatiquement** le champ `lastModified` avec le timestamp actuel lors de la modification
- Retourner **200 OK** avec le livre modifié

Exemple de requête :
```json
PUT /books/1
Content-Type: application/json

{
"title": "Nouveau titre",
"author": "Nouvel auteur",
"published_date": 2025,
"pages": 350,
"summary": "Nouveau résumé"
}
```

⚠ Vous devez aussi ajouter un champ `lastModified` à l'objet ```Book``` pour tracker les modifications :
- `lastModified` : **timestamp** de la dernière modification, automatiquement défini à `System.currentTimeMillis()`

### 2️⃣ Créer un endpoint GET pour récupérer les modifications (Delta)

Créez une nouvelle route `GET /books/delta?timestamp=TIMESTAMP` pour :
- Accepter un paramètre `timestamp` (**requis**)
- Retourner **UNIQUEMENT les livres** qui ont un champ `lastModified` **plus récent** que le timestamp fourni
- Formule : `livres avec lastModified > timestamp`
- Retourner une **liste vide** si aucun livre n'a été modifié après ce timestamp
- Retourner **200 OK** avec la liste des livres modifiés

Exemple de requête :
```
GET /books/delta?timestamp=1705329600000
```

**Cas d'usage :**
1. **Premier appel** : Le client appelle `/books` pour récupérer tous les livres (il note le timestamp courant : T0)
2. **Appels suivants** : Le client appelle `/books/delta?timestamp=T0` pour récupérer uniquement les livres modifiés depuis T0

### Modification du BookRepository

Votre repository doit pouvoir :
- Retourner les livres modifiés après une date spécifique.

---

## 🧪 Étape 3 : Lancer les tests unitaires

Des tests unitaires automatisés ont été créés pour valider votre implémentation. Voici comment les lancer :

### Lancer tous les tests

Depuis le dossier `green-api-optimized`, exécutez dans un terminal (ou via clic droit dans l'IDE - Run tests 👀) :

```bash
cd .\green-api-optimized\
mvn test -Dtest=BookControllerDeltaTest
```

Suite à votre implémentation, ces tests doivent passer sans erreur pour valider que la synchronisation Delta fonctionne correctement. S'il y a des erreurs, n'hésitez pas à améliorer l'implémentation actuelle.

---

## 📏 Étape 4 : Mesurer les améliorations

Utilisez le script `exercice5.sh` dans le dossier `scripts/` pour mesurer l'impact de la synchronisation Delta sur la taille du payload et le temps de réponse.
Exécutez-le comme suit :

```bash
cd scripts
bash exercice5.sh
```

⚠️ Si jamais vous avez des soucis d'exécution des scripts dans l'IDE, vous pouvez utiliser Git Bash ou WSL (sinon bonne chance pour installer bash 😶)

**Étapes du test :**
1. **1ère synchronisation** : Appelle `/books?page=0&size=20` → récupère tous les livres
2. **Modification** : Modifie un livre via `PUT /books/1`
3. **2ème synchronisation (Delta)** : Appelle `/books/delta?timestamp=T0` → récupère UNIQUEMENT les livres modifiés après T0
4. **Comparaison** :
   - Taille du payload 1ère sync vs 2ème sync
   - Quel est le ratio de réduction ?
   - Le temps de réponse est-il amélioré ?

---

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] Le modèle Book contient un champ `lastModified`
- [ ] L'endpoint `PUT /books/{id}` permet de modifier TOUS les champs du livre (title, author, published_date, pages, summary)
- [ ] La route `PUT /books/{id}` met à jour automatiquement le champ `lastModified` avec `System.currentTimeMillis()`
- [ ] La route `PUT /books/{id}` retourne **200 OK** avec le livre modifié
- [ ] L'endpoint `GET /books/delta?timestamp=TIMESTAMP` existe
- [ ] Sans paramètre timestamp, l'endpoint retourne une liste vide
- [ ] Avec un ancien timestamp, l'endpoint retourne **TOUS les livres** modifiés après cette date
- [ ] Avec un futur timestamp, l'endpoint retourne une **liste vide** (aucune modification future)
- [ ] Après une modification via `PUT`, l'endpoint delta détecte le changement
- [ ] La réponse Delta est une **liste vide `[]`** si aucune modification
- [ ] Les tests unitaires `BookControllerDeltaTest` passent sans erreur

---

Félicitations ! 🎉 Vous avez implémenté toutes les règles proposées dans cet atelier :

✅ Exercice 1 : Pagination
✅ Exercice 2 : Filtrage des champs
✅ Exercice 3 : Compression Gzip
✅ Exercice 4 : HTTP Cache (ETag/304)
✅ Exercice 5 : Synchronisation Delta
