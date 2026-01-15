# DE02/DE03 HTTP Cache (ETag/304)

---

## 📚 Contexte

Actuellement, l'API optimisée ne possède **pas d'endpoint pour récupérer un livre spécifique par son ID**. De plus, même si cet endpoint existait, il retournerait **toujours l'intégralité des données** d'un livre, même si le client l'a déjà en cache et que les données n'ont pas changé. Cela peut être problématique si :
- Le client demande plusieurs fois la même ressource
- Les données transférées sont inutiles car identiques à celles en cache
- La bande passante est consommée sans raison

Votre mission : **Créer l'endpoint `GET /books/{id}` et implémenter le mécanisme de cache HTTP avec ETags** pour éviter de retransférer des données non modifiées.

---

## 🔍 Étape 1 : Comprendre le besoin

### ❓ Questions de réflexion

1. **Qu'est-ce qu'un ETag ?**
   - Un identifiant unique généré par le serveur pour représenter l'état d'une ressource
   - Permet au client de vérifier si sa version en cache est toujours valide

2. **Comment fonctionne le mécanisme HTTP Cache ?**
   - **1ère requête** : Le serveur retourne **200 OK** avec les données et un header **ETag**
   - **Requêtes suivantes** : Le client envoie le header **If-None-Match** avec l'ETag
   - Si l'ETag correspond → Le serveur retourne **304 Not Modified** (pas de body)
   - Si l'ETag ne correspond pas → Le serveur retourne **200 OK** avec les nouvelles données

---

## 🛠️ Étape 2 : Implémenter le cache HTTP

### ⚠️ À FAIRE : Créer l'endpoint GET /books/{id}

**Cet endpoint doit être créé dans `BookController.java` :**

Créez une nouvelle route `GET /books/{id}` pour :
- Accepter un paramètre de chemin `{id}` pour identifier le livre
- Accepter un paramètre de header optionnel `If-None-Match`
- Récupérer le livre avec l'ID spécifié depuis le repository
- Retourner **404 Not Found** si le livre n'existe pas
- Générer un **ETag** basé sur le hash du contenu du livre
- Comparer l'ETag reçu avec l'ETag généré
- Retourner **304 Not Modified** si les ETags correspondent (sans body)
- Retourner **200 OK** avec les données et l'ETag si les ETags ne correspondent pas


### Modification du BookRepository

Votre repository doit pouvoir :
- Retourner un livre spécifique par son ID (`findById(long id)`)
- Retourner `null` si le livre n'existe pas

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

**Comparez les résultats :**
1. **1ère requête** : Le serveur retourne **200 OK** avec toutes les données et un header **ETag**
2. **2ème requête avec If-None-Match** : Le serveur retourne **304 Not Modified** sans body
3. **Économies** : Quelle est la réduction de la taille du payload ? (devrait être proche de 100% pour la 2ème requête)

---

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] L'endpoint `GET /books/{id}` existe et est fonctionnel
- [ ] L'endpoint `GET /books/{id}` retourne un header **ETag**
- [ ] Les requêtes avec `If-None-Match` reçoivent **304 Not Modified** si l'ETag correspond
- [ ] La réponse **304** ne contient **aucun body** (payload vide)
- [ ] Deux requêtes identiques retournent le **même ETag**
- [ ] Les requêtes avec un **ETag différent** retournent **200 OK** avec les données complètes
- [ ] Les IDs inexistants retournent **404 Not Found**
- [ ] La taille du payload est réduite pour les requêtes avec cache valide
- [ ] Les tests unitaires passent sans erreur

---

Une fois cet exercice fini, vous pouvez vous rendre sur le fichier `Exercice5_Delta.md`.

