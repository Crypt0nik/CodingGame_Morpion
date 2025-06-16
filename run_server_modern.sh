#!/bin/bash

# 🚀 Script moderne pour lancer le serveur Morpion Ultra-Design

echo "✨ Démarrage du serveur Morpion Ultra-Design ✨"
echo "🔗 Serveur multijoueur avec interface moderne"
echo ""

if [ ! -d "bin" ]; then
    echo "❌ Le projet n'est pas compilé."
    echo "📦 Exécutez d'abord: ./compile.sh"
    echo ""
    exit 1
fi

echo "🎮 Lancement du serveur sur localhost:12345..."
echo "📡 En attente de connexions des clients..."
echo "💡 Utilisez Ctrl+C pour arrêter le serveur"
echo ""
echo "════════════════════════════════════════════"

java -cp bin server.TicTacToeServer
