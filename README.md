# Fumper (v2.0)

Jeu d'arcade physique où vous devez propulser des fruits depuis une bascule jusque dans un panier, développé avec **LibGDX** et le moteur physique **Box2D**.

---

## 🎮 Versions disponibles (v2.0)

La version **2.0** unifie et étend Fumper sur 3 plateformes modernes :

| Plateforme | Projet | Description | Lancement rapide |
|---|---|---|---|
| 📱 **Android** | `fumper-android` | Application Android tactile avec inclinaison par accéléromètre (APK). | `install-apk.bat` |
| 💻 **PC / Desktop** | `fumper-desktop` | Version de bureau plein écran ou fenêtrée (Windows, Linux, macOS). | `run.bat` |
| 🌐 **Web HTML5** | `fumper-html` | Version Web jouable directement dans n'importe quel navigateur moderne. | `run-html.bat` |

---

## 🕹️ Commandes & Contrôles

Le jeu offre une expérience de jeu parfaitement cohérente et harmonisée sur tous les supports :

| Action | 📱 Android | 💻 PC Desktop | 🌐 Web HTML5 |
|---|---|---|---|
| **Propulser la bascule** | Tap tactile sur le côté droit de la planche | Clic gauche de la souris sur la planche | Clic souris ou Touch tactile |
| **Orienter / Déplacer le panier** | Inclinaison du smartphone (**Accéléromètre matériel**) | Touches Fléchées (`←`, `→`, `↑`, `↓`) ou `ZQSD` / `WASD` | Touches Fléchées (`←`, `→`, `↑`, `↓`) ou `ZQSD` / `WASD` |
| **Mode Invincible secret** | 3 touchers rapides sur l'oiseau | 3 clics rapides sur l'oiseau | 3 clics ou touchers sur l'oiseau |
| **Quitter / Menu** | Bouton Retour Android | Touche `Échap` / Fermeture fenêtre | Fermeture onglet |

> [!TIP]
> **Simulation de l'accéléromètre au clavier (Desktop & Web) :**
> - `←` / `A` / `Q` : incline le panier vers la gauche
> - `→` / `D` : incline le panier vers la droite
> - `↑` / `W` / `Z` : incline vers l'arrière pour monter le panier
> - `↓` / `S` : incline vers l'avant pour descendre le panier
> - Relâché : retour progressif et fluide à la position neutre de repos.

---

## ✨ Nouveautés de la Version 2.0

- **Physique Box2D unifiée et dynamisée :**
  - Moteur physique cadencé par accumulateur à pas fixe (60 Hz), garantissant une vitesse identique et fluide quel que soit le taux de rafraîchissement de l'écran (60 Hz, 90 Hz, 120 Hz ou 144 Hz).
  - Vitesse globale du jeu légèrement accélérée (+12%) pour une meilleure réactivité arcade.
  - Résolution des problèmes de détection de contours et de collisions invisibles sur la version Web GWT.
  - Impulsion de la bascule calibrée dynamiquement selon le niveau pour toujours permettre aux fruits d'atteindre le panier lorsqu'il prend de la hauteur.
- **Gestion du panier harmonisée :**
  - Le panier s'élève au fil des niveaux de façon strictement identique sur Android, Desktop et Web.
  - Support natif de l'accéléromètre physique sur Android, et émulation douce au clavier sur PC et Web.
- **Nouveau système de High Score Cloud mondial :**
  - Remplacement de l'ancien système FTP par une API REST universelle, rapide et sécurisée.
  - Sauvegarde et lecture du meilleur score mondial et local en temps réel.
  - Système de cache local hors-ligne pour continuer à jouer même sans connexion Internet.
- **Score progressif :**
  - Chaque fruit réussi rapporte désormais un nombre de points proportionnel au niveau en cours (X points au niveau X).
- **Mode Invincible secret :**
  - Activez-le en cliquant 3 fois sur l'oiseau.
  - Permet de s'entraîner sans Game Over en cas de fruits manqués (aucun point n'est alors enregistré pour préserver l'équité des scores).
- **Modernisation du build & outillage :**
  - Compatibilité Gradle moderne et JDK 17 / 21 (Java Runtime Android Studio ou OpenJDK).
  - Scripts batch automatisés pour compiler, nettoyer, exécuter et déployer en 1 clic.

---

## 🚀 Guide d'utilisation et scripts

### 1. Version PC Desktop
Pour lancer directement le jeu sur ordinateur :
```cmd
run.bat
```
*(Alternative via Gradle : `gradlew.bat :fumper-desktop:run`)*

### 2. Version Web HTML5
Pour démarrer le serveur Web local et ouvrir le jeu dans votre navigateur :
```cmd
run-html.bat
```
Puis accédez à [http://localhost:8085/](http://localhost:8085/).

Pour recompiler le code Java en JavaScript (GWT) après modifications :
```cmd
build-html.bat
```
*(Le dossier prêt à être hébergé sur n'importe quel serveur web ou GitHub Pages se trouve dans `fumper-html/dist/`)*.

### 3. Version Android
Pour compiler l'APK de débogage :
```cmd
build-apk.bat
```
Pour installer et démarrer directement l'application sur un smartphone Android connecté en USB :
```cmd
install-apk.bat
```
*(L'APK compilé se situe dans `fumper-android/build/outputs/apk/debug/fumper-android-debug.apk`)*.

---

## 🛠️ Architecture technique

- **Moteur :** [LibGDX 1.9.14](https://libgdx.com/)
- **Physique :** [Box2D](https://box2d.org/) avec chargeur de polygones optimisé
- **Transpilateur Web :** [GWT 2.11.0](http://www.gwtproject.org/)
- **Backend Android :** Android SDK 34 (Android 14) / Min SDK 19 (Android 4.4)
- **Backend Desktop :** LWJGL / Java 8-21
- **API High Scores :** REST JSON Cloud API

---

## 📜 Historique des versions

- **v2.0 (2026)** :
  - Support multiplateforme complet : Android, PC Desktop et Web HTML5.
  - Simulation de l'accéléromètre au clavier sur Desktop et Web.
  - Vitesse physique homogénéisée et dynamisée (+12%).
  - High Score mondial via API REST Cloud (remplacement du FTP Free.fr).
  - Mode invincible (triple-clic sur l'oiseau).
  - Score progressif par niveau et calibration physique automatique de la bascule.
  - Scripts d'automatisation de build, test et nettoyage.
- **v1.05-arthur** : Ajout du déplacement du panier via les accéléromètres.
- **v1.04-clem** : Modification de l'apparition des fruits et limitation du stick sur la bascule.
- **v1.03** : Première version officielle publiée sur Google Play Store.
- **v1.0** : Version initiale du jeu par Python4D / BacoLand.
