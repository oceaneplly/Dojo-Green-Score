#!/bin/bash

# Script de test pour l'exercice 5 - DE05 Synchronisation Delta
# Usage: ./exercice5.sh [page] [size]
# Exemple: ./exercice5.sh 0 100

echo "=========================================="
echo "  Exercice 5 - DE06/US04 Synchronisation Delta"
echo "=========================================="
echo ""

# Paramètres avec valeurs par défaut
PAGE=${1:-0}
SIZE=${2:-100}
OUTPUT_FILE1="/tmp/response_body_exercice5_books.txt"
OUTPUT_FILE3="/tmp/response_body_exercice5_delta.txt"

# Variables
OPTIMIZED_URL="http://localhost:8081/books?page=$PAGE&size=$SIZE"
UPDATE_URL="http://localhost:8081/books"
DELTA_URL="http://localhost:8081/books/delta"

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

# Récupérer le timestamp JUSTE AVANT la modification
# C'est ce timestamp qui sera utilisé pour Delta
timestamp_before_modification=$(date +%s)000

echo "  Timestamp capturé (avant modification) : $timestamp_before_modification"
echo ""

# ========================================
# Test 1 : Récupérer les livres
# ========================================
echo "  Récupération des livres (GET /books)"
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


# ========================================
# Test 2 : Modifier un seul livre
# ========================================
echo "  Modification d'un livre (PUT /books/1)"
echo "───────────────────────────────────────"

echo "  Modification du livre #1..."
OUTPUT_FILE2="/tmp/response_body_exercice5_put.txt"

# Préarer le JSON pour la requête PUT
PUT_JSON='{"title":"Livre 1 Modifie","author":"Auteur Modifie"}'

response_put=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}" \
  -s \
  -o "$OUTPUT_FILE2" \
  -X PUT "$UPDATE_URL/1" \
  -H "Content-Type: application/json" \
  -d "$PUT_JSON")

# Récupération du code HTTP du PUT
http_code_put=$(echo "$response_put" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_put=$(echo "$response_put" | grep "TIME:" | sed 's/TIME://')

echo "  Code HTTP        : $http_code_put"
echo "  Temps réponse    : ${time_put}s"
echo ""

echo "  Réponse du PUT (livre modifié) :"
echo "  ───────────────────────────────────────"
cat "$OUTPUT_FILE2"
echo ""
echo ""

# ========================================
# Test 3 : Vérifier la modification avec Delta
# ========================================
echo "  Vérification avec Delta (GET /delta?timestamp=TIMESTAMP)"
echo "───────────────────────────────────────"

response_delta=$(curl -w "\nHTTP_CODE:%{http_code}\nTIME:%{time_total}\nSIZE:%{size_download}" \
    -s \
    -o "$OUTPUT_FILE3" \
    "$DELTA_URL?timestamp=$timestamp_before_modification")

# Récupération des métriques
http_code_delta=$(echo "$response_delta" | grep "HTTP_CODE:" | sed 's/HTTP_CODE://')
time_delta=$(echo "$response_delta" | grep "TIME:" | sed 's/TIME://')
size_delta=$(echo "$response_delta" | grep "SIZE:" | sed 's/SIZE://')

# Calculer la taille en KB
if [ -n "$size_delta" ] && [ "$size_delta" -gt 0 ] 2>/dev/null; then
    size_kb_delta=$((size_delta / 1024))
else
    size_kb_delta=0
fi

echo "  Code HTTP        : $http_code_delta"
echo "  Temps réponse    : ${time_delta}s"
echo "  Taille réponse   : $size_delta bytes ($size_kb_delta KB)"
echo "  URL utilisée     : $DELTA_URL?timestamp=$timestamp_before_modification"
echo ""

# Debug: Afficher les détails si la réponse est vide
if [ ! -s "$OUTPUT_FILE3" ]; then
    echo "  ATTENTION: La réponse Delta est vide ou le fichier n'existe pas!"
    echo "  Contenu du fichier : $(cat $OUTPUT_FILE3 2>&1)"
    echo ""
fi

# ========================================
# Comparaison et aperçus
# ========================================
echo " Comparaison des requêtes :"
echo "───────────────────────────────────────"
echo "  1ère requête (GET /books)  : $size_first bytes ($size_kb_first KB)"
echo "  3ème requête (GET /books/delta)  : $size_delta bytes ($size_kb_delta KB)"

if [ "$size_first" -gt 0 ] && [ "$size_delta" -gt 0 ]; then
    reduction=$((100 * (size_first - size_delta) / size_first))
    echo "  Réduction                 : $reduction %"
fi

echo ""

echo " Aperçu de la 1ère requête (GET /books) :"
echo "───────────────────────────────────────"
head -c 500 "$OUTPUT_FILE1"
echo ""
echo "..."
echo ""

echo " Réponse Delta (livres modifiés) :"
echo "───────────────────────────────────────"
cat "$OUTPUT_FILE3"
echo ""
echo ""

# Nettoyage
rm -f "$OUTPUT_FILE1" "$OUTPUT_FILE2" "$OUTPUT_FILE3"

echo "=========================================="
echo "  Test terminé"
echo "=========================================="

