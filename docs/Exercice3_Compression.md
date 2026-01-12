# DE01/USXX Compression Gzip

---

## 📚 Contexte

Actuellement, l'endpoint `GET /books` de l'API optimisée retourne des données JSON **non compressées**. Cela peut être problématique si :
- Les réponses contiennent beaucoup de données (même paginées)
- La bande passante réseau est limitée
- Le temps de transfert des données est long

Votre mission : **Activer la compression Gzip** pour réduire la taille des données transférées sur le réseau.

---

## 🔍 Étape 1 : Comprendre le besoin

### ❓ Questions de réflexion

1. **Qu'est-ce que la compression Gzip ?**

2. **Comment fonctionne la compression HTTP ?**
   - Le client envoie `Accept-Encoding: gzip` dans la requête
   - Le serveur compresse la réponse et ajoute `Content-Encoding: gzip`
   - Le client décompresse automatiquement la réponse

---

## 🛠️ Étape 2 : Implémenter la compression

### Modification de application.yml

Vous devez activer la compression pour l'API optimisée (bonne chance 😎)

---

## 🧪 Étape 3 : Lancer les tests unitaires

Des tests unitaires automatisés ont été créés pour valider votre implémentation. Voici comment les lancer :

### Lancer tous les tests

Depuis le dossier `green-api-optimized`, exécutez dans un terminal (ou via clic droit dans l'IDE - Run tests 👀) :

```bash
cd .\green-api-optimized\
mvn test -Dtest=BookControllerCompressionTest
```

Suite à votre implémentation, ces tests doivent passer sans erreur pour valider que la compression fonctionne correctement. S'il y a des erreurs, n'hésitez pas à améliorer l'implémentation actuelle.

---

## 📏 Étape 4 : Mesurer les améliorations

Utilisez le script `exercice3.sh` dans le dossier `scripts/` pour mesurer l'impact de la compression sur la taille du payload et le temps de réponse.
Exécutez-le comme suit :

```bash
cd scripts
bash exercice3.sh
```

⚠️ Si jamais vous avez des soucis d'exécution des scripts dans l'IDE, vous pouvez utiliser Git Bash ou WSL (sinon bonne chance pour installer bash 😶) 

1. **Comparez la taille avec et sans compression**
2. **Quel est le taux de compression obtenu ?** (généralement 60-80% pour du JSON)
3. **Le temps de réponse est-il amélioré ?**

---

## ✅ Checklist de validation

Avant de dire que vous avez terminé, vérifiez :

- [ ] La compression Gzip est activée dans le bon fichier
- [ ] Les requêtes avec `Accept-Encoding: gzip` reçoivent des réponses compressées
- [ ] L'en-tête `Content-Encoding: gzip` est présent dans les réponses compressées
- [ ] La taille du payload est réduite avec la compression
- [ ] Les requêtes sans `Accept-Encoding: gzip` fonctionnent toujours (non compressé)
- [ ] La compression ne s'applique que pour les réponses > 1KB
- [ ] Les tests unitaires passent sans erreur

---

Une fois cet exercice fini, vous pouvez vous rendre sur le fichier `Exercice3_Compression.md'`.

