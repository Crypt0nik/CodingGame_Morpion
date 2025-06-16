#!/bin/bash

# Script de compilation pour le projet Morpion

echo "Compilation du projet Morpion..."

# Créer le répertoire de sortie
mkdir -p bin

# Compiler tous les fichiers Java
javac -d bin src/common/*.java src/server/*.java src/client/*.java

if [ $? -eq 0 ]; then
    echo "Compilation réussie!"
    echo ""
    echo "Pour lancer le serveur:"
    echo "  ./run_server.sh"
    echo ""
    echo "Pour lancer un client:"
    echo "  ./run_client.sh"
    echo ""
    echo "Ou utilisez directement les commandes Java:"
    echo "  java -cp bin server.TicTacToeServer"
    echo "  java -cp bin client.TicTacToeClient"
else
    echo "Erreur de compilation!"
    exit 1
fi
