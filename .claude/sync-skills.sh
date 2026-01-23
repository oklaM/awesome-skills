#!/bin/bash
# Sync skills to marketplace.json

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

MARKETPLACE_JSON="$PROJECT_ROOT/.claude-plugin/marketplace.json"
SKILLS_DIR="$PROJECT_ROOT/skills"

# Array to store skill entries
declare -a SKILL_ENTRIES

# Find all skill directories
for skill_dir in "$SKILLS_DIR"/*/; do
  if [[ -d "$skill_dir" ]]; then
    skill_name=$(basename "$skill_dir")
    skill_md="$skill_dir/SKILL.md"

    # Skip if no SKILL.md
    if [[ ! -f "$skill_md" ]]; then
      continue
    fi

    # Parse YAML frontmatter
    in_frontmatter=false
    description=""
    category=""

    while IFS= read -r line; do
      if [[ "$line" == "---" ]]; then
        if [[ "$in_frontmatter" == false ]]; then
          in_frontmatter=true
          continue
        else
          break
        fi
      fi

      if [[ "$in_frontmatter" == true ]]; then
        if [[ "$line" =~ ^description:[[:space:]]+(.+)$ ]]; then
          description="${BASH_REMATCH[1]}"
          # Remove quotes if present
          description="${description%\"}"
          description="${description#\"}"
        elif [[ "$line" =~ ^category:[[:space:]]+(.+)$ ]]; then
          category="${BASH_REMATCH[1]}"
        fi
      fi
    done < "$skill_md"

    # Default category if not found
    if [[ -z "$category" ]]; then
      category="development"
    fi

    # Default description if not found
    if [[ -z "$description" ]]; then
      description="$skill_name skill"
    fi

    # Add to array
    SKILL_ENTRIES+=("$skill_name|$description|$category")
  fi
done

# Generate marketplace.json
{
  cat << 'EOF'
{
  "name": "awesome-skills",
  "owner": {
    "name": "oklaM",
    "email": "users@noreply.github.com"
  },
  "metadata": {
    "description": "Awesome Claude Code Skills Collection",
    "version": "1.0.0"
  },
  "plugins": [
    {
      "name": "awesome-skills-collection",
      "description": "Collection of useful skills for Claude Code including TDD expert and example templates",
      "source": "./",
      "strict": false,
      "skills": [
EOF

  first=true
  for entry in "${SKILL_ENTRIES[@]}"; do
    IFS='|' read -r name desc category <<< "$entry"

    if [[ "$first" = true ]]; then
      first=false
    else
      echo ","
    fi

    printf '        "./skills/%s"' "$name"
  done

  echo ""
  echo "      ]"
  echo "    }"
  echo "  ]"
  echo "}"
} > "$MARKETPLACE_JSON"

echo "✓ Synced ${#SKILL_ENTRIES[@]} skills to marketplace.json"

# Add the file to staging if it changed
git add "$MARKETPLACE_JSON" 2>/dev/null || true
