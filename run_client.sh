#!/bin/bash

# Script pour lancer un client

echo "Démarrage du client Morpion..."

if [ ! -d "bin" ]; then
    echo "Le projet n'est pas compilé. Exécutez d'abord ./compile.sh"
    exit 1
fi

java -cp bin client.TicTacToeClient
