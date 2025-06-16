#!/bin/bash

# Script de démonstration pour le jeu de Morpion

echo "=== Démonstration du Jeu de Morpion Multijoueur ==="
echo ""
echo "Ce script va :"
echo "1. Démarrer le serveur en arrière-plan"
echo "2. Attendre 2 secondes"
echo "3. Lancer deux clients automatiquement"
echo ""
echo "Appuyez sur Entrée pour continuer ou Ctrl+C pour annuler..."
read

# Vérifier que le projet est compilé
if [ ! -d "bin" ]; then
    echo "Compilation du projet..."
    ./compile.sh
    if [ $? -ne 0 ]; then
        echo "Erreur de compilation!"
        exit 1
    fi
fi

echo "Démarrage du serveur..."
java -cp bin server.TicTacToeServer &
SERVER_PID=$!

# Attendre que le serveur démarre
sleep 2

echo "Lancement des clients..."
echo "Client 1 (Joueur X)..."
java -cp bin client.TicTacToeClient &
CLIENT1_PID=$!

sleep 1

echo "Client 2 (Joueur O)..."
java -cp bin client.TicTacToeClient &
CLIENT2_PID=$!

echo ""
echo "Serveur et clients démarrés!"
echo "- Serveur PID: $SERVER_PID"
echo "- Client 1 PID: $CLIENT1_PID"
echo "- Client 2 PID: $CLIENT2_PID"
echo ""
echo "Instructions:"
echo "1. Dans chaque fenêtre client, cliquez sur 'Se connecter'"
echo "2. Une fois les deux joueurs connectés, la partie commence"
echo "3. Le joueur X (bleu) commence"
echo "4. Cliquez sur une case libre pour jouer"
echo ""
echo "Appuyez sur Entrée pour arrêter tous les processus..."
read

echo "Arrêt des processus..."
kill $SERVER_PID $CLIENT1_PID $CLIENT2_PID 2>/dev/null
echo "Terminé!"
