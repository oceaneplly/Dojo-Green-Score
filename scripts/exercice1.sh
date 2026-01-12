#!/bin/bash

# Script de test pour l'exercice 1 - DE11 Pagination
# Usage: ./exercice1.sh [page] [size]
# Exemple: ./exercice1.sh 0 20

echo "=========================================="
echo "  Exercice 1 - DE11 Pagination"
echo "=========================================="
echo ""

# Paramètres avec valeurs par défaut
PAGE=${1:-0}
SIZE=${2:-20}
APERCU_TAILLE=2000

# Variables
OPTIMIZED_URL="http://localhost:8081/books?page=$PAGE&size=$SIZE"
OUTPUT_FILE="/tmp/response_body_exercice1.txt"

echo "  URL testée : $OPTIMIZED_URL"
echo "   (Page: $PAGE, Size: $SIZE)"
echo ""

if ! curl -s --max-time 5 "http://localhost:8081/actuator/health" > /dev/null 2>&1; then
    echo "Erreur : Impossible de contacter l'API optimisée sur le port 8081"
    echo ""
    echo "Vérifications à faire :"
    echo "  1. L'application OptimizedApplication est-elle démarrée ?"
    echo "  2. Vérifiez qu'elle tourne bien sur le port 8081"
    echo "  3. Essayez d'accéder à http://localhost:8081/actuator/health"
    echo ""
    exit 1
fi

echo "Application OptimizedApplication accessible sur le port 8081"
echo ""

# Appel curl avec affichage du code HTTP, temps et taille
response=$(curl -w "\n%{http_code}\n%{time_total}\n%{size_download}" \
    -s \
    -o "$OUTPUT_FILE" \
    "$OPTIMIZED_URL")

# Récupération des métriques
http_code=$(echo "$response" | tail -3 | head -1)
time_response=$(echo "$response" | tail -2 | head -1)
size_response=$(echo "$response" | tail -1)

# Lecture de la réponse
response_body=$(cat "$OUTPUT_FILE")

# Affichage des résultats
echo "  Résultats :"
echo "───────────────────────────────────────"
echo "  Code HTTP        : $http_code"
echo "  Temps réponse    : ${time_response}s"

# Calculer la taille en KB sans bc (utiliser bash natif)
size_kb=$((size_response / 1024))
echo " Taille réponse   : $size_response bytes ($size_kb KB)"
echo ""

# Affichage d'un aperçu de la réponse (premiers 200 caractères)
echo " Aperçu de la réponse :"
echo "───────────────────────────────────────"
echo "$response_body" | head -c $APERCU_TAILLE
echo "..."
echo ""

# Nettoyage
rm -f "$OUTPUT_FILE"

echo "=========================================="
echo "  Test terminé"
echo "=========================================="

