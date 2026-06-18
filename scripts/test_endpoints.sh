#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "== Health =="
curl -s "$BASE_URL/api/health" | python3 -m json.tool || true

echo "\n== Dataset ML =="
curl -s "$BASE_URL/api/ml/dataset/pipelines" | python3 -m json.tool || true

echo "\n== Dashboard summary =="
curl -s "$BASE_URL/api/dashboard/summary" | python3 -m json.tool || true

echo "\n== Recent predictions =="
curl -s "$BASE_URL/api/ml/predictions/history" | python3 -m json.tool || true
