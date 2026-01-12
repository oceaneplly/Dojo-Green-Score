#!/bin/bash

# Script de test basique pour l'API baseline (sans optimisation)
echo "=========================================="
echo " Test basique - API Baseline"
echo "=========================================="
echo ""

BASELINE_URL="http://localhost:8080/books"
OUTPUT_FILE="/tmp/response_body_baseline1.txt"
APERCU_TAILLE=2000

# Vérifier si l'application est accessible
if ! curl -s --max-time 5 "http://localhost:8080/actuator/health" > /dev/null 2>&1; then
    echo "Erreur : Impossible de contacter l'API Baseline sur le port 8080"
    echo ""
    echo "Vérifications à faire :"
    echo "  1. L'application BaselineApplication est-elle démarrée ?"
    echo "  2. Vérifiez qu'elle tourne bien sur le port 8080"
    echo "  3. Essayez d'accéder à http://localhost:8080/actuator/health"
    echo ""
    exit 1
fi

echo "Application BaselineApplication accessible sur le port 8080"
echo ""

rm -f "$OUTPUT_FILE"
echo " URL testée : $BASELINE_URL"
echo ""

response=$(curl -w "\n%{http_code}\n%{time_total}\n%{size_download}" \
    -s \
    -o "$OUTPUT_FILE" \
    "$BASELINE_URL")

http_code=$(echo "$response" | tail -3 | head -1)
time_response=$(echo "$response" | tail -2 | head -1)
size_response=$(echo "$response" | tail -1)

echo " Résultats :"
echo "───────────────────────────────────────"
echo " Code HTTP        : $http_code"
echo " Temps réponse    : ${time_response}s"

# Calculer la taille en KB sans bc
size_kb=$((size_response / 1024))
echo " Taille réponse   : $size_response bytes ($size_kb KB)"
echo ""
rm -f "$OUTPUT_FILE"

echo "=========================================="
echo "  Test terminé"
echo "=========================================="

