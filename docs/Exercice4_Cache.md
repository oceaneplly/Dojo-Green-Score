# DE02/DE03 HTTP Cache (ETag/304)

---

## 📚 Contexte

Actuellement, l'endpoint `GET /books` de l'API optimisée retourne **toujours** les données complètes, même si le contenu n'a pas changé depuis la dernière requête. Cela peut être problématique si :
- Le client effectue plusieurs requêtes identiques
- Les données n'ont pas changé entre deux requêtes
- Le réseau et le serveur consomment de l'énergie pour retransmettre les mêmes données

Votre mission : **Implémenter le HTTP Cache avec ETag** pour éviter les retransmissions inutiles de contenu inchangé.

---

## 🔍 Étape 1 : Comprendre le besoin

### ❓ Questions de réflexion

1. **Qu'est-ce qu'un ETag ?**
   - Un "Entity Tag" est un identifiant unique représentant l'état d'une ressource
   - Il change uniquement si le contenu de la ressource change
   - Permet au serveur et au client de vérifier si une ressource a été modifiée

2. **Comment fonctionne le mécanisme ETag/304 ?**
   - **1ère requête** : Le serveur retourne les données avec un ETag (ex: `ETag: "abc123"`)
   - **Client** : Stocke le contenu et l'ETag
   - **2ème requête** : Le client envoie `If-None-Match: "abc123"`
   - **Serveur** : Compare l'ETag. Si identique → retourne **304 Not Modified** (sans données)

3. **Quel est l'impact ?**
   - Réduction drastique du trafic réseau (304 = quelques dizaines d'octets au lieu de plusieurs KB/MB)
   - Moins de charge serveur (pas besoin de retraiter les données)
   - Meilleure expérience utilisateur (réponse quasi-instantanée)

---

## 🛠️ Étape 2 : Implémenter le HTTP Cache

### Modification du BookController

Vous devez modifier la méthode `getBooks()` pour :
- Générer un **ETag** basé sur le contenu des données
- Supporter l'en-tête `If-None-Match` dans la requête
- Retourner **304 Not Modified** si l'ETag correspond
- Retourner **200 OK** avec les données si l'ETag est différent

---

## 🧪 Étape 3 : Lancer les tests unitaires

Des tests unitaires automatisés ont été créés pour valider votre implémentation. Voici comment les lancer :

### Lancer tous les tests

Depuis le dossier `green-api-optimized`, exécutez dans un terminal (ou via clic droit dans l'IDE - Run tests 👀) :

```bash
cd .\green-api-optimized\
mvn test -Dtest=BookControllerCacheTest
```

Suite à votre implémentation, ces tests doivent passer sans erreur pour valider que le cache HTTP fonctionne correctement. S'il y a des erreurs, n'hésitez pas à améliorer l'implémentation actuelle.

---

## 📏 Étape 4 : Mesurer les améliorations

Utilisez le script `exercice4.sh` dans le dossier `scripts/` pour mesurer l'impact du cache HTTP sur la taille du payload et le temps de réponse.
Exécutez-le comme suit :

```bash
cd scripts
bash exercice4.sh
```

⚠️ Si jamais vous avez des soucis d'exécution des scripts dans l'IDE, vous pouvez utiliser Git Bash ou WSL (sinon bonne chance pour installer bash 😶) 

1. **Comparez la 1ère requête (200 OK) avec la 2ème requête (304 Not Modified)**
2. **Quelle est la réduction de taille ?** (devrait être ~99% pour un 304)
3. **Le temps de réponse est-il amélioré ?**

---

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] L'endpoint `/books` retourne un en-tête `ETag` dans la réponse
- [ ] Une requête avec `If-None-Match` correspondant à l'ETag retourne **304 Not Modified**
- [ ] Une requête avec `If-None-Match` différent retourne **200 OK** avec les données
- [ ] La réponse 304 ne contient pas de body (taille = 0 bytes)
- [ ] Les tests unitaires passent sans erreur

---

## 🎓 Pour aller plus loin 🔍

Si vous avez le temps, vous pouvez :

1. **Ajouter des directives Cache-Control** pour optimiser davantage :
   ```
   Cache-Control: max-age=3600, public
   ```

2. **Implémenter Last-Modified** en complément de l'ETag :
   ```
   Last-Modified: Tue, 14 Jan 2026 10:00:00 GMT
   If-Modified-Since: Tue, 14 Jan 2026 10:00:00 GMT
   ```

3. **Combiner avec la pagination et le filtrage** pour maximiser les bénéfices

---

Une fois cet exercice fini, vous pouvez vous rendre sur le fichier `Exercice5_Delta.md'`.

