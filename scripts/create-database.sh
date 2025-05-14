#!/bin/bash

# Database configuration
DB_NAME="ecomplain"
DB_USER="postgres"
DB_PASSWORD="postgres"
DB_HOST="localhost"
DB_PORT="5432"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Checking if database $DB_NAME exists...${NC}"

# Check if the database exists
if PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -lqt | cut -d \| -f 1 | grep -qw $DB_NAME; then
    echo -e "${GREEN}Database $DB_NAME already exists.${NC}"
else
    echo -e "${YELLOW}Database $DB_NAME does not exist. Creating...${NC}"
    
    # Create the database
    if PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -c "CREATE DATABASE $DB_NAME;"; then
        echo -e "${GREEN}Database $DB_NAME created successfully.${NC}"
    else
        echo -e "${RED}Failed to create database $DB_NAME.${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}Database setup completed successfully.${NC}"
exit 0
