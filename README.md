# Jeu de Morpion Multijoueur

Un jeu de Morpion (Tic-Tac-Toe) multijoueur développé en Java avec une interface graphique Swing et une architecture client-serveur utilisant les sockets TCP.

## Architecture

Le projet est composé de trois parties principales :

### 1. Serveur (`server/`)
- **TicTacToeServer.java** : Serveur principal gérant les connexions et la logique du jeu
- **ClientHandler.java** : Gestionnaire de client (un thread par client connecté)

### 2. Client (`client/`)
- **TicTacToeClient.java** : Interface graphique Swing pour les joueurs

### 3. Commun (`common/`)
- **GameMessage.java** : Classe pour les messages échangés entre client et serveur
- **TicTacToeGame.java** : Logique du jeu de Morpion

## Fonctionnalités

### Côté Serveur
- Gestion de deux connexions simultanées via multithreading
- Validation des coups et règles du jeu
- Détection automatique de victoire et match nul
- Gestion des tours alternés
- Synchronisation de l'état du jeu entre les clients
- Gestion des déconnexions

### Côté Client
- Interface graphique intuitive avec Swing
- Grille de jeu interactive (3x3)
- Affichage en temps réel de l'état du jeu
- Indication du tour actuel
- Messages d'information et d'erreur
- Gestion des victoires, défaites et matchs nuls

## Installation et Exécution

### Prérequis
- Java JDK 8 ou supérieur
- Terminal/Command Prompt

### Compilation
```bash
# Rendre les scripts exécutables (sur Unix/Linux/macOS)
chmod +x compile.sh run_server_modern.sh run_client.sh run_client_modern.sh

# Compiler le projet
./compile.sh
```

### Exécution

#### Interface Classique
1. **Démarrer le serveur** :
```bash
./run_server_modern.sh
```

2. **Lancer les clients** (dans des terminaux séparés) :
```bash
./run_client.sh  # Client classique
```

#### 🎨 Interface Moderne (NOUVEAU!)
1. **Démarrer le serveur** :
```bash
./run_server_modern.sh
```

2. **Lancer les clients modernes** :
```bash
./run_client_modern.sh  # Interface moderne et magnifique!
```

3. **Démonstration automatique** :
```bash
./demo_modern.sh  # Lance tout automatiquement
```

### Exécution manuelle
Si vous préférez utiliser directement les commandes Java :

```bash
# Compiler
javac -d bin src/common/*.java src/server/*.java src/client/*.java

# Lancer le serveur
java -cp bin server.TicTacToeServer

# Lancer un client
java -cp bin client.TicTacToeClient
```

## Utilisation

1. Démarrez d'abord le serveur
2. Lancez le premier client et cliquez sur "Se connecter"
3. Lancez le second client et cliquez sur "Se connecter"
4. La partie commence automatiquement une fois les deux joueurs connectés
5. Le joueur X commence toujours en premier
6. Cliquez sur une case libre pour jouer votre coup
7. L'interface indique de qui c'est le tour
8. La partie se termine à la victoire d'un joueur ou en cas de match nul

## Communication Client-Serveur

Le protocole de communication utilise la sérialisation Java avec des objets `GameMessage` :

### Types de messages
- `PLAYER_MOVE` : Coup d'un joueur
- `GAME_UPDATE` : Mise à jour de l'état du jeu
- `GAME_START` : Début de partie
- `GAME_OVER` : Fin de partie
- `PLAYER_CONNECTED` : Confirmation de connexion
- `WAITING_PLAYER` : En attente d'un second joueur
- `INVALID_MOVE` : Coup invalide
- `YOUR_TURN` / `OPPONENT_TURN` : Gestion des tours

## Structure du Projet

```
src/
├── common/
│   ├── GameMessage.java      # Messages réseau
│   └── TicTacToeGame.java    # Logique du jeu
├── server/
│   ├── TicTacToeServer.java  # Serveur principal
│   └── ClientHandler.java    # Gestionnaire de client
└── client/
    └── TicTacToeClient.java  # Interface client Swing

bin/                          # Classes compilées
compile.sh                    # Script de compilation
run_server.sh                 # Script serveur
run_client.sh                 # Script client
README.md                     # Documentation
```

## Fonctionnalités Techniques

- **Multithreading** : Un thread par client côté serveur
- **Synchronisation** : Utilisation de `synchronized` pour l'accès concurrent
- **Sockets TCP** : Communication fiable entre client et serveur
- **Sérialisation Java** : Pour l'échange d'objets complexes
- **Interface graphique Swing** : Interface utilisateur native
- **Gestion d'erreurs** : Gestion robuste des déconnexions et erreurs réseau

## Configuration

Par défaut, le serveur utilise :
- **Host** : localhost
- **Port** : 12345

Pour modifier ces paramètres, éditez les constantes dans les fichiers correspondants.

## Dépannage

### Le serveur ne démarre pas
- Vérifiez que le port 12345 n'est pas déjà utilisé
- Assurez-vous d'avoir les permissions nécessaires

### Le client ne peut pas se connecter
- Vérifiez que le serveur est démarré
- Vérifiez l'adresse et le port du serveur
- Contrôlez les paramètres de pare-feu

### Erreurs de compilation
- Vérifiez la version de Java (JDK 8+)
- Assurez-vous que tous les fichiers source sont présents

### 🎨 Nouvelle Interface Moderne

La version moderne inclut :

#### **Design & UX**
- 🎨 **Interface moderne** avec couleurs vibrantes
- ✨ **Effets visuels** au survol des boutons et cases
- 🎯 **Design responsive** et épuré
- 🌈 **Thème coloré** avec palette moderne (indigo, bleu, vert, rouge)
- 📱 **Interface intuitive** avec icônes émojis

#### **Fonctionnalités Visuelles**
- 🔘 **Boutons stylisés** avec effets 3D
- 🎮 **Grille de jeu interactive** avec feedback visuel
- 💫 **Animations de survol** pour une meilleure UX
- 🎨 **Couleurs distinctives** pour X (bleu) et O (rouge)
- ⚡ **Interface rapide** et fluide

#### **Contrôles Améliorés**
- 🔗 **Bouton de connexion** avec retour visuel
- 🎮 **Nouvelle partie** facilement accessible
- ℹ️ **À propos** avec informations détaillées
- 🎯 **Status en temps réel** avec émojis expressifs
