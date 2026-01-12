## 🚀 Instructions de démarrage

Vous pouvez lancer le projet via le terminal de commandes ou via votre IDE;
Ces deux méthodes sont décrites ci-dessous.

### 1.1 Lancement des applications via le terminal
Avant de lancer les différentes applications, assurez-vous d'avoir la bonne version de Java configurée (Java 21).
Vous pouvez le faire en lançant cette commande :

```bash
java --version
```

Si vous ne pouvez pas changer votre JAVA_HOME globalement, vous pouvez configurer Maven pour utiliser une version spécifique de Java en définissant la variable d'environnement avant d'exécuter Maven :

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
L'API sera disponible sur `http://localhost:8080`

**Terminal 2 - Lancer l'API optimisée**
```bash
cd green-api-optimized
mvn clean install -DskipTests
mvn spring-boot:run
```
L'API sera disponible sur `http://localhost:8081`


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
4. L'API sera disponible sur `http://localhost:8080`

### Lancer l'application optimisée

1. Naviguez vers `green-api-optimized/src/main/java/com/greenapi/optimized/OptimizedApplication.java`
2. Clic droit sur la classe `OptimizedApplication`
3. Sélectionnez `Run 'OptimizedApplication'`
4. L'API sera disponible sur `http://localhost:8081`


## 2. Lancement des scripts de mesure 

Lancez un terminal supplémentaire pour exécuter les scripts: 
```bash
cd scripts
bash basicCase.sh
```

⚠ Si vous avez des soucis avec `bash`, vous pouvez lancer ces commandes sur GitBash.

## 📊 Mesurer les améliorations

Les scripts fournis dans le dossier `scripts/` vous permettent de :
- Comparer le temps de réponse entre la version baseline et optimisée pour chaque exercice
- Mesurer la taille des payloads

Vous pouvez vous rendre sur le fichier `Exercice1_Pagination.md` pour faire le premier exercice de l'atelier. 
