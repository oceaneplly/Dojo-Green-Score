#  DE04/DE05 Synchronisation Delta

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
   - Ajouter un **timestamp** ou un **version number** à chaque livre
   - Le serveur compare le `lastModified` des livres avec le timestamp de la dernière synchronisation du client
   - Le serveur retourne uniquement les livres modifiés après cette date

3. **Quelles opérations doivent être synchronisées ?**
   - **Additions** : nouveaux livres ajoutés
   - **Updates** : livres modifiés

---

## 🛠️ Étape 2 : Implémenter la synchronisation Delta

### Modification du modèle Book

Vous devez ajouter un champ `lastModified` pour tracker les modifications :
- `lastModified` : timestamp de la dernière modification

### Modification du BookController

Vous devez modifier la méthode `getBooks()` pour :
- Accepter un paramètre optionnel avec le timestamp de la dernière synchronisation du client
- Retourner une structure enrichie contenant :
  - `added` : nouveaux livres (ceux dont `lastModified` n'existait pas)
  - `updated` : livres modifiés (ceux dont `lastModified` est après la dernière synchronisation)
  - `deleted` : IDs des livres supprimés depuis la dernière synchronisation
  - `timestamp` : timestamp du serveur pour la prochaine requête

### Modification du BookRepository

Votre repository doit pouvoir retourner les livres modifiés après une date spécifique.

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

1. **Comparez la 1ère synchronisation (tous les livres) avec les syncs suivantes (Delta uniquement)**
2. **Quel est le ratio de réduction ?** (généralement 70-90% après la première sync)
3. **Le temps de réponse est-il amélioré ?**

---

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] Le modèle Book contient un champ `lastModified` 
- [ ] L'endpoint `/books` accepte un paramètre optionnel avec le timestamp de la dernière synchronisation
- [ ] Sans paramètre, tous les livres sont retournés (première synchronisation)
- [ ] La réponse inclut un `timestamp` pour la prochaine synchronisation
- [ ] La taille du payload est drastiquement réduite pour les syncs suivantes
- [ ] Les tests unitaires passent sans erreur

---

Une fois cet exercice fini, vous pouvez vous rendre sur le fichier `Exercice6_PartialContent.md'`.



