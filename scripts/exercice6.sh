#!/bin/bash

# Script de test pour l'exercice 6 - DE06 Partial Content (HTTP 206)
# Usage: ./exercice6.sh [page] [size] [range_size]
# Exemple: ./exercice6.sh 0 100 1000

echo "=========================================="
echo "  Exercice 6 - DE06 Partial Content (HTTP 206)"
echo "=========================================="
echo ""

# Paramètres avec valeurs par défaut
PAGE=${1:-0}
SIZE=${2:-100}
RANGE_SIZE=${3:-1000}
APERCU_TAILLE=2000
OUTPUT_FILE1="/tmp/response_body_exercice6_full.txt"
OUTPUT_FILE2="/tmp/response_body_exercice6_partial.txt"

# Variables
OPTIMIZED_URL="http://localhost:8081/books?page=$PAGE&size=$SIZE"

echo "  URL testée : $OPTIMIZED_URL"
echo "   (Page: $PAGE, Size: $SIZE, Range Size: $RANGE_SIZE bytes)"
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
# Test 1 : Requête complète (200 OK)
# ========================================
echo " Test 1 : Requête complète (200 OK)"
echo "───────────────────────────────────────"

response_full=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
  -s \
  -D /tmp/headers_full.txt \
  -o "$OUTPUT_FILE1" \
  "$OPTIMIZED_URL")

# Récupération des métriques
http_code_full=$(echo "$response_full" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_full=$(echo "$response_full" | grep "TIME:" | sed 's/TIME://')
size_full=$(echo "$response_full" | grep "SIZE:" | sed 's/SIZE://')

# Récupérer les headers
accept_ranges=$(grep -i "Accept-Ranges:" /tmp/headers_full.txt | tr -d '\r')

# Calculer la taille en KB
if [ -n "$size_full" ] && [ "$size_full" -gt 0 ] 2>/dev/null; then
    size_kb_full=$((size_full / 1024))
else
    size_kb_full=0
fi

echo "  Code HTTP        : $http_code_full"
echo "  Temps réponse    : ${time_full}s"
echo "  Taille réponse   : $size_full bytes ($size_kb_full KB)"
echo "  Accept-Ranges    : $accept_ranges"
echo ""

# Calculer la fin de la plage (limiter à la taille totale)
if [ "$size_full" -lt "$RANGE_SIZE" ]; then
    range_end=$((size_full - 1))
else
    range_end=$((RANGE_SIZE - 1))
fi

# ========================================
# Test 2 : Requête partielle avec Range (206 Partial Content)
# ========================================
echo " Test 2 : Requête partielle avec Range: bytes=0-$range_end (206 Partial Content)"
echo "───────────────────────────────────────"

response_partial=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -D /tmp/headers_partial.txt \
    -o "$OUTPUT_FILE2" \
    -H "Range: bytes=0-$range_end" \
    "$OPTIMIZED_URL")

# Récupération des métriques
http_code_partial=$(echo "$response_partial" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_partial=$(echo "$response_partial" | grep "TIME:" | sed 's/TIME://')
size_partial=$(echo "$response_partial" | grep "SIZE:" | sed 's/SIZE://')

# Récupérer les headers
content_range=$(grep -i "Content-Range:" /tmp/headers_partial.txt | tr -d '\r')

# Calculer la taille en KB
if [ -n "$size_partial" ] && [ "$size_partial" -gt 0 ] 2>/dev/null; then
    size_kb_partial=$((size_partial / 1024))
else
    size_kb_partial=0
fi

echo "  Code HTTP        : $http_code_partial"
echo "  Temps réponse    : ${time_partial}s"
echo "  Taille réponse   : $size_partial bytes ($size_kb_partial KB)"
echo "  Content-Range    : $content_range"
echo ""

# ========================================
# Test 3 : Requête avec plage invalide (416 Range Not Satisfiable)
# ========================================
echo " Test 3 : Requête avec plage invalide (416 Range Not Satisfiable)"
echo "───────────────────────────────────────"

invalid_end=$((size_full * 2))

response_invalid=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -D /tmp/headers_invalid.txt \
    -o /dev/null \
    -H "Range: bytes=$invalid_end-$((invalid_end + 1000))" \
    "$OPTIMIZED_URL")

http_code_invalid=$(echo "$response_invalid" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')

echo "  Code HTTP        : $http_code_invalid"

if [ "$http_code_invalid" = "416" ]; then
    echo "  La plage invalide retourne bien 416"
else
    echo "  Attendu 416, reçu $http_code_invalid"
fi

echo ""


# Affichage d'un aperçu des réponses
echo " Aperçu de la réponse complète :"
echo "───────────────────────────────────────"
head -c 300 "$OUTPUT_FILE1"
echo "..."
echo ""

echo " Aperçu de la réponse partielle (206) :"
echo "───────────────────────────────────────"
head -c 300 "$OUTPUT_FILE2"
echo "..."
echo ""

# Nettoyage
rm -f "$OUTPUT_FILE1" "$OUTPUT_FILE2" /tmp/headers_full.txt /tmp/headers_partial.txt /tmp/headers_invalid.txt

echo "=========================================="
echo "  Test terminé"
echo "=========================================="


