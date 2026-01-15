#!/bin/bash

# Script de test pour l'exercice 4 - DE02/DE03 HTTP Cache (ETag/304)
# Usage: ./exercice4.sh [book-id]
# Exemple: ./exercice4.sh 1

echo "=========================================="
echo "  Exercice 4 - DE02/DE03 HTTP Cache"
echo "=========================================="
echo ""

# Paramètres avec valeurs par défaut
BOOK_ID=${1:-1}
APERCU_TAILLE=2000
OUTPUT_FILE1="/tmp/response_body_exercice4_first.txt"
OUTPUT_FILE2="/tmp/response_body_exercice4_second.txt"

# Variables
OPTIMIZED_URL="http://localhost:8081/books/$BOOK_ID"

echo "  URL testée : $OPTIMIZED_URL"
echo "   (Book ID: $BOOK_ID)"
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

HEADERS_FILE="/tmp/headers_exercice4.txt"

response_first=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
  -s \
  -D "$HEADERS_FILE" \
  -o "$OUTPUT_FILE1" \
  "$OPTIMIZED_URL")

# Extraire l'ETag depuis le fichier des headers
etag=$(grep -i "^ETag:" "$HEADERS_FILE" | head -1 | sed 's/ETag: //i' | tr -d '\r')

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

HEADERS_FILE2="/tmp/headers_exercice4_second.txt"

response_second=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -D "$HEADERS_FILE2" \
    -o "$OUTPUT_FILE2" \
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

# ========================================
# Comparaison
# ========================================
echo " Comparaison :"
echo "───────────────────────────────────────"
echo "  1ère requête (200)  : $size_first bytes ($size_kb_first KB)"
echo "  2ème requête (304)  : $size_second bytes ($size_kb_second KB)"

if [ "$http_code_second" = "304" ]; then
    echo "  Économies           : $size_first bytes (100% - pas de body 304)"
    echo "  Compression ETag    : ACTIVE"
else
    echo "  Compression ETag    : NON ACTIVE"
fi

echo ""

# Nettoyage
rm -f "$OUTPUT_FILE1" "$OUTPUT_FILE2" "$HEADERS_FILE" "$HEADERS_FILE2"
