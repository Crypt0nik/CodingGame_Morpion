# GUIDE DE DÉMARRAGE RAPIDE

## Jeu de Morpion Multijoueur - Java

### 🚀 Démarrage Rapide (Démonstration automatique)

```bash
./demo.sh
```

Ce script lance automatiquement :
- Le serveur
- Deux clients
- Instructions pour jouer

### 📖 Démarrage Manuel

#### 1. Compilation
```bash
./compile.sh
```

#### 2. Lancement du serveur (Terminal 1)
```bash
./run_server.sh
```

#### 3. Lancement des clients (Terminaux 2 et 3)
```bash
./run_client.sh
```

### 🎮 Comment Jouer

1. **Connectez les joueurs** : Cliquez sur "Se connecter" dans chaque client
2. **Joueur X commence** : Le premier client connecté est X (bleu)
3. **Joueur O suit** : Le second client connecté est O (rouge)  
4. **Alternance** : Les joueurs alternent automatiquement
5. **Victoire** : Trois symboles alignés (ligne, colonne, diagonale)
6. **Match nul** : Grille pleine sans victoire

### 🏗️ Architecture

```
Client 1 (X) ←→ Serveur ←→ Client 2 (O)
```

- **Serveur** : Gère la logique, valide les coups, synchronise l'état
- **Client** : Interface graphique Swing, envoie/reçoit les coups
- **Multithreading** : Un thread par client côté serveur
- **Communication** : Sockets TCP + sérialisation Java

### 🧪 Tests

```bash
javac -d bin src/common/*.java src/test/*.java
java -cp bin test.GameTest
```

### 📁 Structure

```
src/
├── common/     # Classes partagées
├── server/     # Serveur de jeu  
├── client/     # Interface graphique
└── test/       # Tests unitaires
```

### ⚙️ Configuration

- **Port** : 12345 (modifiable dans les sources)
- **Host** : localhost (modifiable dans les sources)
- **Java** : JDK 8+ requis

### 🔧 Dépannage

**Erreur de connexion** : Vérifiez que le serveur est démarré

**Port occupé** : Modifiez le port dans les sources ou tuez le processus

**Compilation échoue** : Vérifiez la version Java

### 📱 Interface Client

- **Grille 3x3** : Cliquez pour jouer
- **Statut** : Indique le tour actuel
- **Joueur** : Affiche votre symbole (X ou O)
- **Messages** : Victoire, défaite, erreurs

---

**Bon jeu ! 🎯**
