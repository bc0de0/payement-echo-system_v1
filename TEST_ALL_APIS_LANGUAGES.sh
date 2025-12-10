#!/bin/bash

# Comprehensive test script for all APIs with different languages
# Usage: ./TEST_ALL_APIS_LANGUAGES.sh [baseUrl]

BASE_URL="${1:-http://localhost:8080}"
TEST_ID="00000000-0000-0000-0000-000000000000"

echo "🧪 Testing All APIs with Different Languages"
echo "=============================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

test_api() {
    local lang=$1
    local lang_name=$2
    local endpoint=$3
    local method=${4:-GET}
    local data=${5:-""}
    
    echo -e "${YELLOW}Testing: $endpoint with $lang_name ($lang)${NC}"
    
    if [ "$method" = "GET" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -H "Accept-Language: $lang" "$BASE_URL$endpoint")
    elif [ "$method" = "POST" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -X POST -H "Accept-Language: $lang" -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint")
    elif [ "$method" = "DELETE" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE -H "Accept-Language: $lang" "$BASE_URL$endpoint")
    fi
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    # Extract error and message
    ERROR=$(echo "$BODY" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('error', ''))" 2>/dev/null || echo "")
    MESSAGE=$(echo "$BODY" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('message', ''))" 2>/dev/null || echo "")
    
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "204" ]; then
        echo -e "   ${GREEN}✓${NC} Status: $HTTP_CODE"
    else
        echo -e "   ${RED}✗${NC} Status: $HTTP_CODE"
        if [ -n "$ERROR" ]; then
            echo "   Error: $ERROR"
        fi
        if [ -n "$MESSAGE" ]; then
            echo "   Message: ${MESSAGE:0:80}..."
        fi
    fi
    echo ""
}

# Test languages
LANGUAGES=("hi:Hindi" "en:English" "ta:Tamil" "ru:Russian" "bn:Bengali" "te:Telugu" "kn:Kannada" "es:Spanish")

echo "1. Testing Payment APIs"
echo "----------------------"

for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    echo ""
    echo "=== $lang_name ($lang) ==="
    
    # GET Payment by invalid ID (should return 404 with localized message)
    test_api "$lang" "$lang_name" "/api/v1/payments/$TEST_ID" "GET"
    
    # GET All Payments
    test_api "$lang" "$lang_name" "/api/v1/payments" "GET"
done

echo ""
echo "2. Testing Creditor APIs"
echo "------------------------"

for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    echo ""
    echo "=== $lang_name ($lang) ==="
    
    # GET Creditor by invalid ID
    test_api "$lang" "$lang_name" "/api/v1/creditors/$TEST_ID" "GET"
    
    # GET All Creditors
    test_api "$lang" "$lang_name" "/api/v1/creditors" "GET"
done

echo ""
echo "3. Testing Debtor APIs"
echo "----------------------"

for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    echo ""
    echo "=== $lang_name ($lang) ==="
    
    # GET Debtor by invalid ID
    test_api "$lang" "$lang_name" "/api/v1/debtors/$TEST_ID" "GET"
    
    # GET All Debtors
    test_api "$lang" "$lang_name" "/api/v1/debtors" "GET"
done

echo ""
echo "4. Testing POST APIs with Validation Errors"
echo "--------------------------------------------"

for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    echo ""
    echo "=== $lang_name ($lang) ==="
    
    # POST Payment with invalid data (should return 400 with localized validation errors)
    test_api "$lang" "$lang_name" "/api/v1/payments" "POST" '{"amount": -100, "currency": "INVALID", "status": "INVALID"}'
    
    # POST Creditor with invalid data
    test_api "$lang" "$lang_name" "/api/v1/creditors" "POST" '{"name": "", "accountNumber": "", "bankCode": ""}'
done

echo ""
echo "✅ Testing complete!"
echo ""
echo "Summary:"
echo "- Check if error messages are in the correct language"
echo "- Check if validation errors are localized"
echo "- Verify HTTP status codes are correct"
