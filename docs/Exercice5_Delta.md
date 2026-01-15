#  DE06/US04 - Delta (changes since)

---

## 📚 Contexte

Actuellement, l'endpoint `GET /books` de l'API optimisée retourne **toujours l'ensemble des données**, même si une grande partie n'a pas changé depuis la dernière synchronisation du client. Cela peut être problématique si :
- Le client récupère régulièrement l'intégralité des données
- Seule une petite partie a changé depuis la dernière requête
- Les données transférées contiennent surtout du contenu inchangé

Votre mission : **Implémenter la synchronisation Delta** pour retourner uniquement les modifications (ajouts, mises à jour) depuis la dernière synchronisation.

---

## 🔍 Étape 1 : Comprendre le besoin

### ❓ Questions de réflexion

1. **Qu'est-ce qu'une synchronisation Delta ?**
   - Au lieu de retourner toutes les données, retourner uniquement les **modifications** depuis la dernière requête
   - Permet au client de maintenir un état local à jour sans retélécharger les données inchangées

2. **Comment tracker les modifications ?**
   - Ajouter un **timestamp** (`lastModified`) à chaque livre
   - Le serveur compare le `lastModified` des livres avec le timestamp de la dernière synchronisation du client
   - Le serveur retourne uniquement les livres modifiés après cette date

---

## 🛠️ Étape 2 : Implémenter la synchronisation Delta

### Modification du modèle Book

Vous devez ajouter un champ `lastModified` pour tracker les modifications :
- `lastModified` : timestamp (en millisecondes) de la dernière modification
- Ce champ se met à jour **automatiquement** quand un champ du livre est modifié via les setters
- Utiliser Lombok (`@Data`, `@Setter(AccessLevel.NONE)`) pour générer les getters/setters

### Modification du BookController

Vous devez :
1. **Créer une route GET /books/delta** :
   - Accepte un paramètre `timestamp` (obligatoire)
   - Retourne **uniquement** les livres dont `lastModified > timestamp`
   - Retourne une liste vide `[]` si aucun livre n'a été modifié après le timestamp

3. **Créer une route PUT /books/{id}** :
   - Modifie les champs du livre fournis dans le body JSON
   - Met à jour automatiquement `lastModified` au moment de la modification
   - Retourne le livre modifié avec son nouveau `lastModified`

### Modification du BookRepository

Votre repository doit avoir une fonction qui retourne tous les livres modifiés après le timestamp donné

---

## 🧪 Étape 3 : Lancer les tests unitaires

Des tests unitaires automatisés ont été créés pour valider votre implémentation. Voici comment les lancer :

### Lancer tous les tests

Depuis le dossier `green-api-optimized`, exécutez :

```bash
cd .\green-api-optimized\
mvn test -Dtest=BookControllerDeltaTest
```

---

## 📏 Étape 4 : Mesurer les améliorations

Utilisez le script `exercice5.sh` dans le dossier `scripts/` pour mesurer l'impact de la synchronisation Delta sur la taille du payload.

Exécutez-le comme suit :

```bash
cd scripts
bash exercice5.sh
```
---

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] Le modèle Book contient un champ `lastModified` (timestamp)
- [ ] Chaque setter du Book appelle `updateLastModified()` pour mettre à jour automatiquement le timestamp
- [ ] La route PUT /books/{id} met à jour le livre et son `lastModified`
- [ ] L'endpoint `/books` retourne les livres avec le champ `lastModified`
- [ ] L'endpoint `/books/delta?timestamp=T` retourne uniquement les livres modifiés après le timestamp T
- [ ] Sans paramètre, tous les livres sont retournés (première synchronisation)
- [ ] La taille du payload est drastiquement réduite pour les syncs suivantes
- [ ] Les tests unitaires passent sans erreur
- [ ] Le script `exercice5.sh` montre bien la réduction de taille entre GET /books et GET /delta

---

Une fois cet exercice fini, vous pouvez vous rendre sur le fichier `Exercice6_PartialContent.md`.

