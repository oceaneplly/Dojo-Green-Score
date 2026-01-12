#!/bin/bash

# Script de test pour l'exercice 3 - DE01/USXX Compression Gzip
# Usage: ./exercice3.sh [page] [size]
# Exemple: ./exercice3.sh 0 50

echo "=========================================="
echo "  Exercice 3 - DE01 Compression Gzip"
echo "=========================================="
echo ""

# Paramètres avec valeurs par défaut
PAGE=${1:-0}
SIZE=${2:-100}
APERCU_TAILLE=2000
OUTPUT_FILE="/tmp/response_body_exercice3.txt"
OPTIMIZED_URL="http://localhost:8081/books?page=$PAGE&size=$SIZE"

echo "  URL testée : $OPTIMIZED_URL"
echo "   (Page: $PAGE, Size: $SIZE)"
echo ""

# Vérifier si l'application est accessible
if ! curl -s --max-time 5 "http://localhost:8081/actuator/health" > /dev/null 2>&1; then
    echo " Erreur : Impossible de contacter l'API optimisée sur le port 8081"
    echo ""
    echo "Vérifications à faire :"
    echo "  1. L'application OptimizedApplication est-elle démarrée ?"
    echo "  2. Vérifiez qu'elle tourne bien sur le port 8081"
    echo "  3. Essayez d'accéder à http://localhost:8081/actuator/health"
    echo ""
    exit 1
fi

echo " Application OptimizedApplication accessible sur le port 8081"
echo ""

# ========================================
# Test : Requête avec compression Gzip
# ========================================
echo " Test : Requête avec compression (Accept-Encoding: gzip)"
echo "───────────────────────────────────────"

response_body=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -H "Accept-Encoding: gzip" \
    -o "$OUTPUT_FILE" \
    "$OPTIMIZED_URL")

# Récupération des métriques
http_code=$(echo "$response_body" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_response=$(echo "$response_body" | grep "TIME:" | sed 's/TIME://')
size_response=$(echo "$response_body" | grep "SIZE:" | sed 's/SIZE://')

# Enlever les lignes de métriques du body
#response_body=$(echo "$response_body" | grep -v "HTTP_CODE:" | grep -v "TIME:" | grep -v "SIZE:")

# Vérifier si Content-Encoding: gzip est présent
content_encoding=$(curl -s -I -H "Accept-Encoding: gzip" "$OPTIMIZED_URL" | grep -i "Content-Encoding" | tr -d '\r')

if [ -n "$size_response" ] && [ "$size_response" -gt 0 ] 2>/dev/null; then
    size_kb=$((size_response / 1024))
else
    size_kb=0
fi

echo "  Code HTTP        : $http_code"
echo "  Temps réponse    : ${time_response}s"
echo "  Taille réponse   : $size_response bytes ($size_kb KB)"
echo "  Content-Encoding : $content_encoding"
echo ""

## Aperçu de la réponse
#echo " Aperçu de la réponse :"
#echo "───────────────────────────────────────"
#echo "$response_body" | head -c $APERCU_TAILLE
#echo "..."
#echo ""


echo "=========================================="
echo "  Test terminé"
echo "=========================================="

