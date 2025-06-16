#!/bin/bash

echo "✨ === INTERFACE ULTRA-MODERNE === ✨"
echo ""
echo "🎨 Fonctionnalités visuelles exceptionnelles:"
echo "   • Dégradés holographiques"
echo "   • Effets de lueur néon"
echo "   • Animations de pulsation"
echo "   • Boutons 3D interactifs"
echo "   • Glassmorphism design"
echo "   • Grille ultra-responsive"
echo ""
echo "🚀 Lancement de l'expérience visuelle..."

# Démarrer le serveur
echo "📡 Serveur en cours de démarrage..."
./run_server.sh &
SERVER_PID=$!
sleep 2

# Lancer les clients ultra
echo "✨ Clients ultra-modernes en cours de lancement..."
./run_client_modern.sh &
CLIENT1_PID=$!
sleep 1

./run_client_modern.sh &
CLIENT2_PID=$!

echo ""
echo "🌟 INTERFACE ULTRA DÉMARRÉE !"
echo ""
echo "🎮 Comment jouer:"
echo "   1. Admirez d'abord le magnifique design !"
echo "   2. Cliquez sur '🔗 Se connecter' (effet néon)"
echo "   3. Observez les animations de pulsation"
echo "   4. Jouez avec la grille 3D interactive"
echo "   5. Profitez des effets visuels !"
echo ""
echo "Appuyez sur Entrée pour arrêter l'expérience..."
read

echo "🛑 Arrêt de l'interface ultra..."
kill $SERVER_PID $CLIENT1_PID $CLIENT2_PID 2>/dev/null
echo "✅ Interface fermée !"
