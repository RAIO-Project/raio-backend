#!/bin/bash
set -euo pipefail

# Usage:
#   chmod +x new-flyway.sh
#   ./new-flyway.sh "add_user_table"
#   ./new-flyway.sh services/auth "add_user_table"

# ---------------------------
# Input
# ---------------------------
if [ $# -eq 1 ]; then
  SEARCH_ROOT="services"
  RAW_DESC="$1"
elif [ $# -ge 2 ]; then
  SEARCH_ROOT="$1"
  shift
  RAW_DESC="$*"
else
  echo "Usage:"
  echo "  $0 \"add_user_table\""
  echo "  $0 services/auth \"add_user_table\""
  exit 1
fi

if [ ! -d "$SEARCH_ROOT" ]; then
  echo "Error: directory not found: $SEARCH_ROOT"
  exit 1
fi

# ---------------------------
# Find migration directories
# ---------------------------
mapfile -t MIGRATION_BASES < <(find "$SEARCH_ROOT" -type d -path "*/src/main/resources/db/migration")

if [ ${#MIGRATION_BASES[@]} -eq 0 ]; then
  echo "Error: no db/migration directory found under $SEARCH_ROOT"
  exit 1
fi

# ---------------------------
# Select when multiple found
# ---------------------------
if [ ${#MIGRATION_BASES[@]} -gt 1 ]; then
  echo "Multiple migration directories found:"
  for i in "${!MIGRATION_BASES[@]}"; do
    echo "  [$i] ${MIGRATION_BASES[$i]}"
  done
  echo -n "Select target index: "
  read -r IDX

  TARGET_BASE="${MIGRATION_BASES[$IDX]}"
else
  TARGET_BASE="${MIGRATION_BASES[0]}"
fi

# ---------------------------
# Version folder
# ---------------------------
VERSION_DIR="${TARGET_BASE}/v1"
mkdir -p "$VERSION_DIR"

# ---------------------------
# Sanitize description
# ---------------------------
DESC="$(echo "$RAW_DESC" \
  | tr '[:upper:]' '[:lower:]' \
  | sed -E 's/[[:space:]]+/_/g' \
  | sed -E 's/[^a-z0-9_]+/_/g' \
  | sed -E 's/_+/_/g' \
  | sed -E 's/^_+|_+$//g')"

if [ -z "$DESC" ]; then
  echo "Error: invalid description"
  exit 1
fi

# ---------------------------
# Timestamp
# ---------------------------
TS="$(date +%Y%m%d%H%M%S)"

FILE="${VERSION_DIR}/V${TS}__${DESC}.sql"

if [ -e "$FILE" ]; then
  echo "Error: file already exists"
  exit 1
fi

# ---------------------------
# Create file
# ---------------------------
cat > "$FILE" <<'SQL'
-- Flyway Migration
-- Write your SQL below

SQL

echo "Created: $FILE"
