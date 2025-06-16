#!/bin/bash

echo "🎮 === DÉMONSTRATION INTERFACE MODERNE ==="
echo ""
echo "✨ Nouvelle interface graphique avec:"
echo "   • Design moderne et coloré"
echo "   • Effets visuels au survol"
echo "   • Boutons stylisés"
echo "   • Couleurs thématiques"
echo "   • Interface responsive"
echo ""
echo "🚀 Lancement automatique..."

# Démarrer le serveur
echo "📡 Démarrage du serveur..."
./run_server.sh &
SERVER_PID=$!
sleep 2

# Lancer les clients modernes
echo "🎨 Lancement des clients modernes..."
./run_client_modern.sh &
CLIENT1_PID=$!
sleep 1

./run_client_modern.sh &
CLIENT2_PID=$!

echo ""
echo "✅ Tout est démarré !"
echo "📋 Instructions:"
echo "   1. Cliquez sur '🔗 Se connecter' dans chaque fenêtre"
echo "   2. La partie commence automatiquement"
echo "   3. Le joueur X (bleu) commence"
echo "   4. Profitez de la nouvelle interface !"
echo ""
echo "Appuyez sur Entrée pour arrêter..."
read

echo "🛑 Arrêt des processus..."
kill $SERVER_PID $CLIENT1_PID $CLIENT2_PID 2>/dev/null
echo "✅ Terminé !"
