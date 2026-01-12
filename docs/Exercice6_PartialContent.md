# DE06 Partial Content (HTTP 206)

---

## 📚 Contexte

Actuellement, l'endpoint `GET /books` de l'API optimisée retourne **toujours la ressource complète**, même si le client ne demande qu'une partie (ex: une plage spécifique d'éléments). Cela peut être problématique si :
- Le client souhaite reprendre un téléchargement interrompu
- Le client veut récupérer une plage spécifique de données
- La bande passante est limitée et fragmentée
- Le client souhaite faire un streaming progressif

Votre mission : **Implémenter le Partial Content (HTTP 206)** pour permettre aux clients de demander des plages spécifiques de contenu.

---

## 🔍 Étape 1 : Comprendre le besoin

### ❓ Questions de réflexion

1. **Qu'est-ce que le HTTP 206 Partial Content ?**
   - Un code de réponse HTTP qui indique que la ressource est retournée partiellement
   - Le serveur retourne uniquement la plage demandée (spécifiée par l'en-tête `Range`)
   - Permet les téléchargements en fragments, les reprises et le streaming

2. **Comment fonctionnent les Range Requests ?**
   - **Client** : Envoie `Range: bytes=0-999` (demande les 1000 premiers bytes)
   - **Serveur** : Vérifie que `Accept-Ranges: bytes` est supporté
   - **Serveur** : Retourne **206 Partial Content** avec `Content-Range: bytes 0-999/5000`
   - **Client** : Récupère uniquement les données demandées

3. **Quels sont les cas d'usage ?**
   - **Reprises de téléchargement** : Reprendre un fichier après une interruption
   - **Streaming vidéo/audio** : Charger le contenu au fur et à mesure
   - **Pagination avancée** : Charger des sections spécifiques de données
   - **Téléchargement parallèle** : Plusieurs clients récupèrent différentes plages simultanément

---

## 🛠️ Étape 2 : Implémenter le Partial Content

### Modification du BookController

Vous devez modifier la méthode `getBooks()` pour :
- Supporter l'en-tête `Range` dans la requête
- Valider la plage demandée (start <= end, end < total)
- Retourner **206 Partial Content** si une plage valide est demandée
- Ajouter l'en-tête `Accept-Ranges: bytes` pour indiquer le support
- Ajouter l'en-tête `Content-Range: bytes start-end/total` dans la réponse

### Détails d'implémentation

Formats de plage acceptés :
- `Range: bytes=0-999` : Les 1000 premiers bytes
- `Range: bytes=1000-` : Du byte 1000 jusqu'à la fin
- `Range: bytes=-500` : Les 500 derniers bytes
- `Range: bytes=0-999,2000-2999` : Plusieurs plages (optionnel, avancé)

Gestion des erreurs :
- Plage invalide → **416 Range Not Satisfiable**
- Format invalide → Ignorer et retourner la ressource complète (200 OK)

---

## 🧪 Étape 3 : Lancer les tests unitaires

Des tests unitaires automatisés ont été créés pour valider votre implémentation. Voici comment les lancer :

### Lancer tous les tests

Depuis le dossier `green-api-optimized`, exécutez dans un terminal (ou via clic droit dans l'IDE - Run tests 👀) :

```bash
cd .\green-api-optimized\
mvn test -Dtest=BookControllerPartialContentTest
```

Suite à votre implémentation, ces tests doivent passer sans erreur pour valider que le Partial Content fonctionne correctement. S'il y a des erreurs, n'hésitez pas à améliorer l'implémentation actuelle.

---

## 📏 Étape 4 : Mesurer les améliorations

Utilisez le script `exercice6.sh` dans le dossier `scripts/` pour mesurer l'impact du Partial Content sur la taille du payload et le temps de réponse.
Exécutez-le comme suit :

```bash
cd scripts
bash exercice6.sh
```

⚠️ Si jamais vous avez des soucis d'exécution des scripts dans l'IDE, vous pouvez utiliser Git Bash ou WSL (sinon bonne chance pour installer bash 😶) 

1. **Comparez une requête complète (200 OK) avec une requête partielle (206 Partial Content)**
2. **Quelle est la réduction de taille ?** (devrait être proportionnelle à la plage demandée)
3. **Le temps de réponse est-il amélioré pour les plages petites ?**

---

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] L'endpoint `/books` retourne `Accept-Ranges: bytes` dans tous les cas
- [ ] Une requête sans `Range` retourne **200 OK** avec toutes les données
- [ ] Une requête avec `Range: bytes=0-999` retourne **206 Partial Content**
- [ ] La réponse 206 contient l'en-tête `Content-Range: bytes 0-999/total`
- [ ] Le body de la réponse 206 ne contient que la plage demandée
- [ ] Une plage invalide retourne **416 Range Not Satisfiable**
- [ ] Le format `Range: bytes=start-` (jusqu'à la fin) fonctionne
- [ ] Le format `Range: bytes=-last` (derniers N bytes) fonctionne
- [ ] Les tests unitaires passent sans erreur
---

## 🏆 Vous avez terminé ! 

Félicitations ! 🎉 Vous avez implémenté toutes les règles proposées dans cet atelier :

- ✅ Exercice 1 : Pagination
- ✅ Exercice 2 : Filtrage des champs
- ✅ Exercice 3 : Compression Gzip
- ✅ Exercice 4 : HTTP Cache (ETag/304)
- ✅ Exercice 5 : Synchronisation Delta
- ✅ Exercice 6 : Partial Content (HTTP 206)