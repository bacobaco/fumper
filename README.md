# Fumper (v2.0)

Jeu physique arcade où vous devez propulser des fruits depuis une bascule jusque dans un panier, développé avec **LibGDX** et le moteur physique **Box2D**.

---

## 🎮 Versions disponibles (v2.0)

La version **2.0** étend Fumper sur 3 plateformes modernes :

| Plateforme | Projet | Description | Lancement rapide |
|---|---|---|---|
| 📱 **Android** | `fumper-android` | Application Android tactile et accéléromètre (APK). | `install-apk.bat` |
| 💻 **PC / Desktop** | `fumper-desktop` | Version de bureau (Windows, Linux, macOS). | `run.bat` |
| 🌐 **Web HTML5** | `fumper-html` | Version Web jouable directement dans le navigateur. | `run-html.bat` |

> [!WARNING]
> **Note sur la version HTML5 :**
> La version Web HTML5 fonctionne dans le navigateur via GWT/Canvas, mais présente encore des bugs physiques (collisions avec des obstacles invisibles déviant parfois la chute des fruits) qui seront corrigés dans une mise à jour ultérieure.

---

## ✨ Nouveautés de la Version 2.0

- **Nouveau système de High Score en ligne (REST Cloud API) :**
  - Remplacement de l'ancien système FTP Free.fr par une API REST JSON moderne, sécurisée et universelle pour toutes les plateformes.
  - Synchronisation automatique du meilleur score éternel et du nom du joueur.
- **Système de score progressif :**
  - Chaque fruit dans le panier rapporte désormais un nombre de points proportionnel au niveau en cours (X points au niveau X).
- **Mode Invincible secret :**
  - Cliquez 3 fois sur l'oiseau pour activer / désactiver le mode invincible.
  - En mode invincible, les fruits manqués n'entraînent pas de Game Over.
  - Aucun point n'est marqué en mode invincible pour préserver l'intégrité des High Scores.
  - Indicateur visuel clignotant `invincible` en bas à gauche de l'écran.
- **Améliorations physiques et visuelles :**
  - Diversité dans la chute des fruits depuis le feuillage de l'arbre.
  - Calibrage du HUD (Score, Level, High Score local et Web) adapté aux écrans modernes.
  - Déverrouillage automatique du Web Audio sur navigateur lors du premier geste utilisateur.
- **Modernisation du build & outillage :**
  - Compatibilité Gradle moderne et JDK 17/21.
  - Scripts batch d'automatisation pour build et exécution en un clic.

---

## 🚀 Guide d'utilisation et d'exécution

### 1. Version PC (Desktop)
Pour lancer directement le jeu sur PC :
```cmd
run.bat
```
Ou via Gradle :
```cmd
gradlew.bat :fumper-desktop:run
```

### 2. Version Web HTML5
Pour compiler le code Java en JavaScript (GWT) et générer la version Web :
```cmd
build-html.bat
```
Pour démarrer le serveur Web local et jouer dans votre navigateur :
```cmd
run-html.bat
```
Puis ouvrez votre navigateur sur [http://localhost:8085/](http://localhost:8085/).

### 3. Version Android
Pour compiler l'APK de débogage :
```cmd
build-apk.bat
```
Pour installer et lancer l'APK directement sur un smartphone connecté en USB (ADB) :
```cmd
install-apk.bat
```

---

## 📜 Historique des versions

- **v2.0 (2026)** :
  - Support multiplateforme complet : Android, PC Desktop et Web HTML5.
  - High Score mondial via API REST Cloud (adieu le FTP).
  - Mode invincible (triple-clic sur l'oiseau).
  - Score progressif par niveau et améliorations physiques.
  - Scripts d'automatisation de build et d'exécution.
- **v1.05-arthur** : Ajout du déplacement du panier via les accéléromètres.
- **v1.04-clem** : Modification de l'apparition des fruits et limitation du stick sur la bascule.
- **v1.03** : Première version officielle publiée sur Google Play.
- **v1.0** : Version initiale du jeu par Python4D / BacoLand.
