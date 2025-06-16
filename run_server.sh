#!/bin/bash

# Script pour lancer le serveur

echo "Démarrage du serveur Morpion..."

if [ ! -d "bin" ]; then
    echo "Le projet n'est pas compilé. Exécutez d'abord ./compile.sh"
    exit 1
fi

java -cp bin server.TicTacToeServer
