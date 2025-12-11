#!/bin/bash

# Script to create PR using GitHub CLI
# Requires: gh CLI installed and authenticated

BRANCH="feature/deloitte-adyant-payment-enhancements"
BASE_BRANCH="main"
TITLE="feat: Add comprehensive language support (i18n) for all APIs"
BODY_FILE="PR_DESCRIPTION.md"

echo "Creating Pull Request..."
echo "Branch: $BRANCH"
echo "Base: $BASE_BRANCH"
echo ""

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) is not installed."
    echo "Install it from: https://cli.github.com/"
    echo ""
    echo "Or create PR manually at:"
    echo "https://github.com/bc0de0/payement-echo-system_v1/compare/main...$BRANCH"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo "❌ Not authenticated with GitHub CLI"
    echo "Run: gh auth login"
    exit 1
fi

# Create PR
if [ -f "$BODY_FILE" ]; then
    gh pr create \
        --base "$BASE_BRANCH" \
        --head "$BRANCH" \
        --title "$TITLE" \
        --body-file "$BODY_FILE"
else
    gh pr create \
        --base "$BASE_BRANCH" \
        --head "$BRANCH" \
        --title "$TITLE" \
        --body "Add comprehensive language support (i18n) for all APIs. See PR_DESCRIPTION.md for details."
fi

echo ""
echo "✅ PR created successfully!"
