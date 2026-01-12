#!/bin/bash

# Script de test pour l'exercice 5 - DE04/DE05 Synchronisation Delta
# Usage: ./exercice5.sh [page] [size]
# Exemple: ./exercice5.sh 0 100

echo "=========================================="
echo "  Exercice 5 - DE04/DE05 Synchronisation Delta"
echo "=========================================="
echo ""

# Paramètres avec valeurs par défaut
PAGE=${1:-0}
SIZE=${2:-100}
APERCU_TAILLE=2000
OUTPUT_FILE1="/tmp/response_body_exercice5_first.txt"
OUTPUT_FILE2="/tmp/response_body_exercice5_second.txt"

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
# Test 1 : Première synchronisation (tous les livres)
# ========================================
echo " Test 1 : Première synchronisation (tous les livres)"
echo "───────────────────────────────────────"

response_first=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
  -s \
  -o "$OUTPUT_FILE1" \
  "$OPTIMIZED_URL")

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
echo ""

# Extraire le timestamp de la première réponse (pour la deuxième sync)
# On suppose que la réponse contient un timestamp
server_timestamp=$(grep -o '"timestamp":[0-9]*' "$OUTPUT_FILE1" | head -1 | sed 's/"timestamp"://')

if [ -z "$server_timestamp" ]; then
    # Si pas de timestamp dans la structure Delta, utiliser le timestamp actuel
    server_timestamp=$(date +%s)000
fi

# ========================================
# Test 2 : Deuxième synchronisation avec Delta (avec lastSync)
# ========================================
echo " Test 2 : Deuxième synchronisation avec Delta (avec lastSync)"
echo "───────────────────────────────────────"

DELTA_URL="http://localhost:8081/books?page=$PAGE&size=$SIZE&lastSync=$server_timestamp"

response_second=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -o "$OUTPUT_FILE2" \
    "$DELTA_URL")

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


# Affichage d'un aperçu de la réponse (premiers 500 caractères)
echo " Aperçu de la 1ère synchronisation :"
echo "───────────────────────────────────────"
head -c 500 "$OUTPUT_FILE1"
echo "..."
echo ""

echo " Aperçu de la 2ème synchronisation (Delta) :"
echo "───────────────────────────────────────"
head -c 500 "$OUTPUT_FILE2"
echo "..."
echo ""

# Nettoyage
rm -f "$OUTPUT_FILE1" "$OUTPUT_FILE2"

echo "=========================================="
echo "  Test terminé"
echo "=========================================="


