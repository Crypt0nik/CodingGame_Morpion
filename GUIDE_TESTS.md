# 🧪 Guide de Tests - Morpion Multijoueur

## Tests automatiques

### Exécution du test système
```bash
./test_system.sh
```

Ce script vérifie :
- ✅ Compilation du projet
- ✅ Démarrage du serveur
- ✅ Accessibilité du port 12345
- ✅ Nettoyage automatique

## Tests manuels

### Test 1 : Partie normale
1. **Démarrer le serveur** :
   ```bash
   ./run_server_modern.sh
   ```

2. **Client 1** (Terminal 2) :
   ```bash
   ./run_client_modern.sh
   ```
   - Cliquer "🔗 Se connecter"
   - Observer : "🎮 Joueur X"

3. **Client 2** (Terminal 3) :
   ```bash
   ./run_client_modern.sh
   ```
   - Cliquer "🔗 Se connecter" 
   - Observer : "🎮 Joueur O"

4. **Jouer une partie** :
   - Client 1 : Cliquer case (0,0) → X apparaît
   - Client 2 : Cliquer case (1,1) → O apparaît
   - Continuer jusqu'à victoire ou match nul

### Test 2 : Gestion des erreurs
1. **Coup invalide** :
   - Essayer de cliquer sur une case occupée
   - ✅ Attendu : Aucun effet

2. **Coup hors tour** :
   - Essayer de jouer pendant le tour de l'adversaire
   - ✅ Attendu : Boutons désactivés

### Test 3 : Reconnexion
1. **Fermer un client** pendant une partie
2. **Observer** l'autre client : message d'erreur
3. **Relancer** le client fermé
4. **Se reconnecter** pour une nouvelle partie

### Test 4 : Popups de fin de partie
1. **Jouer jusqu'à victoire** :
   ```
   X | X | X
   O | O |  
     |   |  
   ```
   - ✅ Attendu : Popup "🎉 Victoire !" pour X
   - ✅ Attendu : Popup "💔 Défaite" pour O

2. **Jouer jusqu'à match nul** :
   ```
   X | O | X
   O | X | O
   O | X | O
   ```
   - ✅ Attendu : Popup "🤝 Match nul !" pour les deux

## Résultats de tests attendus

### ✅ Tests réussis
- Compilation sans erreur
- Serveur écoute sur port 12345
- Connexion de 2 clients simultanés
- Synchronisation des coups en temps réel
- Détection correcte de victoire/match nul
- Popups personnalisés fonctionnels
- Bouton "Nouvelle partie" activé après fin de jeu

### ❌ Cas d'échec possibles
- Port 12345 déjà utilisé → Changer de port dans le code
- Firewall bloque la connexion → Configurer les exceptions
- Java non installé → Installer JDK 8+

## Métriques de performance

### Tests de latence
```bash
# Mesurer le temps de réponse d'un coup
time java -cp bin client.TicTacToeClient
```

### Tests de mémoire
```bash
# Surveiller l'utilisation mémoire
top -p $(pgrep -f TicTacToeServer)
```

## Rapport de test type

```
🧪 RAPPORT DE TEST - [Date]
================================

✅ Test compilation : RÉUSSI
✅ Test serveur : RÉUSSI  
✅ Test connexion clients : RÉUSSI
✅ Test partie complète : RÉUSSI
✅ Test gestion erreurs : RÉUSSI
✅ Test popups fin de partie : RÉUSSI
✅ Test nouvelle partie : RÉUSSI

🎯 Score global : 7/7 tests réussis
📊 Système validé pour production
```
