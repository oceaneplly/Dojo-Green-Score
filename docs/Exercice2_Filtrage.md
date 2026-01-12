# DE08 Filtrage des champs

---

## 📚 Contexte

Actuellement, l'endpoint `GET /books` retourne **tous les champs** de chaque livre. Cela peut être problématique si :
- Les champs contiennent des données volumineuses (résumé long, contenu, images)
- Le client n'a besoin que de quelques informations (id, title)
- Les données transférées deviennent massives sans raison

Votre mission : **Implémenter le filtrage des champs** pour permettre au client de demander uniquement les données dont il a besoin.

---

## 🔍 Étape 1 : Comprendre le besoin

### ❓ Questions de réflexion

1. **Quels champs doivent être retournés par défaut ?**
   - Les champs "légers" : id, title
   - Les champs "lourds" : summary, content (à exclure par défaut)

2. **Comment le client va-t-il demander des champs spécifiques ?**

---

## 🛠️ Étape 2 : Implémenter le filtrage

### Modification du BookController

Vous devez modifier la méthode `getBooks()` pour :
- Accepter un paramètre optionnel `fields` 
- Valider les champs demandés
- Retourner une liste de livres avec seulement les champs sélectionnés

---

## 🧪 Étape 3 : Lancer les tests unitaires

Des tests unitaires automatisés ont été créés pour valider votre implémentation. Voici comment les lancer :

### Lancer tous les tests

Depuis le dossier `green-api-optimized`, exécutez dans un terminal :

```bash
cd .\green-api-optimized\
mvn test -Dtest=BookControllerFilteringTest
```

Suite à votre implémentation, ces tests doivent passer sans erreur pour valider que le filtrage fonctionne correctement.

---

## 📏 Étape 4 : Mesurer les améliorations

Utilisez le script `exercice2.sh` dans le dossier `scripts/` pour mesurer l'impact du filtrage sur la taille du payload et le temps de réponse.
Exécutez-les comme suit :

```bash
cd scripts # si vous n'êtes pas déjà dans ce dossier 
bash exercice2.sh
```

Comparez la taille avec une requête sans filtrage.

---

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] L'endpoint `/books` accepte un paramètre optionnel `fields`
- [ ] Sans paramètre, les champs par défaut sont retournés (id, title)
- [ ] Avec le paramètre `fields`, seuls les champs demandés sont retournés
- [ ] Les champs sensibles/lourds sont exclus par défaut
- [ ] La taille du payload est réduite comparée à la baseline 
- [ ] Les champs invalides sont rejetés ou ignorés gracieusement
- [ ] Les tests unitaires passent sans erreur

---

Une fois cet exercice fini, vous pouvez vous rendre sur le fichier `Exercice3_Compression.md`.
