#!/bin/bash

# ========================================
# SecondHand Marketplace Launcher (Linux/macOS)
# ========================================

# رنگ‌ها برای خروجی زیباتر
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN} SecondHand Marketplace Launcher${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# بررسی وجود Maven
if ! command -v mvn > /dev/null 2>&1; then
    echo -e "${RED}[ERROR] Maven not found.${NC}"
    echo "Please install Maven first:"
    echo "  - Ubuntu/Debian: sudo apt install maven"
    echo "  - Fedora: sudo dnf install maven"
    echo "  - macOS: brew install maven"
    exit 1
fi

echo -e "${GREEN}[1/2] Starting Backend Server...${NC}"
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

echo -e "${YELLOW}Waiting 8 seconds for backend to initialize...${NC}"
sleep 8

echo -e "${GREEN}[2/2] Starting Frontend Application...${NC}"
cd frontend
mvn javafx:run &
FRONTEND_PID=$!
cd ..

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN} Both services are starting up.${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "Backend PID: ${YELLOW}$BACKEND_PID${NC}"
echo -e "Frontend PID: ${YELLOW}$FRONTEND_PID${NC}"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop both services.${NC}"

# منتظر می‌ماند تا کاربر Ctrl+C بزند
wait