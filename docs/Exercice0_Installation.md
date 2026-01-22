## 🚀 Instructions de démarrage

Vous pouvez lancer le projet via le terminal de commandes ou directement dans votre IDE.
Ces deux méthodes sont décrites ci-dessous.

### 1.1 Lancement des applications via le terminal
Avant de lancer les différentes applications, assurez-vous d'avoir la bonne version de Java configurée (Java 21).
Vous pouvez le faire en lançant cette commande :

```bash
java -version
```

Si vous ne pouvez pas changer votre JAVA_HOME globalement, vous pouvez lancer une version spécifique de Java en définissant la variable d'environnement dans un terminal (dans l'IDE de préférence) :

```bash
# Windows (PowerShell)
$env:JAVA_HOME="C:\chemin\vers\java21"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"

```

Ouvrez **2 terminaux** à la racine du projet et lancez les commandes suivantes :

**Terminal 1 - Lancer l'API baseline**
```bash
cd green-api-baseline
mvn clean install
mvn spring-boot:run
```
L'API sera disponible sur `http://localhost:8080/books`

**Terminal 2 - Lancer l'API optimisée**
```bash
cd green-api-optimized
mvn clean install -DskipTests
mvn spring-boot:run
```
L'API sera disponible sur `http://localhost:8081/books`


## 1.2 Lancement via l'IDE des applications

Si vous préférez lancer les applications directement depuis votre IDE (IntelliJ normalement) :

### Configuration du JDK dans l'IDE

1. **Configurer le SDK du projet** :
   - Allez dans `File > Project Structure > Project`
   - Définissez le **SDK** sur **Java 21**

2. **Pour chaque module** (green-api-baseline et green-api-optimized) :
   - Dans `Project Structure > Modules`
   - Vérifiez que le **Module SDK** est bien défini sur **Java 21**

### Build des modules Maven

Avant de lancer les applications, vous devez builder les projets :

1. **Ouvrir la vue Maven** :
   - Allez dans `View > Tool Windows > Maven` (ou cliquez sur l'onglet Maven sur le côté droit)

2. **Builder le module baseline** :
   - Dépliez `green-api-baseline > Lifecycle`
   - Double-cliquez sur `clean` puis sur `install`
   - Attendez que le build se termine avec succès

3. **Recommencez l'étape précédente pour le module optimisé** (`green-api-optimized`)
   - ⚠ Pour cette étape, il faudra skipper les tests (bouton interdit sur la vue Maven)

### Lancer l'application baseline

1. Naviguez vers `green-api-baseline/src/main/java/com/greenapi/baseline/BaselineApplication.java`
2. Clic droit sur la classe `BaselineApplication`
3. Sélectionnez `Run 'BaselineApplication'`
4. L'API sera disponible sur `http://localhost:8080/books`

### Lancer l'application optimisée

1. Naviguez vers `green-api-optimized/src/main/java/com/greenapi/optimized/OptimizedApplication.java`
2. Clic droit sur la classe `OptimizedApplication`
3. Sélectionnez `Run 'OptimizedApplication'`
4. L'API sera disponible sur `http://localhost:8081/books`

---
⚠ Si vous voulez regarder si vos applications fonctionnent, il est conseillé de faire ça directement sur un navigateur.
Bruno n'aime pas trop les grosses requêtes 🤔 
---
## 2. Lancement des scripts de mesure 

Lancez un terminal supplémentaire pour exécuter les scripts: 
```bash
cd scripts
bash basicCase.sh
```
Le script `basicCase.sh` lance une commande curl sur l'API baseline (http://localhost:8080/books) et affiche le code HTTP, le temps de réponse et la taille du payload.

⚠ Si vous avez des soucis avec `bash`, vous pouvez lancer les différents scripts sur Gitbash.

## 📊 Mesurer les améliorations

Les scripts fournis dans le dossier `scripts/` vous permettent de :
- Mesurer le temps de réponse, la taille du payload et avoir le code HTTP pour chaque règle implémentée
- Comparer les résultats avec le premier script `basicCase.sh` (API baseline) et les scripts des exercices (API optimisée)

Vous pouvez vous rendre sur le fichier `Exercice1_Pagination.md` pour faire le premier exercice de l'atelier. 
