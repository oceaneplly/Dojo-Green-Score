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
echo " Test 1 : Requête avec compression (Accept-Encoding: gzip)"
echo "───────────────────────────────────────"

# Faire une requête avec -D pour capturer les headers
HEADERS_FILE="/tmp/headers_exercice3.txt"
OUTPUT_FILE_COMPRESSED="/tmp/response_body_exercice3_compressed.txt"

response_body=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -D "$HEADERS_FILE" \
    -H "Accept-Encoding: gzip" \
    -o "$OUTPUT_FILE_COMPRESSED" \
    "$OPTIMIZED_URL")

# Récupération des métriques
http_code=$(echo "$response_body" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_response=$(echo "$response_body" | grep "TIME:" | sed 's/TIME://')
size_response=$(echo "$response_body" | grep "SIZE:" | sed 's/SIZE://')

# Vérifier si Content-Encoding: gzip est présent dans les headers captés
content_encoding=$(grep -i "Content-Encoding" "$HEADERS_FILE" | tr -d '\r' | sed 's/^[[:space:]]*//g')

if [ -n "$size_response" ] && [ "$size_response" -gt 0 ] 2>/dev/null; then
    size_kb=$((size_response / 1024))
else
    size_kb=0
fi

echo "  Code HTTP        : $http_code"
echo "  Temps réponse    : ${time_response}s"
echo "  Taille réponse   : $size_response bytes ($size_kb KB)"
echo "  $content_encoding"
echo ""

# Affichage du contenu compressé (binaire)
echo " Contenu compressé (binaire) :"
echo "───────────────────────────────────────"
# Afficher les premiers bytes en hexadécimal
od -A x -t x1z -v "$OUTPUT_FILE_COMPRESSED" | head -20
echo "..."
echo ""

# ========================================
# Test 2 : Requête sans compression
# ========================================
echo " Test 2 : Requête sans compression (lisible)"
echo "───────────────────────────────────────"

OUTPUT_FILE_UNCOMPRESSED="/tmp/response_body_exercice3_uncompressed.txt"

response_body_2=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -o "$OUTPUT_FILE_UNCOMPRESSED" \
    "$OPTIMIZED_URL")

# Récupération des métriques
http_code_2=$(echo "$response_body_2" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_response_2=$(echo "$response_body_2" | grep "TIME:" | sed 's/TIME://')
size_response_2=$(echo "$response_body_2" | grep "SIZE:" | sed 's/SIZE://')

if [ -n "$size_response_2" ] && [ "$size_response_2" -gt 0 ] 2>/dev/null; then
    size_kb_2=$((size_response_2 / 1024))
else
    size_kb_2=0
fi

echo "  Code HTTP        : $http_code_2"
echo "  Temps réponse    : ${time_response_2}s"
echo "  Taille réponse   : $size_response_2 bytes ($size_kb_2 KB)"
echo ""

# Aperçu de la réponse JSON lisible
echo " Contenu décompressé (JSON lisible) :"
echo "───────────────────────────────────────"
cat "$OUTPUT_FILE_UNCOMPRESSED"
echo ""

# ========================================
# Comparaison de la compression
# ========================================
echo " Comparaison de la compression :"
echo "───────────────────────────────────────"
echo "  Taille compressée   : $size_response bytes ($size_kb KB)"
echo "  Taille non-compressée : $size_response_2 bytes ($size_kb_2 KB)"

if [ "$size_response_2" -gt 0 ] 2>/dev/null; then
    compression_ratio=$((100 * (size_response_2 - size_response) / size_response_2))
    echo "  Ratio de compression : $compression_ratio %"
fi

echo ""

# Nettoyage
rm -f "$OUTPUT_FILE_COMPRESSED" "$OUTPUT_FILE_UNCOMPRESSED" "$HEADERS_FILE"

echo "=========================================="
echo "  Test terminé"
echo "=========================================="

