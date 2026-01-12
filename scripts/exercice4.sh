#!/bin/bash

# Script de test pour l'exercice 4 - DE02/DE03 HTTP Cache (ETag/304)
# Usage: ./exercice4.sh [page] [size]
# Exemple: ./exercice4.sh 0 50

echo "=========================================="
echo "  Exercice 4 - DE02/DE03 HTTP Cache"
echo "=========================================="
echo ""

# Paramètres avec valeurs par défaut
PAGE=${1:-0}
SIZE=${2:-50}
APERCU_TAILLE=2000
OUTPUT_FILE1="/tmp/response_body_exercice4_first.txt"
OUTPUT_FILE2="/tmp/response_body_exercice4_second.txt"

# Variables
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
# Test 1 : Première requête (obtenir l'ETag)
# ========================================
echo " Test 1 : Première requête (200 OK avec ETag)"
echo "───────────────────────────────────────"

response_first=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
  -s \
  -o "$OUTPUT_FILE1" \
  "$OPTIMIZED_URL")

# Extraire l'ETag de la réponse
etag=$(echo "$response_first" | grep -i "ETag:" | head -1 | sed 's/ETag: //i' | tr -d '\r')

# Récupération des métriques
http_code_first=$(echo "$response_first" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_first=$(echo "$response_first" | grep "TIME:" | sed 's/TIME://')
size_first=$(echo "$response_first" | grep "SIZE:" | sed 's/SIZE://')

# Calculer la taille en KB
if [ -n "$size_first" ] && [ "$size_first" -gt 0 ] 2>/dev/null; then
    size_kb_first=$((size_first / 1024))
else
    size_kb_first=0
fi

echo "  Code HTTP        : $http_code_first"
echo "  Temps réponse    : ${time_first}s"
echo "  Taille réponse   : $size_first bytes ($size_kb_first KB)"
echo "  ETag             : $etag"
echo ""

# ========================================
# Test 2 : Deuxième requête avec If-None-Match
# ========================================
echo " Test 2 : Deuxième requête avec If-None-Match (304 Not Modified)"
echo "───────────────────────────────────────"

if [ -z "$etag" ]; then
    echo " Erreur : Aucun ETag trouvé dans la première réponse"
    echo "    Vérifiez que le cache HTTP est bien configuré"
    echo ""
    exit 1
fi

response_second=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -o "$OUTPUT_FILE1" \
    -H "If-None-Match: $etag" \
    "$OPTIMIZED_URL")

# Récupération des métriques
http_code_second=$(echo "$response_second" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_second=$(echo "$response_second" | grep "TIME:" | sed 's/TIME://')
size_second=$(echo "$response_second" | grep "SIZE:" | sed 's/SIZE://')

# Calculer la taille en KB
if [ -n "$size_second" ] && [ "$size_second" -gt 0 ] 2>/dev/null; then
    size_kb_second=$((size_second / 1024))
else
    size_kb_second=0
fi

echo "  Code HTTP        : $http_code_second"
echo "  Temps réponse    : ${time_second}s"
echo "  Taille réponse   : $size_second bytes ($size_kb_second KB)"
echo ""

echo "=========================================="
echo "  Test terminé"
echo "=========================================="

