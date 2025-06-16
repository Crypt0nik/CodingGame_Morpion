#!/bin/bash

# 🧪 Script de test automatique pour démontrer le fonctionnement

echo "🧪 Test automatique - Morpion Multijoueur"
echo "========================================"
echo ""

# Vérifier la compilation
echo "1️⃣ Test de compilation..."
if ./compile.sh > /dev/null 2>&1; then
    echo "   ✅ Compilation réussie"
else
    echo "   ❌ Échec de compilation"
    exit 1
fi

echo ""
echo "2️⃣ Test de démarrage serveur..."
echo "   🚀 Démarrage du serveur en arrière-plan..."

# Démarrer le serveur en arrière-plan
./run_server_modern.sh > server_test.log 2>&1 &
SERVER_PID=$!

# Attendre que le serveur démarre
sleep 2

# Vérifier que le serveur écoute
if lsof -Pi :12345 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "   ✅ Serveur démarré et écoute sur port 12345"
else
    echo "   ❌ Serveur non accessible"
    kill $SERVER_PID 2>/dev/null
    exit 1
fi

echo ""
echo "3️⃣ Test de connexion client..."
echo "   💡 Pour tester manuellement :"
echo "   - Ouvrir un terminal : ./run_client_modern.sh"
echo "   - Cliquer sur 'Se connecter'"
echo "   - Ouvrir un second terminal : ./run_client_modern.sh"  
echo "   - Cliquer sur 'Se connecter'"
echo "   - Jouer une partie complète"

echo ""
echo "4️⃣ Nettoyage..."
echo "   🧹 Arrêt du serveur de test..."
kill $SERVER_PID 2>/dev/null
rm -f server_test.log

echo "   ✅ Test terminé"
echo ""
echo "🎮 Le système est prêt à l'utilisation !"
echo "📋 Consultez RAPPORT_TECHNIQUE.md pour plus de détails"
