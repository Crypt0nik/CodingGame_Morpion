#!/bin/bash

# Script pour lancer le client moderne

echo "Démarrage du client Morpion Moderne..."

if [ ! -d "bin" ]; then
    echo "Le projet n'est pas compilé. Exécutez d'abord ./compile.sh"
    exit 1
fi

java -cp bin client.TicTacToeClientModern
