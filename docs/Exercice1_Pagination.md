# DE11 Pagination

---

## 📚 Contexte

Actuellement, l'endpoint `GET /books` de l'API optimisée retourne **tous** les livres de la base de données. Cela peut être problématique si :
- La base contient beaucoup de livres (500,000 dans cet exemple, sacrée bibliothèque🤓)
- Les données transférées deviennent massives (plusieurs MB)

Votre mission : **Implémenter la pagination** pour limiter le nombre de résultats par requête.
Toutes les implémentations devront être dans le module `green-api-optimized`. 

---

## 🔍 Étape 1 : Comprendre le besoin

### ❓ Questions de réflexion

1. **Quel est le nombre idéal de résultats par page ?**

2. **Comment le client va-t-il demander une page spécifique ?**

3. **Quelles informations faut-il retourner au client ?**

---

## 🛠️ Étape 2 : Implémenter la pagination

### Modification du BookController

Vous devez ajouter la méthode `getBooks()` pour :
- Accepter deux paramètres de requête : `page` et `size`
- Ajouter les validations appropriées 
- Retourner les résultats paginés

### Modification du BookRepository

Votre repository doit pouvoir retourner une page de résultats au lieu de la liste complète.

---

## 🧪 Étape 3 : Lancer les tests unitaires

Des tests unitaires automatisés ont été créés pour valider votre implémentation. Voici comment les lancer :

### Lancer tous les tests

Depuis le dossier `green-api-optimized`, exécutez dans un terminal (ou via clic droit dans l'IDE - Run tests 👀) :

```bash
cd .\green-api-optimized\
mvn test -Dtest=BookControllerPaginationTest
```

Suite à votre implémentation, ces tests doivent passer sans erreur pour valider que la pagination fonctionne correctement. S'il y a des erreurs, n'hésitez pas à améliorer l'implémentation actuelle.

---

## 📏 Étape 4 : Mesurer les améliorations

Utilisez les scripts `basicCase.sh` et `exercice1.sh` dans le dossier `scripts/` pour mesurer l'impact de votre pagination sur la taille du payload et le temps de réponse.
Exécutez-les comme suit :

```bash
cd scripts
bash basicCase.sh
bash exercice1.sh
```

⚠️ Si jamais vous avez des soucis d'exécution des scripts dans l'IDE, vous pouvez utiliser Git Bash ou WSL (sinon bonne chance pour installer bash 😶) 

**Que pensez-vous de ses résultats ?**

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] L'endpoint `/books` accepte les paramètres `page` et `size`
- [ ] Une pagination par défaut (page=0, size=20) fonctionne
- [ ] La taille du payload est réduite par rapport à la baseline 
- [ ] Le temps de réponse est amélioré
- [ ] Les paramètres invalides sont validés (size > 100 rejeté, etc.)
- [ ] Le script `exercice1.sh` passe sans erreur et retour une liste paginée 
- [ ] **Optionnel** : Les métadonnées de pagination sont retournées (totalElements, totalPages, etc.)

---

Une fois cet exercice fini, vous pouvez vous rendre sur le fichier `Exercice2_Filtrage.md`.


